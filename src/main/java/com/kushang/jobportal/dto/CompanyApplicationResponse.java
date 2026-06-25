package com.kushang.jobportal.dto;

import com.kushang.jobportal.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private ApplicationStatus status;
    private String resumeUrl;
    private LocalDateTime appliedAt;
}