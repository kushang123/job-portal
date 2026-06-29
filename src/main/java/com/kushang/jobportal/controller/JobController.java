package com.kushang.jobportal.controller;
import java.util.*;
import com.kushang.jobportal.dto.JobRequest;
import com.kushang.jobportal.dto.JobResponse;
import com.kushang.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job", description = "Endpoints for posting, browsing, and managing job listings")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(
            summary = "Create a new job listing",
            description = "Allows an authenticated COMPANY to post a new job. The company must have a profile created before posting."
    )
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all open jobs",
            description = "Returns all currently open job listings. Publicly accessible, used by candidates browsing the platform."
    )
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllOpenJobs() {
        List<JobResponse> jobs = jobService.getAllOpenJobs();
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }
    @Operation(
            summary = "Get job by ID",
            description = "Returns the details of a single job listing by its ID"
    )

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        JobResponse response = jobService.getJobById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(
            summary = "Get jobs by company name",
            description = "Returns all job listings posted by a company, filtered by company name"
    )

    @GetMapping("/company")
    public ResponseEntity<List<JobResponse>> getJobsByCompanyName(@RequestParam String name) {
        List<JobResponse> jobs = jobService.getJobsByCompanyName(name);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }
    @Operation(
            summary = "Update a job listing",
            description = "Allows the owning COMPANY to update the details of an existing job listing"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @RequestBody JobRequest request) {
        JobResponse response = jobService.updateJob(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Close a job listing",
            description = "Marks a job as closed, preventing further applications. Only the owning COMPANY can perform this action."
    )

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> closeJob(@PathVariable Long id) {
        JobResponse response = jobService.closeJob(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}