package com.jobportal.application.models;

import com.jobportal.application.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class User {
    private UserType userType;
    private String firstName,lastName,gender,email,password,location;

    public User(String firstName, String lastName, String gender, String email, String password,String location,UserType userType) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.location=location;
    }



    public static int login(String email,String password) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM users u JOIN user_types ut ON u.user_type_id=ut.user_type_id WHERE email=? AND password=? ");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rS=stmt.executeQuery();

        if(rS.next()){
            String category=rS.getString("category");
            if(category==UserType.JOB_SEEKER.toString()){
                stmt=App.conn.prepareStatement("SELECT * FROM job_seekers WHERE user_id=?");
                stmt.setInt(1, rS.getInt("user_id"));
                ResultSet rS1=stmt.executeQuery();

                App.id=rS1.getInt("job_seeker_id");
                
                //getting Projects
                stmt=App.conn.prepareStatement("SELECT * FROM projects WHERE job_seeker_id=?");
                stmt.setInt(1, App.id);
                ResultSet rProjects=stmt.executeQuery();

                ArrayList<Project> projects=new ArrayList<>();

                while(rProjects.next()){
                    projects.add(new Project(rProjects.getString("title"), rProjects.getString("client"), rProjects.getString("status"), rProjects.getString("link"), rProjects.getString("details"), rProjects.getDate("start"), rProjects.getDate("end")));
                }

                //getting educations
                stmt=App.conn.prepareStatement("SELECT * FROM educations WHERE job_seeker_id=?");
                stmt.setInt(1, App.id);
                ResultSet rEducations=stmt.executeQuery();

                ArrayList<Education> educations=new ArrayList<>();

                while(rEducations.next()){
                    educations.add(new Education(rEducations.getString("educationLevel"), rEducations.getString("specialization"), rEducations.getString("institution"), rEducations.getString("course_type"), rEducations.getInt("passout")));
                }

                //getting employments
                stmt=App.conn.prepareStatement("SELECT * FROM employments WHERE job_seeker_id=?");
                stmt.setInt(1, App.id);
                ResultSet rEmployments=stmt.executeQuery();

                ArrayList<Employment> employments=new ArrayList<>();

                while(rEmployments.next()){
                    employments.add(new Employment(rEmployments.getString("organization"), rEmployments.getString("designation"), rEmployments.getDate("start_date"), rEmployments.getDate("end_date"), rEmployments.getBoolean("stillWorking")));
                }
                

                //getting key_skills
                stmt=App.conn.prepareStatement("SELECT * FROM seeker_skills JOIN key_skills USING(key_skill_id) WHERE job_seeker_id=?");
                stmt.setInt(1, App.id);
                ResultSet rSkills=stmt.executeQuery();

                ArrayList<String> keySkills=new ArrayList<>();

                while(rSkills.next()){
                    keySkills.add(rSkills.getString("name"));
                }

                //getting languages
                stmt=App.conn.prepareStatement("SELECT * FROM seeker_languages JOIN languages USING(language_id) WHERE job_seeker_id=?");
                stmt.setInt(1, App.id);
                ResultSet rLanguages=stmt.executeQuery();

                ArrayList<String> languages=new ArrayList<>();

                while(rLanguages.next()){
                    languages.add(rLanguages.getString("name"));
                }

                App.logginUser=new JobSeeker(rS.getString("first_name"), rS.getString("last_name"), rS.getString("gender"), rS.getString("email"), rS.getString("password"),rS.getString("location"),UserType.JOB_SEEKER, keySkills, languages, employments, educations, projects, rS1.getString("accomplishments"));
            }else{
                stmt.close();
                //getting logged in job provider
                stmt=App.conn.prepareStatement("SELECT * FROM job_providers JOIN companies USING (company_id) JOIN pays ON pays.pay_id=companies.revenue_id WHERE user_id=?");
                stmt.setInt(1, rS.getInt("user_id"));
                ResultSet rSprovider=stmt.executeQuery();

                App.id=rSprovider.getInt("job_provider_id");
                Integer company_id=rSprovider.getInt("company_id");

                //getting company details of the job provider
                stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
                stmt.setInt(1, company_id);
                ResultSet rCompany=stmt.executeQuery();

                Pay revenue=new Pay(rS.getBigDecimal("from"), rS.getBigDecimal("to"), rS.getString("pay_type"));
                Company company=new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), revenue);

                App.logginUser=new JobProvider(UserType.JOB_PROVIDER,rS.getString("first_name"), rS.getString("last_name"), rS.getString("gender"), email, password,rS.getString("location"), rSprovider.getString("designation"), company);
            }
        }
        return -1;
    }

}
