package com.jobportal.application.models;


import java.time.LocalDateTime;

public class Review {
    private JobSeeker user;
    private Company company;
    private LocalDateTime reviewedAt;
    private String review,pros,cons,jobTitle,jobStatus,location;

    public Review(JobSeeker user, Company company, LocalDateTime reviewedAt, String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        this.user = user;
        this.company = company;
        this.reviewedAt = reviewedAt;
        this.review = review;
        this.pros = pros;
        this.cons = cons;
        this.jobTitle = jobTitle;
        this.jobStatus = jobStatus;
        this.location = location;
    }

    public JobSeeker getUser() {
        return user;
    }

    public void setUser(JobSeeker user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPros() {
        return pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getCons() {
        return cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

