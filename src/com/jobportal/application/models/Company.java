package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
