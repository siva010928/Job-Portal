package com.jobportal.application.models;

import com.jobportal.application.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class User {
    private UserType userType;
    private String firstName,lastName,gender,email,location;
    private Date DOB;


    //for seeing this profile by job provier purpose they should not see job_seeker's password
    //these information enough for job provider
    public User(String firstName, String lastName, String gender, Date DOB,String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.DOB=DOB;
        this.email = email;
    }

    public User(String firstName, String lastName, String gender,Date DOB,String email,String location,UserType userType) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.DOB = DOB;
        this.location=location;
    }

    public static int login(String email,String password) throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM users JOIN user_types USING(user_type_id) WHERE email=? AND password=? ");
        stmt.setString(1, email);
        stmt.setString(2, password);
        //hash password


        ResultSet rS=stmt.executeQuery();

        if(rS.next()){
            App.user_id=rS.getInt("user_id");
            String category=rS.getString("category");
            if(category==UserType.JOB_SEEKER.toString()){
                ResultSet temp_seekerResult=App.conn.prepareStatement("SELECT job_seeker_id FROM job_seekers WHERE user_id="+App.user_id).executeQuery();
                temp_seekerResult.next();
                
                int job_seeker_id=temp_seekerResult.getInt("job_seeker_id");
                App.logginUser=new JobSeeker(job_seeker_id,rS.getString("first_name"), rS.getString("last_name"), rS.getString("gender"),rS.getDate("DOB"),rS.getString("email"));
                App.logginUser.setLocation(rS.getString("location"));
                App.logginUser.setUserType(UserType.JOB_SEEKER);
                App.id=job_seeker_id;
            }else{
                stmt.close();
                //getting logged in job provider
                stmt=App.conn.prepareStatement("SELECT * FROM job_providers JOIN companies USING (company_id) JOIN pays ON pays.pay_id=companies.revenue_id WHERE user_id=?");
                stmt.setInt(1, rS.getInt("user_id"));
                ResultSet rSprovider=stmt.executeQuery();
                rSprovider.next();

                App.id=rSprovider.getInt("job_provider_id");
                Integer company_id=rSprovider.getInt("company_id");

                //getting company details of the job provider
                stmt=App.conn.prepareStatement("SELECT COUNT(*) AS reviews,AVG(ratings) AS ratings FROM reviews WHERE company_id=?");
                stmt.setInt(1, company_id);
                ResultSet rCompany=stmt.executeQuery();
                rCompany.next();

                Pay revenue=new Pay(rS.getBigDecimal("from"), rS.getBigDecimal("to"), rS.getString("pay_type"));
                Company company=new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rS.getInt("founded"), rS.getInt("size"), rS.getString("name"), rS.getString("logo"), rS.getString("sector"), rS.getString("industry"), rS.getString("location"), revenue);

                App.logginUser=new JobProvider(UserType.JOB_PROVIDER,rS.getString("first_name"), rS.getString("last_name"), rS.getString("gender"),rS.getDate("DOB"),email,rS.getString("location"), rSprovider.getString("designation"), company);
            }
        }
        return -1;
    }

    public static void updateProfile(HashMap<String,String> updateStrings,String DOBupdates) throws ParseException, SQLException{
        PreparedStatement stmt;
        int writtenResults=0;
        if(!DOBupdates.isEmpty()){
            DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
            java.util.Date date = formatter.parse(DOBupdates);
            java.sql.Date DOB = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date

            App.logginUser.setDOB(DOB);
            stmt=App.conn.prepareStatement("UPDATE users SET DOB=? WHERE user_id=?");
            stmt.setDate(1, DOB);
            stmt.setInt(2,App.user_id);
            writtenResults=stmt.executeUpdate();
        }
        if(!updateStrings.isEmpty()){
            StringBuilder query=new StringBuilder("UPDATE users SET user_id=user_id");
            for (Map.Entry<String,String> m : updateStrings.entrySet()) {
                query.append(",");
                query.append(m.getKey()+" = "+m.getValue());
            }
            query.append("WHERE user_id=?");
            stmt=App.conn.prepareStatement(query.toString());
            writtenResults=stmt.executeUpdate();
        }
    }
    

    public UserType getUserType() {
        return this.userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDOB() {
        return this.DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

}
