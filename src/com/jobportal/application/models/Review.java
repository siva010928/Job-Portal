package com.jobportal.application.models;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.jobportal.application.App;

public class Review {
    private Integer id, ratings;
    private LocalDateTime reviewedAt;
    private String review,pros,cons,jobTitle,jobStatus,location;

    //posting a review
    public Review(Timestamp reviewedAt,Integer ratings,String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        //timestamp.toInstant().atZone(zoneId).toLocalDate()
        //ZoneOffset.UTC
        this.reviewedAt = reviewedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        this.ratings=ratings;
        this.review = review;
        this.pros = pros;
        this.cons = cons;
        this.jobTitle = jobTitle;
        this.jobStatus = jobStatus;
        this.location = location;
    }

    public Review(Integer id,Timestamp reviewedAt,Integer ratings,String review, String pros, String cons, String jobTitle, String jobStatus, String location) {
        this.id=id;
        this.ratings=ratings;
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

    public Review() {
	}

	public void updateReview() throws SQLException{
        
        //Updating review to db
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE reviews SET job_title=?,ratings=?,job_status=?,location=?,review=?,pros=?,cons=? WHERE review_id=?");
        stmt.setString(1, this.getJobTitle());
        stmt.setInt(2, this.getRatings());
        stmt.setString(3, this.getJobStatus());
        stmt.setString(4, this.getLocation());
        stmt.setString(5, this.getReview());
        stmt.setString(6, this.getPros());
        stmt.setString(7, this.getCons());
        stmt.setInt(8, this.getId());
        int updatedResults=stmt.executeUpdate();
        System.out.println("review update: "+updatedResults);
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
    
    public Integer getRatings() {
        return this.ratings;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", ratings='" + getRatings() + "'" +
            ", reviewedAt='" + getReviewedAt() + "'" +
            ", review='" + getReview() + "'" +
            ", pros='" + getPros() + "'" +
            ", cons='" + getCons() + "'" +
            ", jobTitle='" + getJobTitle() + "'" +
            ", jobStatus='" + getJobStatus() + "'" +
            ", location='" + getLocation() + "'" +
            "}";
    }

    public void setRatings(Integer ratings) {
        this.ratings = ratings;
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

