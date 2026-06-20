package com.kushang.jobportal.controller;
import java.util.*;
import com.kushang.jobportal.dto.JobRequest;
import com.kushang.jobportal.dto.JobResponse;
import com.kushang.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllOpenJobs() {
        List<JobResponse> jobs = jobService.getAllOpenJobs();
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        JobResponse response = jobService.getJobById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/company")
    public ResponseEntity<List<JobResponse>> getJobsByCompanyName(@RequestParam String name) {
        List<JobResponse> jobs = jobService.getJobsByCompanyName(name);
        return new ResponseEntity<>(jobs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @RequestBody JobRequest request) {
        JobResponse response = jobService.updateJob(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponse> closeJob(@PathVariable Long id) {
        JobResponse response = jobService.closeJob(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}