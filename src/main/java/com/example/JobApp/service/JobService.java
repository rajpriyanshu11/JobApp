package com.example.JobApp.service;

import com.example.JobApp.model.Job;
import com.example.JobApp.model.JobRequestDTO;
import com.example.JobApp.model.JobResponseDTO;
import com.example.JobApp.repository.repo;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    public JobResponseDTO addJob(JobRequestDTO jobRequestDTO, String username) {

        Job job = new Job();
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
            JobResponseDTO.JobPublicDTO dto = new JobResponseDTO.JobPublicDTO(job);
            System.out.println("Returning DTO: " + dto);
            return dto;
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

    public Object updateJob(Long id, JobRequestDTO jobRequest, @NotNull Authentication auth) {
        Job job = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) { //if the role is user then it will specifically be checked that the user id matches with the owner of the jobs id or not .
            if (!job.getUser().getId().equals(currentUser.getId())) {                       //ADMIN and HR are automatically allowed to update any job
                throw new RuntimeException("Unauthorized: cannot update another user's job");
            }
        }

        if (jobRequest.getCompany() != null) job.setCompany(jobRequest.getCompany());
        if (jobRequest.getJobTitle() != null) job.setJobTitle(jobRequest.getJobTitle());
        if (jobRequest.getJobType() != null) job.setJobType(jobRequest.getJobType());
        if (jobRequest.getJobDesc() != null) job.setJobDesc(jobRequest.getJobDesc());
        if (jobRequest.getLocation() != null) job.setLocation(jobRequest.getLocation());
        if (jobRequest.getTechStack() != null) job.setTechStack(jobRequest.getTechStack());
        if (jobRequest.getMinSalary() != null) job.setMinSalary(jobRequest.getMinSalary());
        if (jobRequest.getMaxSalary() != null) job.setMaxSalary(jobRequest.getMaxSalary());
        if (jobRequest.getMinExp() != null) job.setMinExp(jobRequest.getMinExp());
        if (jobRequest.getMaxExp() != null) job.setMaxExp(jobRequest.getMaxExp());
        if (jobRequest.getWorkMode() != null) job.setWorkMode(jobRequest.getWorkMode());

        Job updatedJob = repo.save(job);

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return new JobResponseDTO.JobAdminDTO(updatedJob);
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR"))) {
            return new JobResponseDTO.JobHRDTO(updatedJob);
        } else {
            return new JobResponseDTO.JobPublicDTO(updatedJob);
        }
    }

    public void deleteJob(Long id, Authentication auth) {
        Job job = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR")) ||
                job.getUser().getId().equals(currentUser.getId())) {
            repo.delete(job);
        } else {
            throw new RuntimeException("Forbidden: You cannot delete this job");
        }
    }


    public Page<Job> getFilteredJobs(
            String company,
            String location,
            String jobType,
            String keyword,
            int page,
            int size,
            String username,
            Collection<? extends GrantedAuthority> authorities
    ) {
        Pageable pageable = PageRequest.of(page, size);

        if (authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return repo.findByUserUsername(username, pageable);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return repo.findByJobDescContainingIgnoreCase(keyword, pageable);
        } else if (company != null && location != null && jobType != null) {
            return repo.findByCompanyIgnoreCaseAndLocationIgnoreCaseAndJobTypeIgnoreCase(
                    company, location, jobType, pageable);
        } else if (location != null) {
            return repo.findByLocationIgnoreCase(location, pageable);
        } else if (jobType != null) {
            return repo.findByJobTypeIgnoreCase(jobType, pageable);
        } else if (company != null) {
            return repo.findByCompanyIgnoreCase(company, pageable);
        }

        return repo.findAll(pageable);
    }
}

