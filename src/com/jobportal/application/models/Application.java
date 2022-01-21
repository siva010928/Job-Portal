package com.jobportal.application.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Application {
    private ArrayList<String> answers;
    private JobSeeker jobSeeker;
    private Job job;
    private String resume,applicationStatus="ACTIVE";
    private LocalDateTime appliedAt;
    private  Integer id;

    
    public Application(Integer id,JobSeeker jobSeeker, Job job, String resume, String applicationStatus, LocalDateTime appliedAt) {
        this.jobSeeker = jobSeeker;
        this.job = job;
        this.resume = resume;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
        this.id = id;
    }
    public Application(ArrayList<String> answers,JobSeeker jobSeeker, Job job, String resume, String applicationStatus, LocalDateTime appliedAt, Integer id) {
        this.answers = answers;
        this.jobSeeker = jobSeeker;
        this.job = job;
        this.resume = resume;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
        this.id = id;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public void setJobSeeker(JobSeeker jobSeeker) {
        this.jobSeeker = jobSeeker;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public Job getJob() {
        return job;
    }

    public String getResume() {
        return resume;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}
