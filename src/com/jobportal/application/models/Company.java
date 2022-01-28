package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jobportal.application.App;


public class Company {
    private Integer id,reviews,ratings,founded,size;
    private String name,logo,sector,industry,location;
    private Pay revenue;

    
    public Company(Integer id,Integer reviews, Integer ratings, Integer founded, Integer size, String name, String logo, String sector, String industry, String location, Pay revenue) {
        this.id=id;
        this.reviews = reviews;
        this.ratings = ratings;
        this.founded = founded;
        this.size = size;
        this.name = name;
        this.logo = logo;
        this.sector = sector;
        this.industry = industry;
        this.location = location;
        this.revenue = revenue;
    }

    public void updateCompany(Company company) throws SQLException{
        this.setFounded(company.getFounded());
        this.setIndustry(company.getIndustry());
        this.setLocation(company.getLocation());
        this.setLogo(company.getLogo());
        this.setName(company.getName());
        this.setRevenue(company.getRevenue());
        this.setSector(company.getSector());
        this.setSize(company.getSize());

        //then update this to  db
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE companies SET name=?,logo=?,sector=?,industry=?,size=?,founded=?,location=? WHERE company_id=?");
        stmt.setString(1,this.getName());
        stmt.setString(2, this.getLogo());
        stmt.setString(3, this.getSector());
        stmt.setString(4, this.getIndustry());
        stmt.setInt(5, this.getSize());
        stmt.setInt(6, this.getFounded());
        stmt.setString(7, this.getLocation());
        stmt.setInt(8, this.getId());
        int updatedResults=stmt.executeUpdate();

        this.getRevenue().updatePay();
    }

    // Adding review to the company
    public void addReview(Review review) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO reviews('job_title','ratings','job_status','location','review','pros','cons','job_seeker_id','company_id') VALUES(?,?,?,?,?,?,?,?,?)");
        stmt.setString(1, review.getJobTitle());
        stmt.setInt(2, review.getRatings());
        stmt.setString(3, review.getJobStatus());
        stmt.setString(4, review.getLocation());
        stmt.setString(5, review.getReview());
        stmt.setString(6, review.getPros());
        stmt.setString(7, review.getCons());
        stmt.setInt(8, App.id);
        stmt.setInt(9, this.id);
        int rowsInserted=stmt.executeUpdate();
        this.reviews++;
    }

    // Deleting the company review 
    public void deleteReview(Review review) throws SQLException {
        PreparedStatement stmt=App.conn.prepareStatement("DELETE FROM reviews WHERE review_id=?");
        stmt.setInt(1, review.getId());
        int deletedRows=stmt.executeUpdate();
        this.reviews--;
    }


    //GetAllReviews(company)(default ratings desc)(search jobtitle, location)(rating Filter)
    public ArrayList<Review> getAllReviews(HashMap<String,String> searchFilter,HashMap<String,Integer> sortFilter,Integer daysFilter) throws SQLException{
        //for default showing ratings will be in descending order
        sortFilter.put("ratings", -1);


        StringBuilder query=new StringBuilder("SELECT * FROM reviews WHERE company_id=?");

        //filterring last 7 days like
        if(daysFilter!=-1){
            query.append(" AND ");
            query.append("reviewedAt>(DATE_SUB(CURRENT_DATE,INTERVAL "+daysFilter+" DAY))");
        }

        //filtering for where class of job title,location
        for (Map.Entry<String,String> m : searchFilter.entrySet()) {
            query.append(" AND ");
            query.append(m.getKey());
            query.append("LIKE");
            query.append("%"+m.getValue()+"%");
        }

        //filtering for order by ratings it will be always the size of one
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
        
        stmt.setInt(1,this.getId());//company_id
        ArrayList<Review> reviews=new ArrayList<>();
        ResultSet rS=stmt.executeQuery();
        while(rS.next()){
            
            reviews.add(new Review(rS.getInt("ratings"),rS.getString("review"), rS.getString("pros"), rS.getString("cons"), rS.getString("job_title"), rS.getString("job_status"), rS.getString("location")));

        }
        return reviews;
    }



    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReviews() {
        return this.reviews;
    }

    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    public Integer getRatings() {
        return this.ratings;
    }

    public void setRatings(Integer ratings) {
        this.ratings = ratings;
    }

    public Integer getFounded() {
        return this.founded;
    }

    public void setFounded(Integer founded) {
        this.founded = founded;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSector() {
        return this.sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return this.industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Pay getRevenue() {
        return this.revenue;
    }

    public void setRevenue(Pay revenue) {
        this.revenue = revenue;
    }

        
}
