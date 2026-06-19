package com.kushang.jobportal.dto;

import lombok.Data;

@Data
public class CompanyRequest {
    private String name;
    private String industry;
    private String location;
    private String description;
    private String website;
}