package com.kushang.jobportal.service;

import com.kushang.jobportal.dto.CompanyRequest;
import com.kushang.jobportal.dto.CompanyResponse;
import com.kushang.jobportal.entity.Company;
import com.kushang.jobportal.entity.User;
import com.kushang.jobportal.exception.CompanyAlreadyExistsException;
import com.kushang.jobportal.exception.ResourceNotFoundException;
import com.kushang.jobportal.repository.CompanyRepository;
import com.kushang.jobportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import com.kushang.jobportal.exception.CompanyNotFoundException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
                          UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public CompanyResponse createCompanyProfile(CompanyRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (companyRepository.findByUser(user).isPresent()) {
            throw new CompanyAlreadyExistsException("Company profile already exists for this account");
        }

        Company company = new Company();
        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setLocation(request.getLocation());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setUser(user);

        Company saved = companyRepository.save(company);

        return mapToResponse(saved);
    }

    private CompanyResponse mapToResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getIndustry(),
                company.getLocation(),
                company.getDescription(),
                company.getWebsite(),
                company.getCreatedAt()
        );
    }
    public CompanyResponse updateCompanyProfile(CompanyRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new CompanyNotFoundException(
                        "Please create your company profile first"));

        company.setName(request.getName());
        company.setIndustry(request.getIndustry());
        company.setLocation(request.getLocation());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());

        Company updatedCompany = companyRepository.save(company);

        return mapToResponse(updatedCompany);
    }
}