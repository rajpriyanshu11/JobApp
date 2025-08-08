package com.example.JobApp.service;

import com.example.JobApp.model.Job;
import com.example.JobApp.repository.repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class JobService {

    @Autowired
    private repo repo;


    public Job addJob(Job job){
        return repo.save(job);
    }

    public List<Job> getAllJobs(){
        return repo.findAll();
    }

    public boolean getJobById(Long id) {
        if(repo.existsById(id)){
            repo.findById(id);
            return true;
        }
        return false;
    }

    public Job updateJob(Long id, Job jobDetails) {
        Optional<Job> existingJob = repo.findById(id);
        if (existingJob.isPresent()) {
            Job job = existingJob.get();
            job.setCompany(jobDetails.getCompany());
            job.setJobTitle(jobDetails.getJobTitle());
            job.setJobType(jobDetails.getJobType());
            job.setJobDesc(jobDetails.getJobDesc());
            job.setLocation(jobDetails.getLocation());
            job.setTechStack(jobDetails.getTechStack());
            job.setMaxSalary(jobDetails.getMaxSalary());
            job.setMinSalary(jobDetails.getMinSalary());
            job.setMaxExp(jobDetails.getMaxExp());
            job.setMinExp(jobDetails.getMinExp());
            job.setWorkMode(jobDetails.getWorkMode());
            return repo.save(job);
        }
        return null;
    }

    public boolean deleteJob(Long id) {
        if(repo.existsById(id)){
            repo.deleteById(id);
            return true;
        }
        return false;
    }


    public List<Job> searchByKeyword(String keyword) {
        List<Job> result = repo.findByJobDescContainingIgnoreCase(keyword);
        System.out.println("Found jobs: " + result.size());
        return result;
    }
}
