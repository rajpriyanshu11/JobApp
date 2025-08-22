package com.example.JobApp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.JobApp.model.Job;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface repo extends JpaRepository<Job, Long> { //Long is the type of primary key

    List<Job> findByJobTitleContainingIgnoreCase(String keyword);

    Page<Job> findByJobDescContainingIgnoreCase(String jobDesc, Pageable pageable);


    Page<Job> findByJobTypeIgnoreCase(String jobType, Pageable pageable);

    Page<Job> findByCompanyIgnoreCase(String company, Pageable pageable);

    Page<Job> findByCompanyIgnoreCaseAndLocationIgnoreCaseAndJobTypeIgnoreCase(
            String company,
            String location,
            String jobType,
            Pageable pageable
    );

    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);


    Page<Job> findByUserUsername(String username,Pageable pageable);

    List<Job> findByUserId(Long userId);


//    static void updateJob(Job job) {
//    }
//
//    static void addJob(Job job) {
//    }
}
