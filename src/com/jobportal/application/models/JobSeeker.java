package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jobportal.application.App;
import com.mysql.cj.xdevapi.Result;

public class JobSeeker extends User{
    private ArrayList<String> keySkills,languages;
    private ArrayList<Employment> employments;
    private ArrayList<Education> educations;
    private ArrayList<Project> projects;
    private String accompolishments;

    public JobSeeker(String firstName, String lastName, String gender, String email, String password,String location,UserType userType, ArrayList<String> keySkills, ArrayList<String> languages, ArrayList<Employment> employments, ArrayList<Education> educations, ArrayList<Project> projects, String accompolishments) {
        super(firstName, lastName, gender, email, password, location,userType);
        this.keySkills = keySkills;
        this.languages = languages;
        this.employments = employments;
        this.educations = educations;
        this.projects = projects;
        this.accompolishments = accompolishments;
    }

    //Jobs Feed fro job seeker optional Filter
    public ArrayList<Job> getJobs(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter,Integer salaryFilter) throws SQLException{
        StringBuilder query=new StringBuilder("SELECT * FROM jobs JOIN pays USING(pay_id) JOIN companies USING(company_id) WHERE 1=1");
        

        //filtering by salary
        if(salaryFilter!=-1){
            //(pays.from >= 17000 OR pays.to >= 17000)
            query.append(" AND ");
            query.append("(pays.from >= "+salaryFilter +" OR pays.to >= 17000) ");
        }

        
        //filterring last 7 days like
        if(daysFilter!=-1){
            query.append(" AND ");
            query.append("postedAt>(DATE_SUB(CURRENT_DATE,INTERVAL "+daysFilter+" DAY))");
        }

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
            jobs.add(new Job(job_id,rS.getInt("openings"),rS.getString("title"), rS.getString("description"), rS.getString("location_type"), rS.getString("location"), rS.getString("fullOrPartTime"), rS.getString("job_status"), rS.getString("candidate_profile"), rS.getString("education_level"), salaryPay, rS.getDate("postedAt"), job_types, job_schedules,questions,company));
        }
        return jobs;
    }

    //getting my-Jobs menu for job seeker
    // public

    public ArrayList<String> getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(ArrayList<String> keySkills) {
        this.keySkills = keySkills;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public ArrayList<Employment> getEmployments() {
        return employments;
    }

    public void setEmployments(ArrayList<Employment> employments) {
        this.employments = employments;
    }

    public ArrayList<Education> getEducations() {
        return educations;
    }

    public void setEducations(ArrayList<Education> educations) {
        this.educations = educations;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    public String getAccompolishments() {
        return accompolishments;
    }

    public void setAccompolishments(String accompolishments) {
        this.accompolishments = accompolishments;
    }
}
