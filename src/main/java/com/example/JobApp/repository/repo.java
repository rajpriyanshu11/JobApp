package com.example.JobApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.JobApp.model.Job;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface repo extends JpaRepository<Job, Long> { //Long is the type of primary key

    List<Job> findByJobDescContainingIgnoreCase(String jobDesc);

//    static void updateJob(Job job) {
//    }
//
//    static void addJob(Job job) {
//    }
}
