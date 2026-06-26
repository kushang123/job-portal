package com.kushang.jobportal.service;
import java.util.*;
import java.util.stream.Collectors;
import com.kushang.jobportal.dto.ApplicationRequest;
import com.kushang.jobportal.dto.ApplicationResponse;
import com.kushang.jobportal.entity.Application;
import com.kushang.jobportal.entity.Job;
import com.kushang.jobportal.entity.JobStatus;
import com.kushang.jobportal.entity.User;
import com.kushang.jobportal.entity.Company;
import com.kushang.jobportal.dto.CompanyApplicationResponse;
import com.kushang.jobportal.entity.ApplicationStatus;
import com.kushang.jobportal.exception.DuplicateApplicationException;
import com.kushang.jobportal.exception.JobClosedException;
import com.kushang.jobportal.exception.JobNotFoundException;
import com.kushang.jobportal.repository.ApplicationRepository;
import com.kushang.jobportal.repository.CompanyRepository;
import com.kushang.jobportal.repository.JobRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.UUID;
import com.kushang.jobportal.exception.InvalidFileTypeException;
import com.kushang.jobportal.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.kushang.jobportal.exception.ApplicationNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import com.kushang.jobportal.exception.InvalidStatusTransitionException;


@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final S3Client s3Client;


    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository, CompanyRepository companyRepository, S3Client s3Client) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.s3Client = s3Client;
    }

    private User getLoggedInCandidate() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in candidate not found")); // mirrors getLoggedInCompany() pattern
    }



    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public ApplicationResponse applyToJob(ApplicationRequest request, MultipartFile resumeFile) {
        User candidate = getLoggedInCandidate();

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + request.getJobId()));

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new JobClosedException("Cannot apply to a closed job");
        }

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new DuplicateApplicationException("You have already applied to this job");
        }

        String resumeUrl = uploadResumeToS3(resumeFile, candidate.getId());

        Application application = new Application();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setResumeUrl(resumeUrl);

        Application saved = applicationRepository.save(application);

        return mapToResponse(saved);
    }

    private String uploadResumeToS3(MultipartFile file, Long candidateId) {
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("Resume file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new InvalidFileTypeException("Only PDF files are allowed for resumes");
        }

        String key = "resumes/" + candidateId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload resume to S3", e);
        }

        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    private ApplicationResponse mapToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setCompanyName(application.getJob().getCompany().getName());
        response.setStatus(application.getStatus());
        response.setResumeUrl(application.getResumeUrl());
        response.setAppliedAt(application.getAppliedAt());
        return response;
    }

    public List<ApplicationResponse> getMyApplications() {
        User candidate = getLoggedInCandidate();

        List<Application> applications = applicationRepository.findByCandidate(candidate);

        return applications.stream()
                .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Company getLoggedInCompany() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
        return companyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Company profile not found for this user"));
    }

    public List<CompanyApplicationResponse> getApplicationsForJob(Long jobId) {
        Company company = getLoggedInCompany();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + jobId));

        if (!job.getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You do not have access to applications for this job");
        }

        List<Application> applications = applicationRepository.findByJob(job);

        return applications.stream()
                .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    private CompanyApplicationResponse mapToCompanyResponse(Application application) {
        CompanyApplicationResponse response = new CompanyApplicationResponse();
        response.setId(application.getId());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setCandidateId(application.getCandidate().getId());
        response.setCandidateName(application.getCandidate().getName());
        response.setCandidateEmail(application.getCandidate().getEmail());
        response.setStatus(application.getStatus());
        response.setResumeUrl(application.getResumeUrl());
        response.setAppliedAt(application.getAppliedAt());
        return response;
    }
    private static final Map<ApplicationStatus, Set<ApplicationStatus>> VALID_TRANSITIONS = Map.of(
            ApplicationStatus.APPLIED, Set.of(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED),
            ApplicationStatus.SHORTLISTED, Set.of(ApplicationStatus.REJECTED),
            ApplicationStatus.REJECTED, Set.of()
    );

    public CompanyApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
        Company company = getLoggedInCompany();

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));

        if (!application.getJob().getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You do not have access to this application");
        }

        ApplicationStatus currentStatus = application.getStatus();
        Set<ApplicationStatus> allowedNextStatuses = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());

        if (!allowedNextStatuses.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status from " + currentStatus + " to " + newStatus);
        }

        application.setStatus(newStatus);
        Application updated = applicationRepository.save(application);

        return mapToCompanyResponse(updated);
    }


}