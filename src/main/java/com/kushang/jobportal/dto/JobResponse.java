package com.kushang.jobportal.dto;
import com.kushang.jobportal.entity.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Double salary;
    private String skillsRequired;
    private JobStatus status;
    private LocalDate deadline;
    private String companyName;
    private LocalDateTime createdAt;
}

