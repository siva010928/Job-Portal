package com.jobportal.application.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jobportal.application.App;

public class JobSeeker extends User{
    private Integer id;
    private ArrayList<String> keySkills,languages;
    private ArrayList<Employment> employments;
    private ArrayList<Education> educations;
    private ArrayList<Project> projects;
    private String accompolishments;


    //for seeing this profile by job provier purpose they should not see job_seeker's password,location
    //for this we want to store id to retrieve educations,projects,etc
    //these information enough for job provider
    public JobSeeker(Integer job_seeker_id,String firstName, String lastName,String email) {
        super(firstName, lastName,email);
        this.id=job_seeker_id;
    }

    //only for login
    public JobSeeker(String firstName, String lastName, String gender,Date DOB,String email,String location,String phone,UserType userType, ArrayList<String> keySkills, ArrayList<String> languages, ArrayList<Employment> employments, ArrayList<Education> educations, ArrayList<Project> projects, String accompolishments) {
        super(firstName, lastName, gender,DOB,email, location,phone,userType);
        this.keySkills = keySkills;
        this.languages = languages;
        this.employments = employments;
        this.educations = educations;
        this.projects = projects;
        this.accompolishments = accompolishments;
        //for sake
        this.id=App.id;
    }
    

    //this method is used for the scenario [job provider comes get list of applicants of job but he not check all job_seeker_extra profiles 
    //but check maybe one or two in that case 
    //we only load this extra profile if he select to view full details of particular applicant
    //otherwise it won't load unnecessary
    //view candidate full profile their education etc from applications page
    //during login also this method invoke once
    @Override
    public void generateProfile()throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("SELECT accomplishments FROM job_seekers WHERE job_seeker_id=?");
        stmt.setInt(1,this.getId());
        ResultSet rS1=stmt.executeQuery();
        rS1.next();
        String accomplishments=rS1.getString("accomplishments");
        //getting Projects
        stmt=App.conn.prepareStatement("SELECT * FROM projects WHERE job_seeker_id=?");
        stmt.setInt(1, this.getId());
        ResultSet rProjects=stmt.executeQuery();

        ArrayList<Project> projects=new ArrayList<>();

        while(rProjects.next()){
            projects.add(new Project(rProjects.getInt("project_id"),rProjects.getString("title"), rProjects.getString("client"), rProjects.getString("status"), rProjects.getString("link"), rProjects.getString("details"), rProjects.getDate("start"), rProjects.getDate("end")));
        }

        //getting educations
        stmt=App.conn.prepareStatement("SELECT * FROM educations WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rEducations=stmt.executeQuery();

        ArrayList<Education> educations=new ArrayList<>();

        while(rEducations.next()){
            educations.add(new Education(rEducations.getInt("education_id"),rEducations.getString("educationLevel"), rEducations.getString("specialization"), rEducations.getString("institution"), rEducations.getString("course_type"), rEducations.getInt("passout")));
        }

        //getting employments
        stmt=App.conn.prepareStatement("SELECT * FROM employments WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rEmployments=stmt.executeQuery();

        ArrayList<Employment> employments=new ArrayList<>();

        while(rEmployments.next()){
            employments.add(new Employment(rEmployments.getInt("employment_id"),rEmployments.getString("organization"), rEmployments.getString("designation"), rEmployments.getDate("start_date"), rEmployments.getDate("end_date")));
        }
        

        //getting key_skills
        stmt=App.conn.prepareStatement("SELECT * FROM seeker_skills JOIN key_skills USING(key_skill_id) WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rSkills=stmt.executeQuery();

        ArrayList<String> keySkills=new ArrayList<>();

        while(rSkills.next()){
            keySkills.add(rSkills.getString("name"));
        }

        //getting languages
        stmt=App.conn.prepareStatement("SELECT * FROM seeker_languages JOIN languages USING(language_id) WHERE job_seeker_id=?");
        stmt.setInt(1, this.id);
        ResultSet rLanguages=stmt.executeQuery();

        ArrayList<String> languages=new ArrayList<>();

        while(rLanguages.next()){
            languages.add(rLanguages.getString("name"));
        }

        //setting all
        this.setAccompolishments(accomplishments);
        this.setProjects(projects);
        this.setEducations(educations);
        this.setEmployments(employments);
        this.setKeySkills(keySkills);
        this.setLanguages(languages);
    }

    
    //Jobs Feed  job seeker optional Filter
    @Override
    public ArrayList<Job> getJobsFeed(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter,Integer salaryFilter) throws SQLException{
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
            query.append(m.getKey());//field name
            query.append("LIKE");
            query.append("%"+m.getValue()+"%");//field value
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
        ArrayList<Job> jobs=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            
            Integer job_id=rS.getInt("job_id");
            int company_id=rS.getInt("company_id");
            int revenue_id=rS.getInt("revenue_id");
            
            //getting pay of the job posted by this job provider
            Pay salaryPay=new Pay(rS.getInt("pay_id"),rS.getBigDecimal("from"),rS.getBigDecimal("to"), rS.getString("pay_type"));

            
            //getting company details of the job posted by this job provider
            stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
            stmt.setInt(1, company_id);
            ResultSet rCompany=stmt.executeQuery();

            stmt=App.conn.prepareStatement("SELECT * FROM pays WHERE pay_id=?");
            stmt.setInt(1, revenue_id);
            ResultSet rCompanyRevenue=stmt.executeQuery();

            Pay revenue=new Pay(rCompanyRevenue.getInt("pay_id"),rCompanyRevenue.getBigDecimal("from"),rCompanyRevenue.getBigDecimal("to"), rCompanyRevenue.getString("pay_type"));
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
            // //getting questions id for a particular job
            // stmt=App.conn.prepareStatement("SELECT * FROM questions WHERE job_id=?");
            // stmt.setInt(1,job_id);
            // ResultSet rQuestions=stmt.executeQuery();

            // ArrayList<Integer> questions=new ArrayList<>();

            // while(rQuestions.next()){
            //     questions.add(rQuestions.getInt("question_id"));
            // }
            jobs.add(new Job(job_id,rS.getInt("openings"),rS.getString("title"), rS.getString("description"), rS.getString("location_type"), rS.getString("location"), rS.getString("fullOrPartTime"), rS.getString("job_status"), rS.getString("candidate_profile"), rS.getString("education_level"), salaryPay, rS.getTimestamp("postedAt"), job_types, job_schedules,company));
        }
        return jobs;
    }

    //update their additional profile
    @Override
    public void updateProfile() throws SQLException {
        
    }


    //getting my-Jobs menu for job seeker 

    public Integer getId(){
        return this.id;
    }
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
