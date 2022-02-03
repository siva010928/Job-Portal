package com.jobportal.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



import com.jobportal.application.models.Application;
import com.jobportal.application.models.Company;
import com.jobportal.application.models.DB_VARIABLES;
import com.jobportal.application.models.Education;
import com.jobportal.application.models.Employment;
import com.jobportal.application.models.Job;
import com.jobportal.application.models.JobProvider;
import com.jobportal.application.models.JobSeeker;
import com.jobportal.application.models.Pay;
import com.jobportal.application.models.PortalException;
import com.jobportal.application.models.Project;
import com.jobportal.application.models.Review;
import com.jobportal.application.models.User;
import com.jobportal.application.models.UserType;
public class App {



    //CURRENTS

    public static Job current_job=null;
    public static Company current_company=null;
    public static Review current_review=null;
    public static Application current_application=null;
    public static Education current_education=null;
    public static Employment current_employment=null;
    public static Project current_project=null;

    //used to check java sql dates retrieve from server is null 
    public static Date nullDate=Date.valueOf("0000-1-1");//converting string into sql date  
    public static Connection conn;
    public static Integer user_id=-1;
    public static Integer id=-1;//job-provider/job-seeker id
    public static User logginUser;///polymorphsm using method overriding
    public static HashMap<Integer,String> job_types,job_schedules;
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    //Create formatter
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM uuuu");
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM uuuu 'at' hh:mm a");

    public static String hashPassword(String s) throws NoSuchAlgorithmException{
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(s.getBytes(),0,s.length());
        return new BigInteger(1,m.digest()).toString(16);
    }

    public static int getLastInsertId() throws SQLException{
        PreparedStatement stmt=conn.prepareStatement("SELECT last_insert_id() AS last");
        ResultSet rS=stmt.executeQuery();
        rS.next();
        return rS.getInt("last");
    }
    public static void initializeJobtypesAndschedules(){
        job_types=new HashMap<>();
        ArrayList<String> job_types_list=new ArrayList<>(Arrays.asList("Permanent","Temporary","Freelance","Volunteer","Internship","Fresher"));
        
        for(int i=1;i<=job_types_list.size();i++){
            job_types.put(i, job_types_list.get(i-1));
        }

        job_schedules=new HashMap<>();
        ArrayList<String> job_schedules_list=new ArrayList<>(Arrays.asList("Morning shift","Day shift","Evening shift","Night shift","Flexible shift","Rotational shift","Fixed shift","US shift"));
        
        for(int i=1;i<=job_schedules_list.size();i++){
            job_schedules.put(i, job_schedules_list.get(i-1));
        }

    }

    public static void addJobtypesAndschedulesToDb() throws SQLException{
        for (int i = 1; i <=job_types.size(); i++) {
            PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO job_types(name) VALUES(?)");
            stmt.setString(1, job_types.get(i));
            int writtenResults=stmt.executeUpdate();
        }
        for (int i = 1; i <=job_schedules.size(); i++) {
            PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO job_schedules(name) VALUES(?)");
            stmt.setString(1, job_schedules.get(i));
            int writtenResults=stmt.executeUpdate();
        }
    }

    public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException, ParseException {
        initializeJobtypesAndschedules();
        // System.out.println("siva");
        try {
            // conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/job_portal","root","Deepika@71199");

            conn=DriverManager.getConnection("jdbc:mysql://"+DB_VARIABLES.HOST+":"+DB_VARIABLES.PORT+"/"+DB_VARIABLES.DB,DB_VARIABLES.USER,DB_VARIABLES.PASSWORD);
            System.out.println("database successfully connected :  "+" [DB=" + DB_VARIABLES.DB + ", HOST=" + DB_VARIABLES.HOST + ", PORT=" + DB_VARIABLES.PORT + "]");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // addJobtypesAndschedulesToDb();
        if(id==-1){

            //addJobtypesAndschedulesToDb(); 

            // App.id=User.login("siva", "siva");//.

            // App.id=User.login("dhanush", "dhanush");//.
            
            // App.id=User.login("rahul", "rahul");//.

            // App.id=User.login("prasanth", "prasanth");//.

            // App.id=User.login("pranav", "pranav");//for test 77 salary

            // System.out.println(logginUser.toString());
            // seeker_home();

            // App.id=User.login("chan", "chan");
            // System.out.println(logginUser.toString());
            // provider_home();

            open_view();
        }
    }
    public static void open_view() throws IOException, NoSuchAlgorithmException, SQLException, ParseException{
        System.out.print("Press 1 to login 2 to signup:  ");
        String input=reader.readLine();
        if(input.equals("1")){
            login_view();
        }else if(input.equals("2")){
            signup_view();
        }else{
            try {
                throw new PortalException("incorrect input...");
            } catch (PortalException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static void login_view() throws IOException, SQLException, NoSuchAlgorithmException, ParseException{
        System.out.println("-------------------LOGIN-------------------");
        System.out.print("Email: ");
        String email = reader.readLine();
        System.out.print("Password: ");
        String password = reader.readLine();
        int id=User.login(email, password);
        if(id!=-1){
            System.out.println("logged in successfully..");
            if(App.logginUser.getUserType()==UserType.JOB_SEEKER){
                //seeker home
                seeker_home();
            }else{
                //provider home
                provider_home();
            }
        } 
        else System.out.println("something wrong...\n\n");
    }

    public static void signup_view() throws IOException,  NoSuchAlgorithmException, ParseException{
        try{
            System.out.println("-------------------REGISTER-------------------");
            System.out.print("First name: ");
            String firstName = reader.readLine();
            System.out.print("Last name: ");
            String lastName = reader.readLine();
            System.out.print("Email: ");
            String email = reader.readLine();
            System.out.print("Password: ");
            String password = reader.readLine();
            System.out.print("Register as job seeker/job_provider(1/2): ");
            String user_type_input = reader.readLine();
            UserType userType=user_type_input.equals("1")?UserType.JOB_SEEKER:UserType.JOB_PROVIDER;

            String companyName=null,designation=null,companyLocation=null;//only for job_provider
            if(userType.equals(UserType.JOB_PROVIDER)){
                System.out.print("Join an existing company/Create a new Company(1/2):   ");
                String company_type_input = reader.readLine();
                if(company_type_input.equals("1")){//join
                    
                    do{
                        System.out.print("Company Name: ");
                        companyName = reader.readLine();  
                         
                    }while(Company.getCompanyWithName(companyName)==-1);
                    System.out.print("Designation: ");
                    designation = reader.readLine();
                }else{//create
                    System.out.print("Company Name: ");
                    companyName = reader.readLine();
                    System.out.print("Company location: ");
                    companyLocation = reader.readLine();
                }
            }
            int id=User.signUp(firstName, lastName, email, App.hashPassword(password), userType, companyName, designation, companyLocation);
            if(id!=-1){
                System.out.println("Registeration Successfull");
                User.login(email, password);
                base_profile_view();
            }
        }catch(PortalException e){
            System.out.println(e.getMessage());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }



    public static void provider_home() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        JobProvider seeker=(JobProvider)logginUser;
        System.out.println("-------------------Home-------------------");  
        System.out.println("1:  Profile");  
        System.out.println("2:  job feed"); 
        System.out.println("3:  post a job");
        if(seeker.getDesignation().equals("owner"))
                System.out.println("4:  Company profile"); 
        System.out.println("0: logout");
        switch (reader.readLine()) {
            case "1":
                provider_profile_view();
                break;
            case "2":
                provider_feed();
                break;
            case "3":
                post_job_view();
                break;
            case "4":
                company_profile();
                break;
            case "0":
                log_out();
            default:
                
        }
    }

    private static void log_out() throws NoSuchAlgorithmException, IOException, SQLException, ParseException {
        logginUser=null;
        current_application=null;
        current_company=null;
        current_education=null;
        current_employment=null;
        current_project=null;
        current_review=null;
        current_job=null;

        App.id=-1;
        user_id=-1;

        open_view();
    }

    public static boolean checkLogin(){
        if(App.id==-1 || logginUser==null) return false;
        return true;
    }

    public static void update_password_view() throws IOException, NoSuchAlgorithmException, SQLException, ParseException{
        String current,new_password,confirm_password;
        System.out.print("current password: ");
        current=reader.readLine(); 
        System.out.print("new password: "); 
        new_password=reader.readLine();
        System.out.print("confirm new password: "); 
        confirm_password=reader.readLine();
        if(new_password.equals(confirm_password)){
            if(logginUser.validatePassword(App.hashPassword(current))){
                logginUser.updatePassword(App.hashPassword(new_password));
            }else{
                System.err.println("\nCurrent Password does not match!\n");
                update_password_view();
            }
        }else{
            System.err.println("\n New Passoword does not match with Confirm Password!\n");
            update_password_view();
        }
        if(logginUser.getUserType().equals(UserType.JOB_SEEKER)) seeker_profile_view();
        else provider_profile_view();
    }

    public static void seeker_home() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        logginUser.generateProfile();
        System.out.println("-------------------Home-------------------");  
        System.out.println("1:  Profile"); 
        System.out.println("2:  My Jobs"); 
        System.out.println("3:  My reviews"); 
        System.out.println("4:  Job Feeds"); 
        System.out.println("5:  Company Feeds");
        System.out.println("6:  logout"); 
        String home_input=reader.readLine();
        switch (home_input) {
            case "1":
                seeker_profile_view();
                break;
            case "2":
                my_jobs_view();
                break;
            case "3":
                my_reviews_view();
                break;
            case "4":
                seeker_feed();
            case "5":
                company_feed();
            case "6":
                log_out();
            default:
                break;
        }
    }

    public static void provider_profile_view() throws SQLException, IOException, ParseException, NoSuchAlgorithmException{
        System.out.println("-------------------Profile-------------------");
        System.out.println(logginUser.toString());
        logginUser.generateProfile();
        System.err.println("profile generated...");
        System.out.println("-------------------Profile-------------------");
        System.out.println("1:  update base profile"); 
        if(!((JobProvider)logginUser).getDesignation().equals("owner"))
            System.out.println("2:  update designation"); 
        System.out.println("0: Change Password");
        System.out.println("Press any key to back"); 
        switch (reader.readLine()) {
            case "1":
                base_profile_view();
                break;
            
            case "2":
                System.out.println("Designation: "+((JobProvider)logginUser).getDesignation());
                System.out.print("update designation: ");
                ((JobProvider)logginUser).updateDesignation(reader.readLine());
                System.out.println("designation successfully updated.   "+"\nDesignation:"+((JobProvider)logginUser).getDesignation());
                break;
            
            case "3":
                System.out.println("Accompolishments:   "+((JobSeeker)logginUser).getAccompolishments()+"\n"+"update accompolishments to: ");
                ((JobSeeker)logginUser).updateAccomplishments(reader.readLine());
                System.err.println("\nAccomplishments successfully updated\n");
                System.err.println("\nupdated accompolishments: "+((JobSeeker)logginUser).getAccompolishments()+"\n");
                break;

            case "0":
                update_password_view();
                break;
        
            default:
                provider_home();
                break;
        }
    }

    public static void company_profile() throws IOException, NoSuchAlgorithmException, SQLException, ParseException{
        System.out.println("-------------------Company Profile-------------------");
        current_company=((JobProvider)logginUser).getCompany();
        System.out.println(current_company.show_details());

        String founded,industry,location,logo,name,sector,size,from,to;

        System.out.println("update Company Name  to:");
        name=reader.readLine();
        if(!name.isEmpty()) current_company.setName(name); 

        System.out.println("update Company Location to:");
        location=reader.readLine();
        if(!location.isEmpty()) current_company.setLocation(location);

        System.out.println("update logo to:");
        logo=reader.readLine();
        if(!logo.isEmpty()) current_company.setLogo(logo);

        System.out.println("update Sector  to:");
        sector=reader.readLine();
        if(!sector.isEmpty())  current_company.setSector(sector);

        System.out.println("update Industry  to:");
        industry=reader.readLine();
        if(!industry.isEmpty()) current_company.setIndustry(industry);

        System.out.println("update Founded(year):");
        founded=reader.readLine();
        if(!founded.isEmpty()) current_company.setFounded(Integer.parseInt(founded));

        System.out.println("update Size:");
        size=reader.readLine();
        if(!size.isEmpty()) current_company.setSize(Integer.parseInt(size));

        System.out.println("update Revenue From:");
        from=reader.readLine();
        if(!from.isEmpty()) current_company.getRevenue().setFrom(new BigDecimal(from));

        System.out.println("update Revenue To:");
        to=reader.readLine();
        if(!to.isEmpty()) current_company.getRevenue().setTo(new BigDecimal(to));


        try {

            conn.setAutoCommit(false);

            if(!from.isEmpty() || !to.isEmpty()) current_company.getRevenue().updatePay();
            current_company.updateCompany();

            conn.commit();
        } catch (SQLException e) {
            try {
                e.printStackTrace();
                conn.rollback();
                System.out.println("Transaction rolled back at App.company_profile()");
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }finally{
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("Company updated Succefully.."+"\nCompany updated: \n"+current_company.show_details());
        provider_home();
    }
    
    public static void edit_job_view() throws NumberFormatException, NoSuchAlgorithmException, IOException, SQLException, ParseException{
        System.out.println("-------------------This feature is only available to membership!-------------------");
        provider_job_page();
        
    }
    public static void provider_job_page() throws IOException, SQLException, NumberFormatException, NoSuchAlgorithmException, ParseException{
        System.out.println("1:  view full job"); 
        System.out.println("2:  edit this job"); 
        System.out.println("3:  view candidates");
        System.out.println("4:  update job status");
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                System.out.println(current_job.show_details());
                provider_job_page();
            case "2":
                edit_job_view();
                break;
            case "3":
                candiates_view();
                break;
            case "4":
                System.out.println("-------------------update Open/Pause/Close  -------------------");
                System.out.println("1:  to open"); 
                System.out.println("2:  to pause"); 
                System.out.println("3:  to close"); 
                switch (reader.readLine()) {
                    case "1":
                        current_job.updateJobStatus("OPEN");
                        break;
                    case "2":
                        current_job.updateJobStatus("PAUSE");
                        break;
                    case "3":
                        current_job.updateJobStatus("CLOSE");
                        break;
                
                    default:
                        break;
                }
                provider_job_page();
                break;
            default:
                provider_feed();
                break;
        }
    }
    public static void provider_application_view() throws IOException, SQLException, NumberFormatException, NoSuchAlgorithmException, ParseException{
        current_application.getJobSeeker().generateProfile();
        System.out.println("1:  view applicant details"); 
        System.out.println("2:  update this application"); 
        System.out.println("3:  view full application details");
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                System.out.println(current_application.getJobSeeker().toString()+"\n\n");
                provider_application_view();
            case "2":
                System.out.println("-------------------update Active//Reviewed/Rejected/Hired  -------------------");
                System.out.println("1:  to hired"); 
                System.out.println("2:  to reviewed"); 
                System.out.println("3:  to rejected"); 
                switch (reader.readLine()) {
                    case "1":
                        current_application.updateStatus("HIRED");
                        break;
                    case "2":
                        current_application.updateStatus("REVIEWED");
                        break;
                    case "3":
                        current_application.updateStatus("REJECTED");
                        break;
                
                    default:
                        break;
                }
                System.err.println("Status Updated: "+current_application.getApplicationStatus()+"\n");
                provider_application_view();
                break;
            case "3":
                current_application.getJob().generateQuestions();
                System.out.println(current_application.show_details());
                provider_application_view();
                break;
            default:
                candiates_view();
                break;
        }
    }
    public static void candiates_view() throws SQLException, IOException, NumberFormatException, NoSuchAlgorithmException, ParseException{
        System.out.println("-------------------Candidates-------------------");
        System.out.println("1:  Filter"); 
        System.out.println("2: Non Filter"); 
        System.out.println("Press any key to go back!");
        String jobstatusFilter="";
        HashMap<String,Integer> sortFilter=new HashMap<>();
        switch (reader.readLine()) {
            case "1":
                System.out.println("-------------------Active//Reviewed/Rejected/Hired  -------------------");
                System.out.println("1:  show active candidates "); 
                System.out.println("2:  show reviewed candidates"); 
                System.out.println("3:  show rejected candidates"); 
                System.out.println("4:  show hired candidates"); 
                switch (reader.readLine()) {
                    case "1":
                        jobstatusFilter="ACTIVE";
                        break;
                    case "2":
                        jobstatusFilter="REVIEWED";
                        break;
                    case "3":
                        jobstatusFilter="REJECTED";
                        break;
                    case "4":
                        jobstatusFilter="HIRED";
                        break;
                
                    default:
                        break;
                }

                System.out.println("------------------- Sort type -------------------");

                System.out.println("1:  First NAme ascending "); 
                System.out.println("2:  First Name ascending"); 
                System.out.println("3:  date applied ascending"); 
                System.out.println("4:  date applied descending"); 
                
                switch (reader.readLine()) {
                    case "1":                    
                        sortFilter.put("first_name", 1);
                        break;
                    case "2":
                        sortFilter.put("first_name", -1);
                        break;
                    case "3":
                        sortFilter.put("appliedAt", 1);
                        break;
                    case "4":
                        sortFilter.put("appliedAt",-1);
                        break;
                    default:
                        break;
                }

                break;
            case "2":
                break;
            default:
                provider_job_page();
                break;
        }
        ArrayList<Application> applications=current_job.getApplications(jobstatusFilter, sortFilter);
        for(int i=0;i<applications.size();i++){
            System.out.println(i+1+":   "+applications.get(i).toString());
        }
        if(!applications.isEmpty()){
            System.err.println("Select and view");
            current_application=applications.get(Integer.parseInt(reader.readLine())-1);
            current_application.generateAnswers();
            provider_application_view();
        }else{
            System.err.println("applications not found! ");
            provider_job_page();
        }
       
    }

    public static void post_job_view() throws IOException, SQLException, NoSuchAlgorithmException, ParseException{
        Job job=new Job();
        System.out.print("Job title:  ");
        job.setJobTitle(reader.readLine());
        System.out.print("Job Description:  ");
        job.setJobDescription(reader.readLine());
        System.out.print("Location Type:  ");
        job.setLocationType(reader.readLine());
        System.out.print("Job Location:  ");
        job.setLocation(reader.readLine());
        System.out.print("Full time/Part time:  ");
        job.setFullOrPartTime(reader.readLine());
        System.out.print("Job Openings:  ");
        job.setOpenings(Integer.parseInt(reader.readLine()));
        System.out.print("Education Level:  ");
        job.setEducationLevel(reader.readLine());
        System.out.print("Candidate Profile:  ");
        job.setCandidateProfile(reader.readLine());
        System.out.println("pay per month:");
        System.out.print("\tFrom:  ");
        String from=reader.readLine();
        System.out.print("\tTo:  ");
        String to=reader.readLine();
        job.setPay(new Pay(-1, new BigDecimal(from), new BigDecimal(to), "MONTHLY"));
        
        
        System.out.println("------------select and add by typing y/n job types-------------------");
        for (Map.Entry<Integer, String> m : job_types.entrySet()) {
            System.out.println(m.getKey()+":    "+m.getValue()+"(y/n)");
            if(reader.readLine().toLowerCase().equals("y")) job.addJobTypeId(m.getKey());
        }


        System.out.println("------------select and add by typing y/n job schedules-------------------");
        for (Map.Entry<Integer, String> m : job_schedules.entrySet()) {
            System.out.println(m.getKey()+":    "+m.getValue()+"(y/n)");
            if(reader.readLine().toLowerCase().equals("y")) job.addJobScheduleId(m.getKey());
        }


        System.out.println("------------Ask your applicant questions-------------------");
        System.out.println("Do you want to add questions(y/n)");
        if(reader.readLine().toLowerCase().equals("y")){
            do{
                job.addQuestionsString(reader.readLine()+"?");
                System.out.println("Do you want to add one more(y/n)");
            }while(reader.readLine().toLowerCase().equals("y"));
        }

        ((JobProvider)logginUser).postJob(job);
        provider_feed();

    } 
    public static void provider_feed() throws NumberFormatException, IOException, SQLException, NoSuchAlgorithmException, ParseException{
        System.out.println("-------------------Feed-------------------");
        System.out.println("1:  Filter"); 
        System.out.println("2: Non Filter"); 
        System.out.println("Press any key to go back!");

        ArrayList<Job> feed_jobs;
        String title,location;
        Integer daysFilter=-1,salaryFilter=-1;
        HashMap<String,String> searchFilter=new HashMap<>();
        HashMap<String,Integer> sortFilter=new HashMap<>();
        switch (reader.readLine()) {
            case "1":
                System.out.println("---------------Filter By Following fields if not leave blank---------------");
                System.out.print("Job title:  "); //title
                title=reader.readLine();
                if(!title.isEmpty()) searchFilter.put("jobs.title", title);

                System.out.print("Location:  "); //location 
                location=reader.readLine();
                if(!location.isEmpty()) searchFilter.put("jobs.location", location);


                System.out.println("------------------- Sort type -------------------");

                System.out.println("1:  job title ascending "); 
                System.out.println("2:  job title ascending"); 
                System.out.println("3:  date posted ascending"); 
                System.out.println("4:  date posted descending"); 
                
                switch (reader.readLine()) {
                    case "1":                    
                        sortFilter.put("jobs.title", 1);
                        break;
                    case "2":
                        sortFilter.put("jobs.title", -1);
                        break;
                    case "3":
                        sortFilter.put("jobs.postedAt", 1);
                        break;
                    case "4":
                        sortFilter.put("jobs.postedAt",-1);
                        break;
                    default:
                        break;
                }




                System.out.println("-------------------Open/Pause/Close  -------------------");
                System.out.println("1:  show open jobs "); 
                System.out.println("2:  show paused jobs"); 
                System.out.println("3:  show closed jobs"); 
                switch (reader.readLine()) {
                    case "1":
                        searchFilter.put("job_status", "OPEN");
                        break;
                    case "2":
                        searchFilter.put("job_status", "PAUSE");
                        break;
                    case "3":
                        searchFilter.put("job_status", "CLOSE");
                        break;
                
                    default:
                        break;
                }

                System.out.println("------------------- Date posted -------------------");
                HashMap<Integer,Integer> dHashMap=new HashMap<>();
                dHashMap.put(1, 1);
                dHashMap.put(2, 3);
                dHashMap.put(3, 7);
                dHashMap.put(4, 14);
                dHashMap.put(5, 28);
                System.out.println("1:  Last 24 hours "); 
                System.out.println("2:  Last 3 days"); 
                System.out.println("3:  Last 7 days"); 
                System.out.println("4:  Last 14 days"); 
                System.out.println("5:  Last 28 days"); 
                daysFilter=dHashMap.get(Integer.parseInt(reader.readLine()));
                break;
            case "2":
                break;
            default:
                provider_home();
                break;
        }
        feed_jobs=logginUser.getJobsFeed(searchFilter, sortFilter, daysFilter, salaryFilter);
        if(!feed_jobs.isEmpty()){
            System.out.println("------------Select and View--------------");
            for(int i=0;i<feed_jobs.size();i++){
                System.out.println(i+1+":  "+feed_jobs.get(i).toString());
            }
            current_job=feed_jobs.get(Integer.parseInt(reader.readLine())-1);
            current_job.generateQuestions();
            provider_job_page();
        }
        else{
            System.err.println("Please post any Job!");
            provider_home();
        }
    }

    public static void base_profile_view() throws IOException, ParseException, SQLException, NoSuchAlgorithmException{
        System.err.println(logginUser.toString()+"\n");
        System.out.println("-------------------if nothing to update leave fields as blank-------------------\n");
        String firstName,lastName,email,phone,gender,location,DOB;
        System.out.println("update First Name to:");
        firstName=reader.readLine();
        if(!firstName.isEmpty()) logginUser.setFirstName(firstName);

        System.out.println("update Last Name to:");
        lastName=reader.readLine();
        if(!lastName.isEmpty()) logginUser.setLastName(lastName);

        System.out.println("update Email to:");
        email=reader.readLine();
        if(!email.isEmpty()) logginUser.setEmail(email);

        System.out.println("update Gender  to:");
        gender=reader.readLine();
        if(!gender.isEmpty()) logginUser.setGender(gender);

        System.out.println("update Location  to:");
        location=reader.readLine();
        if(!location.isEmpty()) logginUser.setLocation(location);

        System.out.println("update Phone  to:");
        phone=reader.readLine();
        if(!phone.isEmpty()) logginUser.setPhone(phone);

        System.out.println("update DOB(dd-mm-yyyy)  to:");
        DOB=reader.readLine();
        if(!DOB.isEmpty()){
            DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
            java.util.Date date = formatter.parse(DOB);
            java.sql.Date newDOB = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
            logginUser.setDOB(newDOB);
        }
        logginUser.updateBaseProfile();
        System.out.println("profile updated successfully..");
        System.out.println("\nupdated profile:\n"+logginUser.toString()+"\n");

        if(logginUser.getUserType().equals(UserType.JOB_SEEKER)) seeker_profile_view();
        else provider_profile_view();
    }

    //------------------------------------------------SEEKER---------------------------------------------------

    public static void education_list_view() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        System.out.println(((JobSeeker)logginUser).getEducations()+"\n\n");
        System.out.println("1:  Add Education");
        System.out.println("2:  Update Education");
        System.out.println("Press any key to go back!");

        switch (reader.readLine()) {
            case "1":
                Education education=new Education();
                System.out.println("Education Level:  ");
                education.setEducation(reader.readLine());
                System.out.println("Course name:  ");
                education.setCourse(reader.readLine());
                System.out.println("Institution:  ");
                education.setInstitute(reader.readLine());
                System.out.println("Course Type:  ");
                System.out.println("1: Full Time");
                System.out.println("2: Part Time");
                switch (reader.readLine()) {
                    case "1":
                        education.setCourseType("Full Time");
                        break;
                    case "2":
                        education.setCourseType("Part Time");
                        break;
                    default:
                        break;
                }
                System.out.println("Grade:  ");
                education.setGrade(Float.parseFloat(reader.readLine()));
                System.out.println("Passout(year):  ");
                education.setPassout(Integer.parseInt(reader.readLine()));
                ((JobSeeker)logginUser).addEducation(education);
                System.err.println("Education successfully added...");
                System.err.println("added education:    "+education.toString());
                education_list_view();
                break;
            case "2":
                education_update_view();
                break;
            default:
                seeker_additional_profile_view();
                break;
        }
    }
    public static void employment_list_view() throws IOException, ParseException, SQLException, NoSuchAlgorithmException{
        System.out.println(((JobSeeker)logginUser).getEmployments()+"\n\n");
        System.out.println("1:  Add Employment");
        System.out.println("2:  Update Employment");
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                String start,end;
                Employment employment=new Employment();
                System.out.println("Organization:  ");
                employment.setOrganization(reader.readLine());
                System.out.println("Designation:  ");
                employment.setDesignation(reader.readLine());
                System.out.println("start(year):  ");
                start=reader.readLine();
                if(!start.isEmpty()){
                    DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                    java.util.Date date = formatter.parse(start);
                    java.sql.Date newStart = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                    employment.setStart(newStart);
                }
                System.out.println("end(if currently working leave blank):  ");
                end=reader.readLine();
                if(!end.isEmpty()){
                    DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                    java.util.Date date = formatter.parse(start);
                    java.sql.Date newEnd = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                    employment.setEnd(newEnd);
                }else employment.setEnd(nullDate);

                ((JobSeeker)logginUser).addEmployment(employment);
                System.err.println("Employment successfully added...");
                employment_list_view();
                break;
            case "2":
                employment_update_view();
                break;
            default:
                seeker_additional_profile_view();
                break;
        }
    }
    public static void project_list_view() throws IOException, ParseException, SQLException, NoSuchAlgorithmException{
        System.out.println(((JobSeeker)logginUser).getProjects()+"\n\n");
        System.out.println("1:  Add Project");
        System.out.println("2:  Update Project");
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                String start,end;
                Project project=new Project();
                System.out.println("Title:  ");
                project.setTitle(reader.readLine());
                System.out.println("Client:  ");
                project.setClient(reader.readLine());
                System.out.println("Status of the project in words:  ");
                project.setStatus(reader.readLine());
                System.out.println("Link(Github/Drive):  ");
                project.setLink(reader.readLine());
                System.out.println("Details of the project:  ");
                project.setDetail(reader.readLine());

                System.out.println("start(year):  ");
                start=reader.readLine();
                if(!start.isEmpty()){
                    DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                    java.util.Date date = formatter.parse(start);
                    java.sql.Date newStart = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                    project.setStart(newStart);
                }
                System.out.println("end(if currently working leave blank):  ");
                end=reader.readLine();
                if(!end.isEmpty()){
                    DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                    java.util.Date date = formatter.parse(start);
                    java.sql.Date newEnd = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                    project.setEnd(newEnd);
                }else project.setEnd(nullDate);

                ((JobSeeker)logginUser).addProject(project);
                System.err.println("Project successfully added...");
                project_list_view();
                break;
            case "2":
                project_update_view();
                break;
            default:
                seeker_additional_profile_view();
                break;
        }

    }

    public static void education_update_view() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        System.out.println("App.education_update_view()");
        for (Education education : ((JobSeeker)logginUser).getEducations()) {
            System.out.println(education.toString()+"\n\n");
            System.out.println("1:  Update Education");
            System.out.println("2:  Delete Education");
            System.out.println("press any key to skip");

            switch (reader.readLine()) {
                case "1":
                    System.out.println("-------------------if nothing to update leave fields as blank-------------------\n");
                    String course,coursetype,education_level,institution,grade,passout;
                    System.out.println("update Course  to:");
                    course=reader.readLine();
                    if(!course.isEmpty()) education.setCourse(course); 
            
                    System.out.println("update Course Type(Full Time/Part Time) to:");
                    coursetype=reader.readLine();
                    if(!coursetype.isEmpty()) education.setCourseType(coursetype);
            
                    System.out.println("update Education Level to:");
                    education_level=reader.readLine();
                    if(!education_level.isEmpty()) education.setEducation(education_level);
            
                    System.out.println("update Instituion  to:");
                    institution=reader.readLine();
                    if(!institution.isEmpty())  education.setInstitute(institution);
            
                    System.out.println("update Passout(year)  to:");
                    passout=reader.readLine();
                    if(!passout.isEmpty()) education.setPassout(Integer.parseInt(passout));

                    System.out.println("update Grade to:");
                    grade=reader.readLine();
                    if(!grade.isEmpty()) education.setGrade(Float.parseFloat(grade));

                    education.updateEducation();
                    System.out.println("education updated successfully..");
                    System.out.println("updated eduation:\n"+education.toString()+"\n");
                    education_list_view();
                    break;
                case "2":
                    ((JobSeeker)logginUser).deleteEducation(education);
                    education_list_view();
                    break;
                default:
                    education_list_view();
                    break;
            }
        }
        education_list_view();
    }
    public static void employment_update_view() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        for (Employment employment : ((JobSeeker)logginUser).getEmployments()) {
            System.out.println(employment.toString()+"\n\n");
            System.out.println("1:  Update Employment");
            System.out.println("2:  Delete Employment");
            System.out.println("press any key to skip");
        
            switch (reader.readLine()) {
                case "1":
                    System.out.println("-------------------if nothing to update leave fields as blank-------------------\n");
                    String start,end,designation,organization;

                    System.out.println("update Designation  to:");
                    designation=reader.readLine();
                    if(!designation.isEmpty()) employment.setDesignation(designation);
            
            
                    System.out.println("update Organization  to:");
                    organization=reader.readLine();
                    if(!organization.isEmpty()) employment.setOrganization(organization);


                    System.out.println("update start(dd-mm-yyyy)  to:");
                    start=reader.readLine();
                    if(!start.isEmpty()){
                        DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        java.util.Date date = formatter.parse(start);
                        java.sql.Date newStart = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                        employment.setStart(newStart);
                    }

                    System.out.println("update end(dd-mm-yyyy)  to:");
                    end=reader.readLine();
                    if(!end.isEmpty()){
                        DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        java.util.Date date = formatter.parse(end);
                        java.sql.Date newEnd = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                        employment.setEnd(newEnd);
                    }

        
                    employment.updateEmployment();
                    System.out.println("employment updated successfully..");
                    System.out.println("updated employment:\n"+employment.toString()+"\n");
                    employment_list_view();
                    break;
                case "2":
                    ((JobSeeker)logginUser).deleteEmployment(employment);
                    employment_list_view();
                    break;
                default:
                    employment_list_view();
                    break;
            }
        }
        employment_list_view();
    }
    public static void project_update_view() throws IOException, ParseException, SQLException, NoSuchAlgorithmException{
        for (Project project : ((JobSeeker)logginUser).getProjects()) {
            System.out.println(project.toString()+"\n\n");
            System.out.println("1:  Update Project");
            System.out.println("2:  Delete Project");
            System.out.println("press any key to skip");
            switch (reader.readLine()) {
                case "1":
                    System.out.println("-------------------if nothing to update leave fields as blank-------------------\n");
                    String title,client,status,link,detail,start,end;
        
                    System.out.println("update Title  to:");
                    title=reader.readLine();
                    if(!title.isEmpty()) project.setTitle(title);
            
                    System.out.println("update Client  to:");
                    client=reader.readLine();
                    if(!client.isEmpty()) project.setClient(client);
                    
                    System.out.println("update Status  to:");
                    status=reader.readLine();
                    if(!status.isEmpty()) project.setStatus(status);

                    System.out.println("update Link  to:");
                    link=reader.readLine();
                    if(!title.isEmpty()) project.setLink(link);
            
                    System.out.println("update Details  to:");
                    detail=reader.readLine();
                    if(!detail.isEmpty()) project.setDetail(detail);
        
        
                    System.out.println("update start(dd-mm-yyyy)  to:");
                    start=reader.readLine();
                    if(!start.isEmpty()){
                        DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        java.util.Date date = formatter.parse(start);
                        java.sql.Date newStart = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                        project.setStart(newStart);
                    }
        
                    System.out.println("update end(dd-mm-yyyy)  to:");
                    end=reader.readLine();
                    if(!end.isEmpty()){
                        DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        java.util.Date date = formatter.parse(end);
                        java.sql.Date newEnd = new java.sql.Date(date.getTime()); // convert java.util.date to java.sql.date
                        project.setEnd(newEnd);
                    }
        
                    project.updateProject();
                    System.out.println("project updated successfully..");
                    System.out.println("updated project:\n"+project.toString()+"\n");
                    project_list_view();
                    break;
                case "2":
                    ((JobSeeker)logginUser).deleteProject(project);
                    project_list_view();
                    break;
                default:
                    project_list_view();
                    break;
            }
        }
        project_list_view();
    }

    public static void language_list_view() throws NoSuchAlgorithmException, IOException, SQLException, ParseException{
        JobSeeker jobSeeker=((JobSeeker)logginUser);
        System.out.println("Languages:\t"+jobSeeker.getLanguages()+"\n\n");
        System.out.println("1:  Add/Delete Language");
        System.out.println("Press any key to go back!");

        switch (reader.readLine()) {
            case "1":
                System.out.println("-------------------------------Search languages to Add/Delete------------------------------");
                System.out.print("Enter keyword to search: ");
                ArrayList<ArrayList<String>> searchedLanguages=jobSeeker.searchLanguages(reader.readLine());
                
                int language_input=-1;
                do{
                    System.out.println("Press 0 to skip....");
                    for(int i=0;i<searchedLanguages.size();i++){
                        System.out.println(i+1+": "+searchedLanguages.get(i).get(1));
                    }
                    language_input=Integer.parseInt(reader.readLine());
                    if(language_input>0 && language_input<=searchedLanguages.size()){

                        ArrayList<String> t=searchedLanguages.get(language_input-1);

                        System.out.println("1:  Add this Language");
                        if(jobSeeker.getLanguages().contains(t.get(1)))
                            System.out.println("2:  Remove this Language");
                        System.err.println("Press Any Key to Skip...");
                        switch (reader.readLine()) {
                            case "1":
                                jobSeeker.addLanguage(Integer.parseInt(t.get(0)),t.get(1));
                                break;
                            case "2":
                                jobSeeker.removeLanguage(Integer.parseInt(t.get(0)),t.get(1));
                                break;
                            default:
                                language_input=0;
                                break;
                        }
                    } 
                    else{
                        System.err.println("wrong input");
                        language_input=1;
                    } 
                }while(language_input<=0);
                language_list_view();
                break;
            default:
                seeker_profile_view();
                break;
        }
    }

    public static void skill_list_view() throws NoSuchAlgorithmException, IOException, SQLException, ParseException{
        JobSeeker jobSeeker=((JobSeeker)logginUser);
        System.out.println("Skills:\t"+jobSeeker.getKeySkills()+"\n\n");
        System.out.println("1:  Add/Delete Skill");
        System.out.println("Press any key to go back!");
    
        switch (reader.readLine()) {
            case "1":
                System.out.println("-------------------------------Search skills to Add/Delete------------------------------");
                System.out.print("Enter keyword to search: ");
                ArrayList<ArrayList<String>> searchedSkills=jobSeeker.searchSkills(reader.readLine());
                
                int skill_input=-1;
                do{
                    System.out.println("Press 0 to skip....");
                    for(int i=0;i<searchedSkills.size();i++){
                        System.out.println(i+1+": "+searchedSkills.get(i).get(1));
                    }
                    skill_input=Integer.parseInt(reader.readLine());
                    if(skill_input>0 && skill_input<=searchedSkills.size()){

                        ArrayList<String> t=searchedSkills.get(skill_input-1);

                        System.out.println("1:  Add this Skill");
                        if(jobSeeker.getKeySkills().contains(t.get(1)))
                            System.out.println("2:  Remove this Skill");
                        System.err.println("Press Any Key to Skip...");
                        switch (reader.readLine()) {
                            case "1":
                                jobSeeker.addSkill(Integer.parseInt(t.get(0)),t.get(1));
                                break;
                            case "2":
                                jobSeeker.removeSkill(Integer.parseInt(t.get(0)),t.get(1));
                                break;
                            default:
                                skill_input=0;
                                break;
                        }
                    } 
                    else{
                        System.err.println("wrong input");
                        skill_input=1;
                    } 
                }while(skill_input<=0);
                skill_list_view();
                break;
            default:
                seeker_profile_view();
                break;
        }
    }

    public static void seeker_additional_profile_view() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        System.out.println("-------------------Profile-------------------");
        System.out.println("1:  update education"); 
        System.out.println("2:  update employment"); 
        System.out.println("3:  update project"); 
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                education_list_view();
                break;
            
            case "2":
                employment_list_view();
                break;

            case "3":
                project_list_view();
                break;

            default:
                seeker_profile_view();
                break;
        }
    }

    public static void seeker_profile_view() throws IOException, ParseException, SQLException, NoSuchAlgorithmException{
        logginUser.generateProfile();
        
        System.out.println("-------------------Profile-------------------");
        System.out.println("1:  update base profile"); 
        System.out.println("2:  update education/employment/project"); 
        System.out.println("3:  update achievements"); 
        System.out.println("4:  update languages"); 
        System.out.println("5:  update keyskills"); 
        System.out.println("6:  Show your profile");
        System.out.println("0: Change Password");
        System.out.println("Press any number to skip"); 
        switch (reader.readLine()) {
            case "1":
                base_profile_view();
                break;
            
            case "2":
                seeker_additional_profile_view();
                break;
            
            case "3":
                System.out.println("Accompolishments:   "+((JobSeeker)logginUser).getAccompolishments()+"\n"+"update accompolishments to: ");
                ((JobSeeker)logginUser).updateAccomplishments(reader.readLine());
                System.err.println("\nAccomplishments successfully updated\n");
                System.err.println("\nupdated accompolishments: "+((JobSeeker)logginUser).getAccompolishments()+"\n");
                seeker_profile_view();
                break;
            case "4":
                language_list_view();
                break;
            case "5":
                skill_list_view();
            case "6":
                System.err.println(logginUser.toString());
                seeker_profile_view();
            case "0":
                update_password_view();
                break;
            default:
                seeker_home();
                break;
        }
    }
    public static void my_jobs_view() throws SQLException, NumberFormatException, IOException, ParseException, NoSuchAlgorithmException{//job seeker applied jobs view
        System.out.println("-------------------My Jobs-------------------");
        JobSeeker seeker=(JobSeeker)logginUser;

        System.out.println("1:  Filter"); 
        System.out.println("2: Non Filter"); 
        System.out.println("Press any key to go back!");

        ArrayList<Application> my_applications;
        Integer daysFilter=-1;
        HashMap<String,Integer> sortFilter=new HashMap<>();
        HashMap<String,String> searchFilter=new HashMap<>();
        String title,name,location;
        switch (reader.readLine()) {
            case "1":
                
            System.out.println("---------------Filter By Following fields if not leave blank---------------");
            System.out.print("Job title:  "); //title
            title=reader.readLine();
            if(!title.isEmpty()) searchFilter.put("jobs.title", title);

            System.out.print("Company name:  "); //name
            name=reader.readLine();
            if(!name.isEmpty()) searchFilter.put("companies.name", name);

            System.out.print("Location:  "); //location 
            location=reader.readLine();
            if(!location.isEmpty()) searchFilter.put("jobs.location",location);
            
            if(!location.isEmpty()) searchFilter.put("jobs.location", location);
                System.out.print("sort by posted date(ASC/DESC/no sort) (a/d/n):  ");
                switch (reader.readLine().toLowerCase()) {
                    case "a":
                        sortFilter.put("appliedAt", 1);
                        break;
                    case "d":
                        sortFilter.put("appliedAt", -1);
                        break;
                    case "n":
                        break;
                    default:
                        break;
                } 
                System.out.println("------------------- Date posted -------------------");
                HashMap<Integer,Integer> dHashMap=new HashMap<>();
                dHashMap.put(1, 1);
                dHashMap.put(2, 3);
                dHashMap.put(3, 7);
                dHashMap.put(4, 14);
                dHashMap.put(5, 28);
                System.out.println("1:  Last 24 hours "); 
                System.out.println("2:  Last 3 days"); 
                System.out.println("3:  Last 7 days"); 
                System.out.println("4:  Last 14 days"); 
                System.out.println("5:  Last 28 days"); 
                System.out.println("Press any number to skip"); 
                daysFilter=dHashMap.get(Integer.parseInt(reader.readLine()));
                break;
            case "2":
                break;
            default:
                seeker_home();
                break;
        }

        my_applications=seeker.getMyApplications(searchFilter,sortFilter, daysFilter);
        if(!my_applications.isEmpty()){
            System.out.println("------------Select and View--------------");
            for(int i=0;i<my_applications.size();i++){
                System.out.println(i+1+":  "+my_applications.get(i).getJob().toString()+" Applied on "+App.dateTimeFormatter.format(my_applications.get(i).getAppliedAt())+"\n"+"Status: "+my_applications.get(i).getApplicationStatus()+"\n");
            }
            current_application=my_applications.get(Integer.parseInt(reader.readLine())-1);
            current_application.generateAnswers();
            seeker_application_view();
        }else{
            System.err.println("No Applications Found!");
            seeker_profile_view();
        }
    }

    public static void seeker_application_view() throws IOException, SQLException, NumberFormatException, ParseException, NoSuchAlgorithmException{
        System.out.println("1:  View this Job"); 
        System.out.println("2: View Appliation");
        System.out.println("3: Delete This Appliation");
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                System.err.println(current_application.getJob().show_details());
                seeker_application_view();
                break;
            case "2":
                current_application.getJob().generateQuestions();
                System.out.println(current_application.show_details());
                seeker_application_view();

                break;
            case "3":
                ((JobSeeker)logginUser).deleteApplication(current_application);
                my_jobs_view();
                break;
            default:
                my_jobs_view();
                break;
        } 
    }

    public static void my_reviews_view() throws SQLException, NumberFormatException, IOException, ParseException, NoSuchAlgorithmException{
        System.out.println("-------------------My Reviews-------------------");
        JobSeeker seeker=(JobSeeker)logginUser;

        System.out.println("1:  Filter"); 
        System.out.println("2: Non Filter"); 
        System.out.println("Press any key to go back!");

        ArrayList<Review> my_reviews;
        Integer daysFilter=-1;
        HashMap<String,Integer> sortFilter=new HashMap<>();
        switch (reader.readLine()) {
            case "1":
                System.out.print("sort by posted date(ASC/DESC/no sort) (a/d/n):  ");
                switch (reader.readLine().toLowerCase()) {
                    case "a":
                        sortFilter.put("reviewedAt", 1);
                        break;
                    case "d":
                        sortFilter.put("reviewedAt", -1);
                        break;
                    case "n":
                        break;
                    default:
                        break;
                } 
                System.out.println("------------------- Date posted -------------------");
                HashMap<Integer,Integer> dHashMap=new HashMap<>();
                dHashMap.put(1, 1);
                dHashMap.put(2, 3);
                dHashMap.put(3, 7);
                dHashMap.put(4, 14);
                dHashMap.put(5, 28);
                System.out.println("1:  Last 24 hours "); 
                System.out.println("2:  Last 3 days"); 
                System.out.println("3:  Last 7 days"); 
                System.out.println("4:  Last 14 days"); 
                System.out.println("5:  Last 28 days"); 
                System.out.println("Press any number to skip"); 
                daysFilter=dHashMap.get(Integer.parseInt(reader.readLine()));
                break;
            case "2":
                break;
            default:
                seeker_home();
                break;
        }
        my_reviews=seeker.getMyReviews(sortFilter, daysFilter);
        System.out.println("------------Select and View--------------");
        if(!my_reviews.isEmpty()){
            for(int i=0;i<my_reviews.size();i++){
                System.out.println(i+1+":  "+my_reviews.get(i).toString());
            }
            //getting job from user
            current_review=my_reviews.get(Integer.parseInt(reader.readLine())-1);
            seeker_review_page();
        }
        else{
            System.err.println("No Reviews Found");
            seeker_profile_view();
        }

    }
    public static void seeker_review_page() throws IOException, SQLException, NumberFormatException, ParseException, NoSuchAlgorithmException{
        System.err.println("\n"+current_review.toString()+"\n");
        System.out.println("1: Edit Review");
        System.out.println("2: Delete Review");
        System.out.println("Press any key to go back!");
        switch (reader.readLine()) {
            case "1":
                review_update_view();
                break;
            case "2":
                ((JobSeeker)logginUser).deleteReview(current_review);
                System.out.println("Review Deleted Successfully...");
                my_reviews_view();
            default:
                my_reviews_view();
                break;
        }
    }
    public static void review_update_view() throws IOException, SQLException, NumberFormatException, ParseException, NoSuchAlgorithmException{
        System.out.println("-------------------if nothing to update leave fields as blank-------------------\n");
        String job_title,ratings,job_status,job_location,review,pros,cons;

        System.out.println("update Job Title  to:");
        job_title=reader.readLine();
        if(!job_title.isEmpty()) current_review.setJobTitle(job_title); 

        System.out.println("update Job Status(Former/Current Employer) to:");
        job_status=reader.readLine();
        if(!job_status.isEmpty()) current_review.setJobStatus(job_status);

        System.out.println("update Job Location to:");
        job_location=reader.readLine();
        if(!job_location.isEmpty()) current_review.setLocation(job_location);

        System.out.println("update Ratings  to:");
        ratings=reader.readLine();
        if(!ratings.isEmpty())  current_review.setRatings(Integer.parseInt(ratings));

        System.out.println("update  Review to:");
        review=reader.readLine();
        if(!review.isEmpty()) current_review.setReview(review);

        System.out.println("update Pros to:");
        pros=reader.readLine();
        if(!pros.isEmpty()) current_review.setPros(pros);

        System.out.println("update Cons to:");
        cons=reader.readLine();
        if(!cons.isEmpty()) current_review.setCons(cons);

        current_review.updateReview();
        System.out.println("review updated successfully..");
        System.out.println("updated review: \n"+current_review.toString()+"\n");
        my_reviews_view();
    }
    public static void seeker_feed() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        System.out.println("-------------------Feed-------------------");
        System.out.println("1:  Filter"); 
        System.out.println("2: Non Filter"); 
        System.out.println("Press any key to go back!");

        ArrayList<Job> feed_jobs;
        String title,name,location;
        Integer from,daysFilter=-1,salaryFilter=-1;
        HashMap<String,String> searchFilter=new HashMap<>();
        HashMap<String,Integer> sortFilter=new HashMap<>();
        switch (reader.readLine()) {
            case "1":
                System.out.println("---------------Filter By Following fields if not leave blank---------------");
                System.out.print("Job title:  "); //title
                title=reader.readLine();
                if(!title.isEmpty()) searchFilter.put("jobs.title", title);

                System.out.print("Company name:  "); //name
                name=reader.readLine();
                if(!name.isEmpty()) searchFilter.put("companies.name", name);

                System.out.print("Job Location:  "); //location 
                location=reader.readLine();
                if(!location.isEmpty()) searchFilter.put("jobs.location", location);

                System.out.print("Salary(y/n):  "); 
                switch (reader.readLine().toLowerCase()) {
                    case "y":
                        System.out.println("-------------------Salry Filter-------------------");
                        HashMap<Integer,Integer> salaryFMap=new HashMap<>();
                        salaryFMap.put(1, 18000);
                        salaryFMap.put(2, 25000);
                        salaryFMap.put(3, 29000);
                        salaryFMap.put(4, 35000);
                        salaryFMap.put(5, 48000);
                        System.out.println("1:  18000+/month"); 
                        System.out.println("2:  25000+/month"); 
                        System.out.println("3:  29000+/month"); 
                        System.out.println("4:  35000+/month"); 
                        System.out.println("5:  48000+/month"); 
                        salaryFilter=salaryFMap.get(Integer.parseInt(reader.readLine()));
                        break;
                    case "n":
                        break;
                    default:
                        break;
                }
                

                System.out.println("------------------- Sort type -------------------");

                System.out.println("1:  job title ascending "); 
                System.out.println("2:  job title descending"); 
                System.out.println("3:  date posted ascending"); 
                System.out.println("4:  date posted descending"); 
                
                switch (reader.readLine()) {
                    case "1":                    
                        sortFilter.put("jobs.title", 1);
                        break;
                    case "2":
                        sortFilter.put("jobs.title", -1);
                        break;
                    case "3":
                        sortFilter.put("jobs.postedAt", 1);
                        break;
                    case "4":
                        sortFilter.put("jobs.postedAt",-1);
                        break;
                    default:
                        break;
                }



                System.out.println("------------------- Date posted -------------------");
                HashMap<Integer,Integer> dHashMap=new HashMap<>();
                dHashMap.put(1, 1);
                dHashMap.put(2, 3);
                dHashMap.put(3, 7);
                dHashMap.put(4, 14);
                dHashMap.put(5, 28);
                System.out.println("1:  Last 24 hours "); 
                System.out.println("2:  Last 3 days"); 
                System.out.println("3:  Last 7 days"); 
                System.out.println("4:  Last 14 days"); 
                System.out.println("5:  Last 28 days"); 
                daysFilter=dHashMap.get(Integer.parseInt(reader.readLine()));
                break;
            case "2":
                break;
            default:
                seeker_home();
                break;
        }
        feed_jobs=logginUser.getJobsFeed(searchFilter, sortFilter, daysFilter, salaryFilter);
        for(int i=0;i<feed_jobs.size();i++){
            System.out.println(i+1+":  "+feed_jobs.get(i).toString());
        }
        //getting job from user
        System.out.println("------------Select and View--------------");
        current_job=feed_jobs.get(Integer.parseInt(reader.readLine())-1);
        current_job.generateQuestions();
        seeker_job_page();

    }


    public static void company_feed() throws IOException, SQLException, ParseException, NoSuchAlgorithmException{
        System.out.println("-------------------Companies Feed-------------------");
        System.out.println("1:  Filter"); 
        System.out.println("2: Non Filter"); 
        System.out.println("Press any key to go back!");

        ArrayList<Company> feed_companies;
        String name,location;
        HashMap<String,String> searchFilter=new HashMap<>();
        switch (reader.readLine()) {
            case "1":
                System.out.println("---------------Filter By Following fields if not leave blank---------------");

                System.out.print("Company name:  "); //name
                name=reader.readLine();
                if(!name.isEmpty()) searchFilter.put("name", name);

                System.out.print("Company Location:  "); //location 
                location=reader.readLine();
                if(!location.isEmpty()) searchFilter.put("location", location);

            case "2":
                break;
            default:
                seeker_home();
                break;
        }
        feed_companies=((JobSeeker)logginUser).getCompaniesFeed(searchFilter);
        for(int i=0;i<feed_companies.size();i++){
            System.out.println(i+1+":  "+feed_companies.get(i).toString());
        }
        //getting job from user
        System.out.println("------------Select and View--------------");
        current_company=feed_companies.get(Integer.parseInt(reader.readLine())-1);
        public_company_view();

    }

    public static void seeker_job_page() throws IOException, SQLException, ParseException, NumberFormatException, NoSuchAlgorithmException{
        System.out.println("1:  view full job"); 
        System.out.println("2:  apply this job"); 
        System.out.println("3:  view company details"); 
        switch (reader.readLine()) {
            case "1":
                System.out.println(current_job.show_details());
                seeker_feed();
            case "2":
                apply_job_view();
                break;
            case "3":
                current_company=current_job.getCompany();
                public_company_view();
                break;
            default:
                break;
        }
    }
    public static void apply_job_view() throws SQLException, IOException, ParseException, NumberFormatException, NoSuchAlgorithmException{
        if(!current_job.checkApplicationExists()){
            ArrayList<String> questions=current_job.generateQuestions();
            ArrayList<String> answers=new ArrayList<>();
            for(int i=0;i<questions.size();i++){
                System.out.println(questions.get(i));
                answers.add(reader.readLine());
            }
            current_job.applyJob(answers, "resume_"+logginUser.getFirstName()+"_"+App.id);
        }
        my_jobs_view();
    }
    public static void public_company_view() throws IOException, SQLException, NumberFormatException, ParseException, NoSuchAlgorithmException{
        System.out.println("1:  view full details of the company"); 
        System.out.println("2:  view all reviews"); 
        System.out.println("3: post a review "); 
        switch (reader.readLine()) {
            case "1":
                System.out.println(current_company.show_details());
                public_company_view();
            case "2":
                company_review_view();
                break;
            case "3":
                if(current_job!=null)
                    current_company=current_job.getCompany();
                post_review_view();
                break;
            default:
                break;
        }
    }
    public static void company_review_view() throws NumberFormatException, IOException, SQLException, ParseException, NoSuchAlgorithmException{
            System.out.println("1:  Filter"); 
            System.out.println("2: Non Filter"); 
            System.out.println("Press any key to go back!");

            ArrayList<Review> feed_reviews;
            String job_title,location;
            Integer daysFilter=-1;
            HashMap<String,String> searchFilter=new HashMap<>();
            HashMap<String,Integer> sortFilter=new HashMap<>();
            switch (reader.readLine()) {
                case "1":
                    System.out.println("---------------Filter By Following fields if not leave blank---------------");
                    System.out.print("Job title:  "); //title
                    job_title=reader.readLine();
                    if(!job_title.isEmpty()) searchFilter.put("job_title", job_title);


                    System.out.print("Location:  "); //location 
                    location=reader.readLine();
                    if(!location.isEmpty()) searchFilter.put("location", location);

                    System.out.print("sort by posted date(ASC/DESC/no sort) (a/d/n):  ");
                    switch (reader.readLine().toLowerCase()) {
                        case "a":
                            sortFilter.put("reviewedAt", 1);
                            break;
                        case "d":
                            sortFilter.put("reviewedAt", -1);
                            break;
                        case "n":
                            break;
                        default:
                            break;
                    } 
                    System.out.println("------------------- Date posted -------------------");
                    HashMap<Integer,Integer> dHashMap=new HashMap<>();
                    dHashMap.put(1, 1);
                    dHashMap.put(2, 3);
                    dHashMap.put(3, 7);
                    dHashMap.put(4, 14);
                    dHashMap.put(5, 28);
                    System.out.println("1:  Last 24 hours "); 
                    System.out.println("2:  Last 3 days"); 
                    System.out.println("3:  Last 7 days"); 
                    System.out.println("4:  Last 14 days"); 
                    System.out.println("5:  Last 28 days"); 
                    int d_i;
                    do{
                        d_i=Integer.parseInt(reader.readLine());
                        daysFilter=dHashMap.get(d_i);

                    }while(d_i<1 || d_i>5);
                    break;
                case "2":
                    break;
                default:
                    break;
            }
            feed_reviews=current_company.getAllReviews(searchFilter, sortFilter, daysFilter);
            if(!feed_reviews.isEmpty()){
                System.out.println("------------Select and View--------------");
                for(int i=0;i<feed_reviews.size();i++){
                    System.out.println(i+1+":  "+feed_reviews.get(i).toString());
                }
                //getting job from user
                current_review=feed_reviews.get(Integer.parseInt(reader.readLine())-1);
                public_company_view();
            }else{
                System.err.println("No Reviews Found!");
                public_company_view();
            }
    }
    public static void post_review_view() throws IOException, SQLException, NumberFormatException, ParseException, NoSuchAlgorithmException{
        Review review=new Review();
        System.out.println("Job title:  ");
        review.setJobTitle(reader.readLine());
        System.out.println("ratings(5):  ");
        review.setRatings(Integer.parseInt(reader.readLine()));
        System.out.println("job location:  ");
        review.setLocation(reader.readLine());
        System.out.println("what your job status:  ");
        System.out.println("1: former employer");
        System.out.println("2: current employer");
        switch (reader.readLine()) {
            case "1":
                review.setJobStatus("Former Employer");
                break;
            case "2":
                review.setJobStatus("Current Employer");
                break;
            default:
                break;
        }
        System.out.println("review:  ");
        review.setReview(reader.readLine());
        System.out.println("Pros:  ");
        review.setPros(reader.readLine());
        System.out.println("Cons:  ");
        review.setCons(reader.readLine());
        current_company.addReview(review);
        my_reviews_view();
    }   

}
