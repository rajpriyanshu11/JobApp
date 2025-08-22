package com.example.JobApp.controller;

import com.example.JobApp.model.Job;
import com.example.JobApp.model.JobRequestDTO;
import com.example.JobApp.model.JobResponseDTO;
import com.example.JobApp.model.User;
import com.example.JobApp.repository.UserRepo;
import com.example.JobApp.response.ApiResponse;
import com.example.JobApp.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("job")
    public ResponseEntity<ApiResponse<Page<?>>> getJobs(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth
    ) {
        Page<Job> jobs = jobService.getFilteredJobs(
                company,
                location,
                jobType,
                keyword,
                page, size,
                auth.getName(),
                auth.getAuthorities()
        );

        Page<?> jobDTOs;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            jobDTOs = jobs.map(JobResponseDTO.JobAdminDTO::new);
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR"))) {
            jobDTOs = jobs.map(JobResponseDTO.JobHRDTO::new);
        } else {
            jobDTOs = jobs.map(JobResponseDTO.JobPublicDTO::new);
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Jobs fetched successfully", true, jobDTOs));
    }



    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<ApiResponse<?>> getJobById(@PathVariable Long id, Authentication auth) {
        Object jobDTO = jobService.getJobById(id, auth);

        if (jobDTO == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(
                    404,
                    "Job not found",
                    false,
                    null
            ));
        }


            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "Job fetched successfully",
                    true,
                    jobDTO
            ));
        }


    @PostMapping("/job")
    public ResponseEntity<ApiResponse<JobResponseDTO>> addJob(
            @Valid @RequestBody JobRequestDTO jobRequest,
            Authentication auth
    ) { //when you want to receive data , Requestbody annot. is used
        JobResponseDTO createdJob = jobService.addJob(jobRequest, auth.getName());
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(201, "Job added successfully", true, createdJob));
    }


    @PutMapping("/job/{id}")
    public ResponseEntity<ApiResponse<Job>> updateJob(@PathVariable Long id, @RequestBody Job job) {
        Job updatedJob = jobService.updateJob(id, job);

        if (updatedJob != null) {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            200,
                            "Job updated successfully",
                            true,
                            updatedJob
                    )
            );
        } else {
            return ResponseEntity.status(404).body(
                    new ApiResponse<>(
                            404,
                            "Job not found",
                            false,
                            null
                    )
            );
        }
    }

    @PatchMapping("/job/{id}")
    public ResponseEntity<ApiResponse<?>> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequestDTO jobRequest,
            Authentication auth
    ) {
        try {
            Object updatedJob = jobService.updateJob(id, jobRequest, auth);
            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "Job updated successfully",
                    true,
                    updatedJob
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(new ApiResponse<>(
                    403,
                    e.getMessage(),
                    false,
                    null
            ));
        }
    }


    @DeleteMapping("/job/{id}")
    public ResponseEntity<ApiResponse<?>> deleteJob(
            @PathVariable Long id,
            Authentication auth
    ) {
        try {
            jobService.deleteJob(id, auth);
            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "Job deleted successfully",
                    true,
                    null
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(new ApiResponse<>(
                    403,
                    e.getMessage(),
                    false,
                    null
            ));
        }
    }


}
