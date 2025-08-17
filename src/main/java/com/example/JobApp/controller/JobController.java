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
    public ResponseEntity<ApiResponse<List<?>>> getAllJobs(Authentication auth) {
        List<Job> jobs = jobService.getAllJobs(auth.getName(), auth.getAuthorities());

        List<?> jobDTOs;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            jobDTOs = jobs.stream().map(JobResponseDTO.JobAdminDTO::new).toList();
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR"))) {
            jobDTOs = jobs.stream().map(JobResponseDTO.JobHRDTO::new).toList();
        } else {
            jobDTOs = jobs.stream().map(JobResponseDTO.JobPublicDTO::new).toList();
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




    @GetMapping("/job/keyword/{keyword}")
    public ResponseEntity<ApiResponse> searchByKeyword(@PathVariable String keyword) {
        List<Job> jobs = jobService.searchByKeyword(keyword);
        return ResponseEntity.ok(new ApiResponse<>(200, "Jobs fetched successfully", true, jobs));
    }

    @DeleteMapping("/job/{id}")
    public ResponseEntity<ApiResponse> deleteJob(@PathVariable Long id) {
        boolean deleted = jobService.deleteJob(id);

        if (deleted) {
            return ResponseEntity.ok(new ApiResponse(
                    200,
                    "Job deleted successfully",
                    true,
                    null
            ));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse(
                    404,
                    "Job not found",
                    false,
                    null
            ));
        }
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
}
