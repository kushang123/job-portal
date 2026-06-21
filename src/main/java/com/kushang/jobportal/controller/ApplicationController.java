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


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> applyToJob(@Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.applyToJob(request);
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