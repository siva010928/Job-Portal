package com.jobportal.application.models;


public class Education {
    private String education,course,institute,courseType;
    private Integer passout;
    private Float grade;

    public Education(String education, String course, String institute, String courseType, Integer passout) {
        this.education = education;
        this.course = course;
        this.institute = institute;
        this.courseType = courseType;
        this.passout = passout;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public Integer getPassout() {
        return passout;
    }

    public void setPassout(Integer passout) {
        this.passout = passout;
    }

    public Float getGrade() {
        return grade;
    }

    public void setGrade(Float grade) {
        this.grade = grade;
    }
}
