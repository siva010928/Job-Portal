package com.jobportal.application.models;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jobportal.application.App;

public class Job {

    private Integer id,active=-1,reviewed=-1,hired=-1,rejected=-1,openings;
    private String jobTitle,jobDescription,locationType,location,fullOrPartTime,jobStatus,candidateProfile,educationLevel;
    private Pay pay;
    private LocalDateTime postedAt;
    ArrayList<String> jobTypes,jobSchedules;
    ArrayList<Integer> questions;//question ids
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

    //only for getting jobs feed to job seeker
    public Job(Integer id, Integer openings, String jobTitle, String jobDescription, String locationType, String location, String fullOrPartTime, String jobStatus, String candidateProfile, String educationLevel, Pay pay, Timestamp postedAt, ArrayList<String> jobTypes, ArrayList<String> jobSchedules, Company company) {
        this.id = id;
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
        //timestamp.toInstant().atZone(zoneId).toLocalDate()
        //ZoneOffset.UTC
        this.postedAt = postedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        this.jobTypes = jobTypes;
        this.jobSchedules = jobSchedules;
        // this.questions = questions;
        this.company = company;
    }

    //this is for job provider while seeing applications submitted by candidates job provider never wants to see job details during 
    
    //general constructor for creating a job
    public Job(Integer id, Integer active, Integer reviewed, Integer hired, Integer rejected, Integer openings, String jobTitle, String jobDescription, String locationType, String location, String fullOrPartTime, String jobStatus, String candidateProfile, String educationLevel, Pay pay, Timestamp postedAt, ArrayList<String> jobTypes, ArrayList<String> jobSchedules, Company company) {
        this.id = id;
        this.active = active;
        this.reviewed = reviewed;
        this.hired = hired;
        this.rejected = rejected;
        this.openings = openings;//*
        this.jobTitle = jobTitle;//*
        this.jobDescription = jobDescription;//*
        this.locationType = locationType;//*
        this.location = location;//*
        this.fullOrPartTime = fullOrPartTime;
        this.jobStatus = jobStatus;
        this.candidateProfile = candidateProfile;
        this.educationLevel = educationLevel;
        this.pay = pay;
        //timestamp.toInstant().atZone(zoneId).toLocalDate()
        //ZoneOffset.UTC
        this.postedAt = postedAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        this.jobTypes = jobTypes;
        this.jobSchedules = jobSchedules;
        // this.questions = questions;
        this.company = company;
    }

    //get All candidates applied to this job;
    //can be filter by (active,reviewed,rejected)
    //can be sorted by applied_date and first name(asc/desc)
    public ArrayList<Application> getApplications(String jobstatusFilter,HashMap<String,Integer> sortFilter) throws SQLException{
        //at first this job does not load any questionsIds and questionsStrings
        //at this calling of method,sure job provider will review atleast one applicants so they need to see their's answers with questions
        this.generateQuestionsIds();
        
        int job_id=this.getId();
        StringBuilder query=new StringBuilder("SELECT * FROM applications JOIN job_seekers USING(job_seeker_id) JOIN users USING(user_id) WHERE job_id=?");
        
        //filter by (active,reviewed,rejected)
        if(!jobstatusFilter.isEmpty()){
            query.append(" AND ");
            query.append("status="+jobstatusFilter);
        }

        //can be sorted by applied_date and first name(asc/desc)
        if(!sortFilter.isEmpty()){
            query.append(" ORDER BY ");
            for(Map.Entry<String,Integer> m:sortFilter.entrySet()){
                query.append(m.getKey());
                query.append(m.getValue()==1?" ASC":" DESC");
                query.append(",");
            }
            query.deleteCharAt(query.length()-1);   
        }
        PreparedStatement stmt=App.conn.prepareStatement(query.toString());
        stmt.setInt(1, job_id);
        ResultSet rApplications=stmt.executeQuery();

        ArrayList<Application> applications=new ArrayList<>();

        while(rApplications.next()){
            JobSeeker jobSeeker=new JobSeeker(rApplications.getInt("job_seeker_id"),rApplications.getString("first_name"), rApplications.getString("last_name"), rApplications.getString("gender"),rApplications.getDate("DOB"),rApplications.getString("email"));
            
            applications.add(new Application(rApplications.getInt("application_id"), jobSeeker, this, rApplications.getString("resume"), rApplications.getString("status"), rApplications.getTimestamp("appliedAt")));
        }
        return applications;

    }

    public void applyJob(ArrayList<String> answers,String resume) {
        int job_id=this.id;

        //here we want to use transaction principle 
        //once a use applied a job values are inserted into both applications db and answers db
        //what if values after inserted into applications db and get interrupted,so there not be any answers  with this application_id
        //to prevent that cause ,Transaction is implemented(auto commit is turned off) refer -https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
        try{
            App.conn.setAutoCommit(false);
            //one transaction

            PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO applications(resume,job_seeker_id,job_id) VALUES(?,?,?)");
            stmt.setString(1, resume);
            stmt.setInt(2, App.id);
            stmt.setInt(3, job_id);
            int rowsAffected=stmt.executeUpdate();
            stmt.close();


            //inserting answers
            int application_id=App.getLastInsertId();
            stmt=App.conn.prepareStatement("INSERT INTO seeker_answers VALUES(?,?,?)");
            for(int i=0;i<this.questions.size();i++){
                stmt.setString(1,answers.get(i));
                stmt.setInt(2, application_id);
                stmt.setInt(3, questions.get(i));
                rowsAffected=stmt.executeUpdate();
            }
            App.conn.commit();
        }catch(SQLException e){
            e.printStackTrace();
            try {
                System.err.println("Transaction rolled back at Job.applyJob method");
                App.conn.rollback();
                
            } catch (SQLException e1) {
                e1.printStackTrace();

            }
        }
        finally{
            try {
                App.conn.setAutoCommit(true);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void generateQuestions() throws SQLException{
        this.generateQuestionsIds();
        this.generateQuestionsAsStrings();
    }

    public ArrayList<Integer> generateQuestionsIds() throws SQLException{
        ArrayList<Integer> questionsIds=new ArrayList<>();

        //base case
        if(!this.questions.isEmpty()) return this.questions;
        
        PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM questions WHERE job_id=?");
        stmt.setInt(1,this.getId());
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            questionsIds.add(rS.getInt("question_id"));
        }
        stmt.close();

        return this.questions=questionsIds;
    }

    public ArrayList<String>  generateQuestionsAsStrings() throws SQLException{
        ArrayList<String> questionsStrings=new ArrayList<>();


        //base case
        if(!this.questionsStrings.isEmpty()) return this.questionsStrings;

        

        for (Integer question_id : this.questions) {
            PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM questions WHERE question_id=?");
            stmt.setInt(1,question_id);
            ResultSet rS=stmt.executeQuery();
            rS.next();
            questionsStrings.add(rS.getString("question"));
            stmt.close();
        }

        return this.questionsStrings=questionsStrings;
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

    public LocalDateTime getPostedAt() {
        return this.postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
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


    public void setQuestions(ArrayList<Integer> questions) {
        this.questions = questions;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
