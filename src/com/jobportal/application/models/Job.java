package com.jobportal.application.models;


import java.math.BigDecimal;
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

    public Job(){
        
    }

    {
        this.jobTypes=new ArrayList<>();
        this.jobSchedules=new ArrayList<>();
        this.questions=new ArrayList<>();
        this.questionsStrings=new ArrayList<>();
        this.jobTypesIds=new ArrayList<>();
        this.jobSchedulesIds=new ArrayList<>();
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
    //can be filter by (active,reviewed,rejected,hired)
    //can be sorted by applied_date and first name(asc/desc)
    public ArrayList<Application> getApplications(String jobstatusFilter,HashMap<String,Integer> sortFilter) throws SQLException{
        

        //default sorted by applied date desc and first name of the user asc descending order
        if(sortFilter.isEmpty()){
            sortFilter.put("appliedAt", -1);
            sortFilter.put("first_name", 1);
        }

        //at first this job does not load any questionsIds and questionsStrings
        //at this calling of method,sure job provider will review atleast one applicants so they need to see their's answers with questions
        this.generateQuestionsIds();
        
        int job_id=this.getId();
        StringBuilder query=new StringBuilder("SELECT * FROM applications JOIN job_seekers USING(job_seeker_id) JOIN users USING(user_id) WHERE job_id=?");
        
        //filter by (active,reviewed,rejected)
        if(!jobstatusFilter.isEmpty()){
            query.append(" AND ");
            query.append("applications.status="+"'"+jobstatusFilter+"'");
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
        // System.err.println(query.toString());
        stmt.setInt(1, job_id);
        ResultSet rApplications=stmt.executeQuery();

        ArrayList<Application> applications=new ArrayList<>();

        while(rApplications.next()){
            JobSeeker jobSeeker=new JobSeeker(rApplications.getInt("job_seeker_id"),rApplications.getString("first_name"), rApplications.getString("last_name"),rApplications.getString("email"));
            
            applications.add(new Application(rApplications.getInt("application_id"), jobSeeker, this, rApplications.getString("resume"), rApplications.getString("status"), rApplications.getTimestamp("appliedAt")));
        }
        return applications;

    }

    public boolean checkApplicationExists() throws SQLException{
        PreparedStatement stmt = App.conn.prepareStatement("SELECT * FROM applications WHERE job_seeker_id=? AND job_id=?");
        stmt.setInt(1, App.id);
        stmt.setInt(2, this.getId());
        if(stmt.executeQuery().isBeforeFirst()){
            System.out.println("Already Applied... check my applications");
            return true;
        } 
        return false;
    }

    public void applyJob(ArrayList<String> answers,String resume) {
        int job_id=this.id;


        PreparedStatement stmt;

        //here we want to use transaction principle 
        //once a use applied a job values are inserted into both applications db and answers db
        //what if values after inserted into applications db and get interrupted,so there not be any answers  with this application_id
        //to prevent that cause ,Transaction is implemented(auto commit is turned off) refer -https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
        try{
            App.conn.setAutoCommit(false);
            //one transaction

            stmt=App.conn.prepareStatement("INSERT INTO applications(resume,job_seeker_id,job_id) VALUES(?,?,?)");
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
            System.out.println("Applied successfully...");
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

    public void updateJob(Job jobDetails, BigDecimal minSalary,BigDecimal maxSalary,String payType) throws SQLException{
        Pay salaryPay = new Pay(jobDetails.getPay().getId(),minSalary,maxSalary,payType);
        salaryPay.updatePay();

        this.setJobTitle(jobDetails.getJobTitle());
        this.setJobDescription(jobDetails.getJobDescription());
        this.setLocationType(jobDetails.getLocationType());
        this.setLocation(jobDetails.getLocation());
        this.setFullOrPartTime(jobDetails.getFullOrPartTime());
        this.setOpenings(jobDetails.getOpenings());

        PreparedStatement stmt=App.conn.prepareStatement("UPDATE jobs SET title=?,description=?,location_type=?,location=?,fullOrPartTime=?,openings=? WHERE job_id=?");
        stmt.setString(1, jobDetails.getJobTitle());
        stmt.setString(2, jobDetails.getJobDescription());
        stmt.setString(3, jobDetails.getLocationType());
        stmt.setString(4, jobDetails.getLocation());
        stmt.setString(5, jobDetails.getFullOrPartTime());
        stmt.setInt(6, jobDetails.getOpenings());
        stmt.setInt(7, jobDetails.getId());
        int updatedResults=stmt.executeUpdate();
        
    }

    public ArrayList<String> generateQuestions() throws SQLException{
        this.generateQuestionsIds();
        return this.generateQuestionsAsStrings();
    }

    public ArrayList<Integer> generateQuestionsIds() throws SQLException{
        ArrayList<Integer> questionsIds=new ArrayList<>();
        
        //base case
        if(!this.questions.isEmpty()) return this.questions;

        this.questions=new ArrayList<>();

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
        
        System.out.println("came to part 1 in generateQuestionsAsStrings");
        //base case
        if(!this.questionsStrings.isEmpty()) return this.questionsStrings;
        this.questionsStrings=new ArrayList<>();
        System.out.println("came to part 2 in generateQuestionsAsStrings");
        

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

    public void updateJobStatus(String status) throws SQLException{
        this.setJobStatus(status);
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE jobs SET job_status=? WHERE job_id=?");
        stmt.setString(1, this.getJobStatus());
        stmt.setInt(2,this.getId());
        System.out.println(stmt.toString());
        int updateResults=stmt.executeUpdate();
        System.err.println("job updated: "+updateResults);
    }


    
    @Override
    public String toString() {
        if(App.logginUser.getUserType().equals(UserType.JOB_SEEKER))
            return this.getJobTitle()+"\n"+
            this.getCompany().getName()+"\n"+
            this.getLocation()+
            (this.getPay()!=null?this.getPay().toString():"")+"\n"+
            "Profile: "+this.getCandidateProfile().substring(0, getCandidateProfile().length()>10?10:getCandidateProfile().length())+
            ".........."+"\n";
    return "\n"+this.getJobTitle()+"                 "+this.getActive()+" Active\t"+this.getReviewed()+" Reviewed\t"+this.getHired()+" of "+this.getOpenings()+" hired\n"+
        "\t"+this.getLocation()+"                 "+
        "Created at "+App.dateFormatter.format(this.getPostedAt());
    }

    
    
    public String show_details(){
        StringBuilder s=new StringBuilder();

        if(App.logginUser.getUserType().equals(UserType.JOB_PROVIDER)){
            s.append(""
            +"  Awaiting : "+this.getActive()
            +"  Reviewed: "+this.getReviewed()
            +"  Hired: "+this.getHired()
            +"  Rejected: "+this.getRejected());
            s.append("\n\n");
        }

        s.append(""
        +"  Job Title: "+this.getJobTitle()+"\n"
        +"  Company: "+this.getCompany().getName()+"\n"
        +"  Location: "+this.getLocation()+"\n"
        +"  Job Time: "+this.getFullOrPartTime()+"\n"
        +"  Job Openings: "+this.getOpenings()+"\n"
        +"  Education Level: "+this.getEducationLevel()+"\n"
        +"  Posted on : "+App.dateFormatter.format(this.getPostedAt())+"\n"
        +"  "+this.getPay().toString()+"\n"
        +"-----------------------Description-----------------------\n\t"
        +this.getJobDescription()+"\n"
        +"----------------------Candidate Profile----------------------\n\t"
        +this.getCandidateProfile()+"\n"
        +"---------------------- Job Types----------------------\n\t"
        +this.getJobTypes().toString()+"\n"
        +"---------------------- Job Schedules----------------------\n\t"
        +this.getJobSchedules().toString());

        return s.toString();

    }

    public ArrayList<Integer> getQuestions() {
        return questions;
    }

    public ArrayList<String> getQuestionsStrings() {
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

    public void addJobTypes(String jobType) {
        this.jobTypes.add(jobType);
    }

    public ArrayList<String> getJobSchedules() {
        return this.jobSchedules;
    }

    public void setJobSchedules(ArrayList<String> jobSchedules) {
        this.jobSchedules = jobSchedules;
    }

    public void addJobSchedules(String jobSchedule) {
        this.jobSchedules.add(jobSchedule);
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

    public void addQuestionsString(String questionsString) {
        this.questionsStrings.add(questionsString);
    }

    public ArrayList<Integer> getJobTypesIds() {
        return this.jobTypesIds;
    }

    public void addJobTypeId(int id){
        this.jobTypesIds.add(id);
    }

    public void setJobTypesIds(ArrayList<Integer> jobTypesIds) {
        this.jobTypesIds = jobTypesIds;
    }

    public ArrayList<Integer> getJobSchedulesIds() {
        return this.jobSchedulesIds;
    }

    public void addJobScheduleId(int id){
        this.jobSchedulesIds.add(id);
    }


    public void setJobSchedulesIds(ArrayList<Integer> jobSchedulesIds) {
        this.jobSchedulesIds = jobSchedulesIds;
    }

    
}
