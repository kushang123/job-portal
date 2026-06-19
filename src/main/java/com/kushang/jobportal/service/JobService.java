package com.kushang.jobportal.service;

import com.kushang.jobportal.dto.JobRequest;
import com.kushang.jobportal.dto.JobResponse;
import com.kushang.jobportal.entity.Company;
import com.kushang.jobportal.entity.Job;
import com.kushang.jobportal.entity.User;
import com.kushang.jobportal.exception.CompanyNotFoundException;
import com.kushang.jobportal.repository.CompanyRepository;
import com.kushang.jobportal.repository.JobRepository;
import com.kushang.jobportal.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository,
                      CompanyRepository companyRepository,
                      UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public JobResponse createJob(JobRequest request) {


        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Please create your company profile before posting a job"));


        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setDeadline(request.getDeadline());
        job.setCompany(company);


        Job savedJob = jobRepository.save(job);

        return mapToResponse(savedJob);
    }

    private JobResponse mapToResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setSalary(job.getSalary());
        response.setSkillsRequired(job.getSkillsRequired());
        response.setStatus(job.getStatus());
        response.setDeadline(job.getDeadline());
        response.setCompanyName(job.getCompany().getName());
        response.setCreatedAt(job.getCreatedAt());
        return response;
    }
}