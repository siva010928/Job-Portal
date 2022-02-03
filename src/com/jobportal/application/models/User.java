package com.jobportal.application.models;

import com.jobportal.application.*;


import java.io.IOException;
import java.lang.System.Logger;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
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
    private String firstName,lastName,gender,email,location,phone;
    private Date DOB;


    //for seeing this profile by job provier purpose they should not see job_seeker's location,dob,gender
    //these information enough for job provider
    public User(String firstName, String lastName,String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String firstName, String lastName, String gender,Date DOB,String email,String location,String phone,UserType userType) {
        this.phone=phone;
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.DOB = DOB;
        this.location=location;
    }

    public static int login(String email,String password) throws SQLException, NoSuchAlgorithmException, IOException, ParseException{
        PreparedStatement stmt=App.conn.prepareStatement("SELECT * FROM users JOIN user_types USING(user_type_id) WHERE email=? ");
        stmt.setString(1, email);
        //hash password  

        ResultSet rS=stmt.executeQuery();
        if(rS.isBeforeFirst()){
            rS.next();
            String retrievedPassword=rS.getString("password");
            if(App.hashPassword(password).equals(retrievedPassword)){
                App.user_id=rS.getInt("user_id");
                String category=rS.getString("category");
                if(category.equals(UserType.JOB_SEEKER.toString())){
                    ResultSet temp_seekerResult=App.conn.prepareStatement("SELECT job_seeker_id FROM job_seekers WHERE user_id="+App.user_id).executeQuery();
                    temp_seekerResult.next();

                    int job_seeker_id=temp_seekerResult.getInt("job_seeker_id");

                    App.logginUser=(JobSeeker)new JobSeeker(job_seeker_id,rS.getString("first_name"), rS.getString("last_name"),rS.getString("email"));
                    App.logginUser.setDOB(rS.getDate("DOB"));
                    App.logginUser.setPhone(rS.getString("phone"));
                    App.logginUser.setGender(rS.getString("gender"));
                    App.logginUser.setLocation(rS.getString("location"));
                    App.logginUser.setUserType(UserType.JOB_SEEKER);

                    App.id=job_seeker_id;
                    // App.logginUser.generateProfile();
                }else{
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

                    Pay revenue=new Pay(rSprovider.getInt("revenue_id"),rSprovider.getBigDecimal("from"), rSprovider.getBigDecimal("to"), rSprovider.getString("pay_type"));
                    Company company=new Company(company_id,rCompany.getInt("reviews"), rCompany.getInt("ratings"), rSprovider.getInt("founded"), rSprovider.getInt("size"), rSprovider.getString("name"), rSprovider.getString("logo"), rSprovider.getString("sector"), rSprovider.getString("industry"), rSprovider.getString("location"), revenue);
                    
                    App.logginUser=new JobProvider(UserType.JOB_PROVIDER,rS.getString("first_name"), rS.getString("last_name"), rS.getString("gender"),rS.getDate("DOB"),email,rS.getString("location"), rS.getString("phone"),rSprovider.getString("designation"), company);
                }
            }
            else{
                    //wrong password but user exists
                    System.out.println("Incorrect email or password...");
                    App.login_view();
            }
        }else{
            //user does not exist
            System.out.println("user not exist....");
            System.out.println("please sign up to continue..");
            try {
                try {
                    App.signup_view();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return App.id;
    }



    public static int signUp(String firstName,String lastName,String email,String hashedPassword,UserType userType,String companyName,String designation,String companyLocation) throws PortalException{  
        int company_id=-1;
        PreparedStatement stmt;
        if(designation!=null){
            try {
                //will always return other than -1
                company_id=Company.getCompanyWithName(companyName);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    

        //here we want to use transaction principle because on sign up,
        //being storing into users db we alsow want to insert in seeker/provider table 
        //if after inserting into users table if any error happens there will user withou provider/seeker which is unethical
        try{
            System.out.println("auto commit turned off");
            App.conn.setAutoCommit(false);
            //one transaction

            //inserting user into users db
            stmt=App.conn.prepareStatement("INSERT INTO users(first_name,last_name,email,password,user_type_id) VALUES(?,?,?,?,?)");//,?,?,?,DEFAULT,?,?
            stmt.setString(1,firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.setInt(5, userType.getUserTypeId());
            // stmt.setString(5, user.getLocation());
            // stmt.setString(6, user.getGender());
            // stmt.setDate(7, user.getDOB());
            // stmt.setInt(8, user.getUserType().getUserTypeId());
            // stmt.setString(9, user.getPhone());
            
            int rowsAffected=stmt.executeUpdate();

            //inserting into job_seekers
            App.user_id=App.getLastInsertId();
            if(userType==UserType.JOB_SEEKER){
                stmt=App.conn.prepareStatement("INSERT INTO job_seekers(user_id) VALUES(?)");
                stmt.setInt(1, App.user_id);
                rowsAffected=stmt.executeUpdate();
                App.id=App.getLastInsertId();
            }else{
                stmt.close();

                if(company_id==-1){//create new company

                    Pay pay=new Pay(-1, new BigDecimal(0.00), new BigDecimal(0.00), "ANNUALLY");
                    Integer revenue_id=pay.addPayToDb();

                    stmt=App.conn.prepareStatement("INSERT INTO companies(name,location,revenue_id) VALUES(?,?,?)");
                    stmt.setString(1, companyName);
                    stmt.setString(2, companyLocation);
                    stmt.setInt(3, revenue_id);
                    rowsAffected=stmt.executeUpdate();
                    company_id=App.getLastInsertId();


                    stmt=App.conn.prepareStatement("INSERT INTO job_providers(user_id,company_id) VALUES(?,?)");
                    stmt.setInt(1, App.user_id);
                    stmt.setInt(2, company_id);
                    rowsAffected=stmt.executeUpdate();
                }else{//join an company
                    stmt.close();
                    stmt=App.conn.prepareStatement("INSERT INTO job_providers(user_id,designation,company_id) VALUES(?,?,?)");
                    stmt.setInt(1, App.user_id);
                    stmt.setString(2, designation);
                    stmt.setInt(3, company_id);
                    rowsAffected=stmt.executeUpdate();
                }
                App.id=App.getLastInsertId();

                
            }
            System.out.println("commited");
            App.conn.commit();
        }catch(SQLException e){
            e.printStackTrace();
            try {
                System.err.println("Transaction rolled back at sign up");
                App.conn.rollback();
                
            } catch (SQLException e1) {
                e1.printStackTrace();

            }
        }
        finally{
            try {
                App.conn.setAutoCommit(true);
                System.out.println("auto commit turned on");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return App.id;
    }

    public  void updateBaseProfile() throws ParseException, SQLException{
        
        //can be used in MAIN class
        // DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
        // java.util.Date date = formatter.parse(DOBupdates);
        // java.sql.Date DOB = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
        // App.logginUser.setDOB(DOB);
        // App.logginUser.setEmail(email);
        // App.logginUser.setFirstName(firstName);
        // App.logginUser.setGender(gender);
        // App.logginUser.setLastName(lastName);
        // App.logginUser.setLocation(location);

        PreparedStatement stmt;
        stmt=App.conn.prepareStatement("UPDATE users SET first_name=?,last_name=?,email=?,gender=?,DOB=?,location=?,phone=? WHERE user_id=?");
        stmt.setString(1, this.getFirstName());
        stmt.setString(2, this.getLastName());
        stmt.setString(3, this.getEmail());
        stmt.setString(4, this.getGender());
        stmt.setDate(5, this.getDOB());
        stmt.setString(6, this.getLocation());
        stmt.setString(7, "+91"+this.getPhone());
        stmt.setInt(8, App.user_id);
        int writtenResults=stmt.executeUpdate();
        System.err.println("updated: "+writtenResults);
    }

    public void updatePassword(String password) throws SQLException{
        PreparedStatement stmt;
        stmt=App.conn.prepareStatement("UPDATE users SET password=? WHERE user_id=?");
        stmt.setString(1, password);
        stmt.setInt(2, App.user_id);
        int writtenResults=stmt.executeUpdate();
        System.err.println("updated: "+writtenResults);
    }

    public boolean validatePassword(String password) throws SQLException{
        PreparedStatement stmt;
        stmt=App.conn.prepareStatement("SELECT password FROM users WHERE user_id=?");
        stmt.setInt(1, App.user_id);   
        ResultSet rS=stmt.executeQuery();
        rS.next();
        if(rS.getString("password").equals(password)) return true;
        return false;
    }
    
    //both seeker and providers jobs feed
    public abstract  ArrayList<Job> getJobsFeed(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter,Integer salaryFilter) throws SQLException;
    public abstract void updateProfile() throws SQLException;
    public abstract void generateProfile() throws SQLException;


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

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDOB() {
        return this.DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }


    public abstract String toString();
   

}
