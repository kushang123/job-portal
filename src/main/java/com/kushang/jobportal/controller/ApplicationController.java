package com.kushang.jobportal.controller;
import java.util.*;
import com.kushang.jobportal.dto.ApplicationRequest;
import com.kushang.jobportal.dto.ApplicationResponse;
import com.kushang.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.kushang.jobportal.dto.UpdateApplicationStatusRequest;
import com.kushang.jobportal.dto.CompanyApplicationResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/applications")
@Tag(name = "Application", description = "Endpoints for candidates applying to jobs and companies managing applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @Operation(
            summary = "Apply to a job with resume upload",
            description = "Allows an authenticated CANDIDATE to apply to a job by uploading a resume (PDF only). " +
                    "The file is validated and stored in AWS S3 under a unique key, and the resulting resume URL is saved with the application."
    )

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationResponse> applyToJob(
            @RequestParam("jobId") Long jobId,
            @RequestParam("resumeFile") MultipartFile resumeFile) {

        ApplicationRequest request = new ApplicationRequest();
        request.setJobId(jobId);

        ApplicationResponse response = applicationService.applyToJob(request, resumeFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @Operation(
            summary = "Get my applications",
            description = "Returns all job applications submitted by the currently authenticated CANDIDATE"
    )
    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        List<ApplicationResponse> responses = applicationService.getMyApplications();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    @Operation(
            summary = "Get applications for a job",
            description = "Returns all candidate applications submitted for a specific job. Restricted to the COMPANY that owns the job."
    )
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<CompanyApplicationResponse>> getApplicationsForJob(@PathVariable Long jobId) {
        List<CompanyApplicationResponse> responses = applicationService.getApplicationsForJob(jobId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(
            summary = "Update application status",
            description = "Allows the owning COMPANY to update a candidate's application status (e.g., SHORTLISTED, REJECTED). " +
                    "Status transitions are validated against allowed state changes; REJECTED is terminal."
    )

    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        CompanyApplicationResponse response = applicationService.updateApplicationStatus(applicationId, request.getStatus());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}