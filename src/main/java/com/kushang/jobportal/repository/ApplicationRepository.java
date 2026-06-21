package com.kushang.jobportal.repository;

import com.kushang.jobportal.entity.Application;
import com.kushang.jobportal.entity.Job;
import com.kushang.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kushang.jobportal.entity.ApplicationStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidate(User candidate);
    List<Application> findByJob(Job job);
    Optional<Application> findByCandidateAndJob(User candidate, Job job);
    Boolean existsByCandidateAndJob(User candidate, Job job);
    List<Application> findByCandidateAndStatus(User candidate, ApplicationStatus status);
}