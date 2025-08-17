package com.example.JobApp.model;

import lombok.Data;

import java.util.*;

@Data
public class JobResponseDTO {


    private String company;
    private String jobTitle;
    private String jobType;
    private String jobDesc;
    private String location;
    private List<String> techStack;
    private Double minSalary;
    private Double maxSalary;
    private Integer minExp;
    private Integer maxExp;
    private String workMode;

    public JobResponseDTO(Job job) {

        this.company = job.getCompany();
        this.jobTitle = job.getJobTitle();
        this.jobType = job.getJobType();
        this.jobDesc = job.getJobDesc();
        this.location = job.getLocation();
        this.techStack = job.getTechStack();
        this.minSalary = job.getMinSalary();
        this.maxSalary = job.getMaxSalary();
        this.minExp = job.getMinExp();
        this.maxExp = job.getMaxExp();
        this.workMode = job.getWorkMode();
    }

    @Data
    public static class JobPublicDTO {
        private String company;
        private String jobTitle;
        private String location;

        public JobPublicDTO(Job job) {
            this.company = job.getCompany();
            this.jobTitle = job.getJobTitle();
            this.location = job.getLocation();
        }
    }

    // HR view DTO
    @Data
    public static class JobHRDTO extends JobPublicDTO {
        private String jobDesc;

        public JobHRDTO(Job job) {
            super(job);
            this.jobDesc = job.getJobDesc();
        }
    }

    // Admin view DTO
    @Data
    public static class JobAdminDTO extends JobHRDTO {
        private Double minSalary;
        private Double maxSalary;
        private List<String> techStack;
        private Integer maxExp;
        private Integer minExp;
        private String jobType;

        public JobAdminDTO(Job job) {
            super(job);
            this.minSalary = job.getMinSalary();
            this.maxSalary = job.getMaxSalary();
            this.techStack = job.getTechStack();
            this.jobType = job.getJobType();
            this.maxExp = job.getMaxExp();
            this.minExp = job.getMinExp();
        }
    }

}
