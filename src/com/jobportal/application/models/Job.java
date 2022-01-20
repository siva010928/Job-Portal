package com.jobportal.application.models;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

import com.jobportal.application.App;

public class Job {

    private Integer id,active,reviewed,hired,rejected,openings;
    private String jobTitle,jobDescription,locationType,location,fullOrPartTime,jobStatus,candidateProfile,educationLevel;
    private Pay pay;
    private Date postedAt;
    ArrayList<String> jobTypes,jobSchedules;
    ArrayList<Integer> questions;
    private Company company;

    //only for posting a job
    private ArrayList<String> questionsStrings;
    ArrayList<Integer> jobTypesIds,jobSchedulesIds;

    public Job(String jobTitle, String jobDescription, String locationType, String location, String fullOrPartTime,Integer openings, String candidateProfile, String educationLevel, ArrayList<Integer> jobTypes, ArrayList<Integer> jobSchedules, ArrayList<String> questionsStrings) {
        this.openings=openings;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.locationType = locationType;
        this.location = location;
        this.fullOrPartTime = fullOrPartTime;
        this.candidateProfile = candidateProfile;
        this.educationLevel = educationLevel;
        this.jobTypesIds = jobTypes;
        this.jobSchedulesIds = jobSchedules;
        this.questionsStrings = questionsStrings;
    }

    

    public Job(Integer id, Integer active, Integer reviewed, Integer hired, Integer rejected, Integer openings, String jobTitle, String jobDescription, String locationType, String location, String fullOrPartTime, String jobStatus, String candidateProfile, String educationLevel, Pay pay, Date postedAt, ArrayList<String> jobTypes, ArrayList<String> jobSchedules, ArrayList<Integer> questions, Company company) {
        this.id = id;
        this.active = active;
        this.reviewed = reviewed;
        this.hired = hired;
        this.rejected = rejected;
        this.openings = openings;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.locationType = locationType;
        this.location = location;
        this.fullOrPartTime = fullOrPartTime;
        this.jobStatus = jobStatus;
        this.candidateProfile = candidateProfile;
        this.educationLevel = educationLevel;
        this.pay = pay;
        this.postedAt = postedAt;
        this.jobTypes = jobTypes;
        this.jobSchedules = jobSchedules;
        this.questions = questions;
        this.company = company;
    }


    public void applyJob(ArrayList<String> answers,String resume) throws SQLException{
        int job_id=this.id;
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO applications(resume,job_seeker_id,job_id) VALUES(?,?,?)");
        stmt.setString(1, resume);
        stmt.setInt(2, App.id);
        stmt.setInt(3, job_id);
        int rowsAffected=stmt.executeUpdate();
        stmt.close();

        int application_id=App.getLastInsertId();

        for(int i=0;i<this.questions.size();i++){
            stmt=App.conn.prepareStatement("INSERT INTO seeker_answers VALUES(?,?,?)");
            stmt.setString(1,answers.get(i));
            stmt.setInt(2, application_id);
            stmt.setInt(3, questions.get(i));
            rowsAffected=stmt.executeUpdate();
        }
    }

    public ArrayList<String> getQuestionsAsStrings() throws SQLException{
        ArrayList<String> questionsStrings=new ArrayList<>();

        for (Integer question_id : this.questions) {
            PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM questions WHERE question_id=?");
            stmt.setInt(1,question_id);
            ResultSet rS=stmt.executeQuery();
            rS.next();
            questionsStrings.add(rS.getString("question"));
            stmt.close();
        }

        return questionsStrings;
    }



    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActive() {
        return this.active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getReviewed() {
        return this.reviewed;
    }

    public void setReviewed(Integer reviewed) {
        this.reviewed = reviewed;
    }

    public Integer getHired() {
        return this.hired;
    }

    public void setHired(Integer hired) {
        this.hired = hired;
    }

    public Integer getRejected() {
        return this.rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Integer getOpenings() {
        return this.openings;
    }

    public void setOpenings(Integer openings) {
        this.openings = openings;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return this.jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFullOrPartTime() {
        return this.fullOrPartTime;
    }

    public void setFullOrPartTime(String fullOrPartTime) {
        this.fullOrPartTime = fullOrPartTime;
    }

    public String getJobStatus() {
        return this.jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCandidateProfile() {
        return this.candidateProfile;
    }

    public void setCandidateProfile(String candidateProfile) {
        this.candidateProfile = candidateProfile;
    }

    public String getEducationLevel() {
        return this.educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Pay getPay() {
        return this.pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    public Date getPostedAt() {
        return this.postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public ArrayList<String> getJobTypes() {
        return this.jobTypes;
    }

    public void setJobTypes(ArrayList<String> jobTypes) {
        this.jobTypes = jobTypes;
    }

    public ArrayList<String> getJobSchedules() {
        return this.jobSchedules;
    }

    public void setJobSchedules(ArrayList<String> jobSchedules) {
        this.jobSchedules = jobSchedules;
    }

    public ArrayList<Integer> getQuestions() {
        return this.questions;
    }

    public void setQuestions(ArrayList<Integer> questions) {
        this.questions = questions;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ArrayList<String> getQuestionsStrings() {
        return this.questionsStrings;
    }

    public void setQuestionsStrings(ArrayList<String> questionsStrings) {
        this.questionsStrings = questionsStrings;
    }

    public ArrayList<Integer> getJobTypesIds() {
        return this.jobTypesIds;
    }

    public void setJobTypesIds(ArrayList<Integer> jobTypesIds) {
        this.jobTypesIds = jobTypesIds;
    }

    public ArrayList<Integer> getJobSchedulesIds() {
        return this.jobSchedulesIds;
    }

    public void setJobSchedulesIds(ArrayList<Integer> jobSchedulesIds) {
        this.jobSchedulesIds = jobSchedulesIds;
    }
    
    
}
