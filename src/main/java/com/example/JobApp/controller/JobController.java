package com.example.JobApp.controller;

import com.example.JobApp.model.Job;
import com.example.JobApp.response.ApiResponse;
import com.example.JobApp.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("job")
    public ResponseEntity<ApiResponse<List<Job>>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(new ApiResponse<>(200, "Jobs fetched successfully", true, jobs));
    }

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping("job/{id}")
    public ResponseEntity<ApiResponse> getJobById(@PathVariable Long id){  //@PathVariable is used to extract values from the URL path and bind them to method parameters in a controller.
        boolean getJobById = jobService.getJobById(id);
        if (getJobById) {
            return ResponseEntity.ok(new ApiResponse(
                    200,
                    "Job fetched successfully",
                    true,
                    getJobById
            ));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse(
                    404,
                    "Job not found",
                    false,
                    getJobById
            ));
        }
    }

    @GetMapping("job/keyword/{keyword}")
    public ResponseEntity<ApiResponse> searchByKeyword(@PathVariable String keyword) {
        List<Job> jobs = jobService.searchByKeyword(keyword);
        return ResponseEntity.ok(new ApiResponse<>(200, "Jobs fetched successfully", true, jobs));
    }

    @DeleteMapping("job/{id}")
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

    @PostMapping("job")
    public ResponseEntity<ApiResponse<Job>> addJob(@RequestBody Job job) { //when you want to receive data , Requestbody annot. is used
        Job savedJob = jobService.addJob(job);
        return ResponseEntity.status(201)
                .body(new ApiResponse<>(201, "Job added successfully", true, savedJob));
    }

    @PutMapping("job/{id}")
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
