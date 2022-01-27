package com.jobportal.application.models;


import java.time.LocalDateTime;

public class Review {
    private Integer id;
    private JobSeeker user;
    private Company company;
    private LocalDateTime reviewedAt;
    private String review,pros,cons,jobTitle,jobStatus,location;

    //posting a review
    public Review(JobSeeker user, Company company, String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        this.user = user;
        this.company = company;
        this.review = review;
        this.pros = pros;
        this.cons = cons;
        this.jobTitle = jobTitle;
        this.jobStatus = jobStatus;
        this.location = location;
    }

    public Review(Integer id,JobSeeker user, Company company, LocalDateTime reviewedAt, String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        this.id=id;
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


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobSeeker getUser() {
        return this.user;
    }

    public void setUser(JobSeeker user) {
        this.user = user;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getReviewedAt() {
        return this.reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReview() {
        return this.review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPros() {
        return this.pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getCons() {
        return this.cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobStatus() {
        return this.jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
   
}

