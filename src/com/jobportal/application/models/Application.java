package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;

import com.jobportal.application.App;

public class Application {
    private ArrayList<String> answers;
    private JobSeeker jobSeeker;
    private Job job;
    private String resume,applicationStatus="ACTIVE";
    private LocalDateTime appliedAt;
    private  Integer id;

    
    public Application(Integer id,JobSeeker jobSeeker, Job job, String resume, String applicationStatus, Timestamp appliedAt) {
        this.jobSeeker = jobSeeker;
        this.job = job;
        this.resume = resume;
        this.applicationStatus = applicationStatus;
        //timestamp.toInstant().atZone(zoneId).toLocalDate()
        //ZoneOffset.UTC
        this.appliedAt = appliedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        this.id = id;
    }
    public Application(Integer id,ArrayList<String> answers,JobSeeker jobSeeker, Job job, String resume, String applicationStatus, LocalDateTime appliedAt) {
        this.answers = answers;
        this.jobSeeker = jobSeeker;
        this.job = job;
        this.resume = resume;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
    }
    public ArrayList<String> generateAnswers() throws SQLException{
        ArrayList<String> answers=new ArrayList<>();

        int job_id=this.job.getId();
        PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM seeker_answers WHERE application_id=?");
        stmt.setInt(1, this.getId());
        ResultSet rAnswers=stmt.executeQuery();
        while(rAnswers.next()){
            answers.add(rAnswers.getString("answer"));
        }
        return this.answers=answers;
    }

    public void updateStatus(String updatedStatus) throws SQLException{
        this.setApplicationStatus(applicationStatus);
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE applications SET status=? WHERE application_id=?");
        stmt.setString(1,updatedStatus);
        stmt.setInt(2, this.getId());
        int writtenResults=stmt.executeUpdate();
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
