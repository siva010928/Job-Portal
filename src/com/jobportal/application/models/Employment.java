package com.jobportal.application.models;

import java.util.Date;

public class Employment {
    private Integer experience;
    private String designation,organization;
    private Date start,end=null;
    private boolean isCurrentCompany;

    public Employment(String organization,String designation, Date start, Date end, boolean isCurrentCompany) {
        this.designation = designation;
        this.organization = organization;
        this.start = start;
        this.end = end;
        this.isCurrentCompany = isCurrentCompany;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean isCurrentCompany() {
        return isCurrentCompany;
    }

    public void setCurrentCompany(boolean currentCompany) {
        isCurrentCompany = currentCompany;
    }
}

