package com.example.JobApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.JobApp.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}
