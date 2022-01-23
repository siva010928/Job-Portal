package com.jobportal.application;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.jobportal.application.models.User;
public class App {
    public static Connection conn;
    public static Integer user_id=-1;
    public static Integer id=-1;//job-provider/job-seeker id
    public static User logginUser;///polymorphsm using method overriding
    public static HashMap<Integer,String> job_types,job_schedules;

    public static void main(String[] args) throws Exception {
        initializeJobtypesAndschedules();
        // System.out.println("siva");
        conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/job_portal","root","Deepika@71199");
        Statement statement=conn.createStatement();
        ResultSet rs=statement.executeQuery("select * from users");
        while(rs.next()){
            System.out.println(rs.getString("first_name"));
        }
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

}
