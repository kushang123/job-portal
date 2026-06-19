package com.kushang.jobportal.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String location;
    private Double salary;
    private String skillsRequired;
    private LocalDate deadline;
}