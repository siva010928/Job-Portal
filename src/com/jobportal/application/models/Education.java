package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jobportal.application.App;

public class Education {
    private Integer id;
    private String education,course,institute,courseType;
    private Integer passout;
    private Float grade;

    public Education(Integer education_id,String education, String course, String institute, String courseType, Integer passout) {
        this.id=education_id;
        this.education = education;
        this.course = course;
        this.institute = institute;
        this.courseType = courseType;
        this.passout = passout;
    }

    //when job provider edits his educations list details
    public void updateEducation(Education education) throws SQLException{
        this.setCourse(education.getCourse());
        this.setCourseType(education.getCourseType());
        this.setEducation(education.getEducation());
        this.setInstitute(education.getInstitute());
        this.setPassout(education.getPassout());
        this.setGrade(education.getGrade());

        //then update this to  db
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE educations SET course=?,course_type=?,education=?,institute=?,passout=?,grade=? WHERE education_id=?");
        stmt.setString(1, this.getCourse());
        stmt.setString(2, this.getCourseType());
        stmt.setString(3, this.getEducation());
        stmt.setString(4, this.getInstitute());
        stmt.setInt(5, this.getPassout());
        stmt.setInt(6, this.getId());

        int updatedResults=stmt.executeUpdate();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEducation() {
        return this.education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCourse() {
        return this.course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getInstitute() {
        return this.institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCourseType() {
        return this.courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public Integer getPassout() {
        return this.passout;
    }

    public void setPassout(Integer passout) {
        this.passout = passout;
    }

    public Float getGrade() {
        return this.grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }

   
}
