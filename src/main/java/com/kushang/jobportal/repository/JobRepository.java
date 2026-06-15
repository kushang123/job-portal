package com.kushang.jobportal.repository;

import com.kushang.jobportal.entity.Company;
import com.kushang.jobportal.entity.Job;
import com.kushang.jobportal.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCompany(Company company);
    List<Job> findByStatus(JobStatus status);
    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findByLocationContainingIgnoreCase(String location);
}