package com.example.JobApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    private String company;
    private String jobTitle;
    private String jobType;
    private String jobDesc;
    private String location;

    @ElementCollection
    private List<String> techStack; // Store as a separate table

    private Double maxSalary;
    private Double minSalary;
    private Integer maxExp;
    private Integer minExp;
    private String workMode;
}

