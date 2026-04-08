package com.architecturedays.day008.repository;

import com.architecturedays.day008.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobStatusRepository extends JpaRepository<JobStatus, String> {
}
