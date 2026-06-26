package com.kushang.jobportal.controller;
import java.util.*;
import com.kushang.jobportal.dto.ApplicationRequest;
import com.kushang.jobportal.dto.ApplicationResponse;
import com.kushang.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.kushang.jobportal.dto.UpdateApplicationStatusRequest;
import com.kushang.jobportal.dto.CompanyApplicationResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

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

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        List<ApplicationResponse> responses = applicationService.getMyApplications();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<CompanyApplicationResponse>> getApplicationsForJob(@PathVariable Long jobId) {
        List<CompanyApplicationResponse> responses = applicationService.getApplicationsForJob(jobId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyApplicationResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        CompanyApplicationResponse response = applicationService.updateApplicationStatus(applicationId, request.getStatus());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}