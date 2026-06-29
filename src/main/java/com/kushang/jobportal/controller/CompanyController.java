package com.kushang.jobportal.controller;
import com.kushang.jobportal.dto.CompanyRequest;
import com.kushang.jobportal.dto.CompanyResponse;
import com.kushang.jobportal.service.CompanyService;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company", description = "Endpoints for managing company profiles")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }
    @Operation(
            summary = "Create company profile",
            description = "Creates a company profile for the currently authenticated COMPANY user. Must be called before the company can post jobs."
    )

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompanyProfile(
            @RequestBody CompanyRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        CompanyResponse response = companyService.createCompanyProfile(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update company profile",
            description = "Updates the profile details of the currently authenticated COMPANY user"
    )

    @PutMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyResponse> updateCompanyProfile(@RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.updateCompanyProfile(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}