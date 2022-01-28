package com.jobportal.application.models;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Review {
    private Integer id;
    private LocalDateTime reviewedAt;
    private String review,pros,cons,jobTitle,jobStatus,location;

    //posting a review
    public Review(String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        this.review = review;
        this.pros = pros;
        this.cons = cons;
        this.jobTitle = jobTitle;
        this.jobStatus = jobStatus;
        this.location = location;
    }

    public Review(Integer id,Timestamp reviewedAt, String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        this.id=id;
        //timestamp.toInstant().atZone(zoneId).toLocalDate()
        //ZoneOffset.UTC
        this.reviewedAt = reviewedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
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

