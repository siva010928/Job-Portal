package com.jobportal.application.models;


import java.math.BigDecimal;
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

    public JobProvider(UserType userType, String firstName, String lastName, String gender, String email, String password, String designation, Company company) {
        super(firstName, lastName, gender, email, password,userType);
        this.designation = designation;
        this.company = company;
    }


    //post a job from this provider
    public void postJob(Job jobDetails,BigDecimal minSalary,BigDecimal maxSalary) throws SQLException{
        Pay salaryPay=new Pay(minSalary, maxSalary, "MONTHLY");
        //inserting salary int pays db and getting it's id 
        Integer last_pay_id=salaryPay.addPayToDb();

        //inserting job
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO jobs('title','description','location_type','location','fullOrPartTime','openings','job_provider_id','company_id','pay_id') VALUES(?,?,?,?,?,?,?,?)");
        stmt.setString(1, jobDetails.getJobTitle());
        stmt.setString(2, jobDetails.getJobDescription());
        stmt.setString(3, jobDetails.getLocationType());
        stmt.setString(4, jobDetails.getLocation());
        stmt.setString(5, jobDetails.getFullOrPartTime());
        stmt.setInt(6, jobDetails.getOpenings());
        stmt.setInt(7, App.id);
        stmt.setInt(8, this.company.getId());
        stmt.setInt(9, last_pay_id);

        //inserting job_schedules

        //getting the last inserted job_id        
        int last_job_id=App.getLastInsertId();

        //inserting questions into db
        for (String question : jobDetails.getQuestionsAsStrings()) {
            stmt=App.conn.prepareStatement("INSERT INTO questions(question,job_id) VALUES(?,?)");
            stmt.setString(1, question);
            stmt.setInt(2, last_job_id);
        }
        
    }

    //PostedJobs optional Filter
    public ArrayList<Job> getJobs(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter) throws SQLException{
        StringBuilder query=new StringBuilder("SELECT * FROM jobs JOIN pays USING(pay_id) JOIN companies USING(company_id) WHERE job_provider_id=?");
        
        //filtering for where class of job title,location
        for (Map.Entry<String,String> m : searchFilter.entrySet()) {
            query.append(" AND ");
            query.append(m.getKey());
            query.append("LIKE");
            query.append("%"+m.getValue()+"%");
        }

        //filtering for order by class of postedAt,title(sort) it will be always the size of one
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
        
        stmt.setInt(1, App.id);
        ArrayList<Job> jobs=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            
            Integer job_id=rS.getInt("job_id");
            int company_id=rS.getInt("company_id");
            int revenue_id=rS.getInt("revenue_id");
            
            //getting pay of the job posted by this job provider
            Pay salaryPay=new Pay(rS.getBigDecimal("from"),rS.getBigDecimal("to"), rS.getString("pay_type"));

            
            //getting company details of the job posted by this job provider
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
            stmt.setInt(1, company_id);
            ResultSet rCompany=stmt.executeQuery();

            stmt=App.conn.prepareStatement("SELECT * FROM pays WHERE pay_id=?");
            stmt.setInt(1, revenue_id);
            ResultSet rCompanyRevenue=stmt.executeQuery();

            Pay revenue=new Pay(rCompanyRevenue.getBigDecimal("from"),rCompanyRevenue.getBigDecimal("to"), rCompanyRevenue.getString("pay_type"));
            Company company=new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), revenue);


            //getting applications of hired,rejected,active,hired,reviewed count of particular provider's job
            Integer hired,rejected,active,reviewed;

            //hired-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS hired FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.HIRED.toString());
            ResultSet rHired=stmt.executeQuery();
            hired=rHired.getInt("hired");


            //rejected-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS rejected FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.REJECTED.toString());
            ResultSet rRejected=stmt.executeQuery();
            rejected=rRejected.getInt("rejected");

            //active-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS active FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.ACTIVE.toString());
            ResultSet rActive=stmt.executeQuery();
            active=rActive.getInt("active");

            //reviewed-count
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviewed FROM applications WHERE job_id=? AND status=?");
            stmt.setInt(1,job_id);
            stmt.setString(2, ApplicationStatus.REVIEWED.toString());
            ResultSet rReviewed=stmt.executeQuery();
            reviewed=rReviewed.getInt("reviewed");
            
    
            //getting job_types
            stmt=App.conn.prepareStatement("SELECT * FROM job_job_type JOIN job_types USING(job_type_id) WHERE job_id=?");
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
                job_schedules.add(rJobtypes.getString("name"));
            }
            //getting questions id for a particular job
            stmt=App.conn.prepareStatement("SELECT * FROM questions WHERE job_id=?");
            stmt.setInt(1,job_id);
            ResultSet rQuestions=stmt.executeQuery();

            ArrayList<Integer> questions=new ArrayList<>();

            while(rQuestions.next()){
                questions.add(rQuestions.getInt("question_id"));
            }
            jobs.add(new Job(job_id, active, reviewed, hired, rejected, rS.getInt("openings"),rS.getString("title"), rS.getString("description"), rS.getString("location_type"), rS.getString("location"), rS.getString("fullOrPartTime"), rS.getString("job_status"), rS.getString("candidate_profile"), rS.getString("education_level"), salaryPay, rS.getDate("postedAt"), job_types, job_schedules,questions,company));
        }
        return jobs;
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

   
}

