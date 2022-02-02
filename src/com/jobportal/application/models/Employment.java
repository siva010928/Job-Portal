package com.jobportal.application.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jobportal.application.App;

public class Employment {
    private Integer id;
    private String designation,organization;
    private Date start,end;
    private boolean isCurrentCompany=false;

    public Employment(Integer employment_id,String organization,String designation, Date  start, Date end) {
        this.id=employment_id;
        this.designation = designation;
        this.organization = organization;
        this.start = start;
        //if job seeker works in current employment then he will not give end input and db will store it as null
        if(end.equals(App.nullDate)){
            this.isCurrentCompany=true;
            this.end=App.nullDate;
        }else this.end=end;
    }

    public Employment() {
    }

    //when job provider edits his educations list details
    public void updateEmployment() throws SQLException{

        //then update this to  db
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE employments SET start_date=?,end_date=?,designation=?,organization=? WHERE employment_id=?");
        stmt.setDate(1, this.getStart());

        //if job seeker does not fill end date in this education in edit education then set null in databse
        if(this.getEnd().equals(App.nullDate)){
            stmt.setNull(2, java.sql.Types.DATE);//updating null to education db
            this.setIsCurrentCompany(true);
        }else{
            stmt.setDate(2, this.getEnd());
            this.setIsCurrentCompany(false);
        }
        stmt.setString(3, this.getDesignation());
        stmt.setString(4, this.getOrganization());
        stmt.setInt(5, this.getId());

        int updatedResults=stmt.executeUpdate();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date end) {
        this.end=end.equals(App.nullDate)?App.nullDate:end;
    }

    public boolean isIsCurrentCompany() {
        return this.isCurrentCompany;
    }

    public boolean getIsCurrentCompany() {
        return this.isCurrentCompany;
    }

    public void setIsCurrentCompany(boolean isCurrentCompany) {
        this.isCurrentCompany = isCurrentCompany;
    }

    @Override
    public String toString() {
        return "Employment [designation=" + designation + ", end=" + end + ", isCurrentCompany=" + isCurrentCompany
                + ", organization=" + organization + ", start=" + start + "]";
    }

    
    
}

