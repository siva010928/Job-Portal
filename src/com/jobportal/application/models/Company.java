package com.jobportal.application.models;


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
