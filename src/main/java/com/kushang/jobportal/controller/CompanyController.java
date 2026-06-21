package com.kushang.jobportal.controller;

import com.kushang.jobportal.dto.CompanyRequest;
import com.kushang.jobportal.dto.CompanyResponse;
import com.kushang.jobportal.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompanyProfile(
            @RequestBody CompanyRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        CompanyResponse response = companyService.createCompanyProfile(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyResponse> updateCompanyProfile(@RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.updateCompanyProfile(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}