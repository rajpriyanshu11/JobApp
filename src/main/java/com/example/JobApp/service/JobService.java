package com.example.JobApp.service;

import com.example.JobApp.model.Job;
import com.example.JobApp.model.JobRequestDTO;
import com.example.JobApp.model.JobResponseDTO;
import com.example.JobApp.repository.repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import com.example.JobApp.model.User;
import com.example.JobApp.repository.UserRepo;


import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class JobService {

    @Autowired
    private repo repo;

    @Autowired
    private UserRepo userRepo;


    public JobResponseDTO addJob(JobRequestDTO jobRequestDTO, String username){

        Job job=new Job();
        job.setCompany(jobRequestDTO.getCompany());
        job.setJobTitle(jobRequestDTO.getJobTitle());
        job.setJobType(jobRequestDTO.getJobType());
        job.setJobDesc(jobRequestDTO.getJobDesc());
        job.setLocation(jobRequestDTO.getLocation());
        job.setTechStack(jobRequestDTO.getTechStack());
        job.setMinSalary(jobRequestDTO.getMinSalary());
        job.setMaxSalary(jobRequestDTO.getMaxSalary());
        job.setMinExp(jobRequestDTO.getMinExp());
        job.setMaxExp(jobRequestDTO.getMaxExp());
        job.setWorkMode(jobRequestDTO.getWorkMode());


        User owner = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        job.setUser(owner);

        Job saved = repo.save(job);
        return new JobResponseDTO(saved);
    }

    public List<Job> getAllJobs(String username, Collection<? extends GrantedAuthority> authorities) {
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                authorities.contains(new SimpleGrantedAuthority("ROLE_HR"))) {
            return repo.findAll(); // HR/Admin see everything
        } else {
            return repo.findByUserUsername(username);
        }
    }


    public Object getJobById(Long id, Authentication auth) {
        Job job = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Authorization logic
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new JobResponseDTO.JobAdminDTO(job);
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR"))) {
            return new JobResponseDTO.JobHRDTO(job);
        } else {
            if (!job.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Unauthorized: cannot access another user’s job");
            }
            return new JobResponseDTO.JobPublicDTO(job);
        }
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
