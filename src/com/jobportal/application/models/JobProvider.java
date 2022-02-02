package com.jobportal.application.models;


import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jobportal.application.App;

public class JobProvider extends  User{
    private String designation;
    private Company company;

    public JobProvider(UserType userType, String firstName, String lastName, String gender,Date DOB,String email,String location,String phone,String designation, Company company) {
        super(firstName, lastName, gender,DOB,email,location,phone,userType);
        this.designation = designation;
        this.company = company;
    }


    //post a job from this provider
    public void postJob(Job jobDetails) throws SQLException{
        Pay salaryPay=jobDetails.getPay();
        
        try{
            App.conn.setAutoCommit(false);
            //inserting salary int pays db and getting it's id 
            Integer last_pay_id=salaryPay.addPayToDb();


            //inserting job
            PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO jobs(title,description,location_type,location,fullOrPartTime,openings,education_level,candidate_profile,job_provider_id,company_id,pay_id) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, jobDetails.getJobTitle());
            stmt.setString(2, jobDetails.getJobDescription());
            stmt.setString(3, jobDetails.getLocationType());
            stmt.setString(4, jobDetails.getLocation());
            stmt.setString(5, jobDetails.getFullOrPartTime());
            stmt.setInt(6, jobDetails.getOpenings());
            stmt.setString(7, jobDetails.getEducationLevel());
            stmt.setString(8, jobDetails.getCandidateProfile());
            stmt.setInt(9, App.id);
            stmt.setInt(10, this.company.getId());
            stmt.setInt(11, last_pay_id);
            int writtenResults=stmt.executeUpdate();

            //getting the last inserted job_id        
            int last_job_id=App.getLastInsertId();

            //inserting job_schedules
            for (Integer job_schedule_id : jobDetails.getJobSchedulesIds()) {
                stmt=App.conn.prepareStatement("INSERT INTO job_job_schedules VALUES(?,?)");
                stmt.setInt(1, last_job_id);
                stmt.setInt(2, job_schedule_id);
                writtenResults=stmt.executeUpdate();
            }

            //inserting job_types
            for (Integer job_type_id : jobDetails.getJobTypesIds()) {
                stmt=App.conn.prepareStatement("INSERT INTO job_job_types VALUES(?,?)");
                stmt.setInt(1, last_job_id);
                stmt.setInt(2, job_type_id);
                writtenResults=stmt.executeUpdate();
            }


            //inserting questions into db
            for (String question : jobDetails.generateQuestionsAsStrings()) {
                stmt=App.conn.prepareStatement("INSERT INTO questions(question,job_id) VALUES(?,?)");
                stmt.setString(1, question);
                stmt.setInt(2, last_job_id);
                writtenResults=stmt.executeUpdate();
            }
            App.conn.setAutoCommit(true);
            System.out.println("Posted successfully...");
        }
        catch(SQLException e){
            e.printStackTrace();
            try {
                System.err.println("Transaction rolled back at post_job(jobprovider)"+e.getMessage());
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

    //PostedJobs optional Filter
    @Override
    public ArrayList<Job> getJobsFeed(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter,Integer salaryFilter) throws SQLException{
        StringBuilder query=new StringBuilder("SELECT * FROM jobs JOIN pays USING(pay_id) JOIN companies USING(company_id) WHERE job_provider_id=?");
        
        //default sorted by posted date desc and job title asc descending order
        if(sortFilter.isEmpty()){
            sortFilter.put("jobs.postedAt", -1);
            sortFilter.put("jobs.title", 1);
        }

        
        //filtering by salary
        if(salaryFilter!=-1){
            //(pays.from >= 17000 OR pays.to >= 17000)
            query.append(" AND ");
            query.append("(pays.from >= "+salaryFilter +" OR pays.to >="+salaryFilter+ ") ");
        }

        //filterring last 7 days like
        if(daysFilter!=-1){
            query.append(" AND ");
            query.append("jobs.postedAt>(DATE_SUB(CURRENT_DATE,INTERVAL "+daysFilter+" DAY))");
        }

        //filtering for where class of job title,location
        if(!searchFilter.isEmpty()){
            for (Map.Entry<String,String> m : searchFilter.entrySet()) {
                // System.out.println("search Filter....");
                query.append(" AND ");
                query.append(m.getKey());//field name
                query.append(" LIKE ");
                query.append("'%"+m.getValue()+"%' ");//field value
            }
        }

        //filtering for order by class of postedAt,title(sort) it will be always the size of one
        if(!sortFilter.isEmpty()){
            // System.out.println("sort Filter....");
            query.append(" ORDER BY ");
            for(Map.Entry<String,Integer> m:sortFilter.entrySet()){
                query.append(m.getKey());
                query.append(m.getValue()==1?" ASC ":" DESC ");
                query.append(",");
            }
            query.deleteCharAt(query.length()-1);   
        }

        query.append("LIMIT 10");

        PreparedStatement stmt=App.conn.prepareStatement(query.toString());
        
        stmt.setInt(1, App.id);
        ArrayList<Job> jobs=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            
            Integer job_id=rS.getInt("job_id");
            int company_id=rS.getInt("company_id");
            int revenue_id=rS.getInt("revenue_id");
            
            //getting pay of the job posted by this job provider
            Pay salaryPay=new Pay(rS.getInt("pay_id"),rS.getBigDecimal("from"),rS.getBigDecimal("to"), rS.getString("pay_type"));

            
            // //getting company details of the job posted by this job provider
            // stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
            // stmt.setInt(1, company_id);
            // ResultSet rCompany=stmt.executeQuery();

            // stmt=App.conn.prepareStatement("SELECT * FROM pays WHERE pay_id=?");
            // stmt.setInt(1, revenue_id);
            // ResultSet rCompanyRevenue=stmt.executeQuery();

            // Pay revenue=new Pay(rCompanyRevenue.getInt("pay_id"),rCompanyRevenue.getBigDecimal("from"),rCompanyRevenue.getBigDecimal("to"), rCompanyRevenue.getString("pay_type"));
            Company company=new Company(company_id,-1, -1, rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), new Pay(-1, new BigDecimal(0), new BigDecimal(0),"ANNUALLY"));


            //getting applications of hired,rejected,active,hired,reviewed count of particular provider's job
            Integer hired=0,rejected=0,active=0,reviewed=0;

            //hired-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS hired FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.HIRED.toString());
            ResultSet rHired=stmt.executeQuery();
            while(rHired.next())
                hired=rHired.getInt("hired");


            //rejected-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS rejected FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.REJECTED.toString());
            ResultSet rRejected=stmt.executeQuery();
            while(rRejected.next())
                rejected=rRejected.getInt("rejected");

            //active-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS active FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.ACTIVE.toString());
            ResultSet rActive=stmt.executeQuery();
            while(rActive.next())
                active=rActive.getInt("active");

            //reviewed-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviewed FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.REVIEWED.toString());
            ResultSet rReviewed=stmt.executeQuery();
            while(rReviewed.next())
                reviewed=rReviewed.getInt("reviewed");
            
    
            //getting job_types
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_types JOIN job_types USING(job_type_id) WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rJobtypes=stmt.executeQuery();

            ArrayList<String> job_types=new ArrayList<>();
            while(rJobtypes.next()){
                job_types.add(rJobtypes.getString("name"));
            }

            //getting job_schedules
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_schedules JOIN job_schedules USING(job_schedule_id) WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rJobschedules=stmt.executeQuery();

            ArrayList<String> job_schedules=new ArrayList<>();

            while(rJobschedules.next()){
                job_schedules.add(rJobschedules.getString("name"));
            }
            jobs.add(new Job(job_id, active, reviewed, hired, rejected, rS.getInt("openings"),rS.getString("title"), rS.getString("description"), rS.getString("location_type"), rS.getString("location"), rS.getString("fullOrPartTime"), rS.getString("job_status"), rS.getString("candidate_profile"), rS.getString("education_level"), salaryPay, rS.getTimestamp("postedAt"), job_types, job_schedules,company));
        }
        System.out.println("jobs successfully fetched.....");
        return jobs;
    }

    @Override
    public void generateProfile() throws SQLException {
        
        
    }

    //update their additional profile
    @Override
    public void updateProfile() throws SQLException {
    }

    public void updateDesignation(String designation) throws SQLException{
        this.setDesignation(designation);
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE job_providers SET designation=? WHERE job_provider_id=?");
        stmt.setString(1, this.designation);
        stmt.setInt(2,App.id);
        int updateResults=stmt.executeUpdate();
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }    


    @Override
    public String toString() {
        return "{" +
            " userType='" + getUserType() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", gender='" + getGender() + "'" +
            ", email='" + getEmail() + "'" +
            ", location='" + getLocation() + "'" +
            ", phone='" + getPhone() + "'" +
            ", DOB='" + getDOB() + "'" +
            " designation='" + getDesignation() + "'" +
            ", company='" + getCompany().getName() + "'" +
            "}";
    }

   
}

