package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jobportal.application.App;

public class Education {
    private Integer id;
    private String education,course,institute,courseType;
    private Integer passout;
    private Float grade;

    public Education(Integer education_id,String education, String course, String institute, String courseType, Integer passout,Float grade) {
        this.id=education_id;
        this.education = education;
        this.course = course;
        this.institute = institute;
        this.courseType = courseType;
        this.passout = passout;
        this.grade=grade;
    }

    public Education() {
    }

    //when job provider edits his educations list details
    public void updateEducation() throws SQLException{
        

        //then update this to  db
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE educations SET course=?,course_type=?,education_level=?,institution=?,passout=?,grade=? WHERE education_id=?");
        stmt.setString(1, this.getCourse());
        stmt.setString(2, this.getCourseType());
        stmt.setString(3, this.getEducation());
        stmt.setString(4, this.getInstitute());
        stmt.setInt(5, this.getPassout());
        stmt.setFloat(6, this.getGrade());
        stmt.setInt(7, this.getId());

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

    @Override
    public String toString() {
        return "Education [course=" + course + ", courseType=" + courseType + ", education=" + education + ", grade="
                + grade + ", institute=" + institute + ", passout=" + passout + "]";
    }

   
}
