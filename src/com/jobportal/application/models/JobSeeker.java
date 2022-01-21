package com.jobportal.application.models;

import java.util.ArrayList;

public class JobSeeker extends User{
    private ArrayList<String> keySkills,languages;
    private ArrayList<Employment> employments;
    private ArrayList<Education> educations;
    private ArrayList<Project> projects;
    private String accompolishments;

    public JobSeeker(String firstName, String lastName, String gender, String email, String password,String location,UserType userType, ArrayList<String> keySkills, ArrayList<String> languages, ArrayList<Employment> employments, ArrayList<Education> educations, ArrayList<Project> projects, String accompolishments) {
        super(firstName, lastName, gender, email, password, location,userType);
        this.keySkills = keySkills;
        this.languages = languages;
        this.employments = employments;
        this.educations = educations;
        this.projects = projects;
        this.accompolishments = accompolishments;
    }

    public ArrayList<String> getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(ArrayList<String> keySkills) {
        this.keySkills = keySkills;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public ArrayList<Employment> getEmployments() {
        return employments;
    }

    public void setEmployments(ArrayList<Employment> employments) {
        this.employments = employments;
    }

    public ArrayList<Education> getEducations() {
        return educations;
    }

    public void setEducations(ArrayList<Education> educations) {
        this.educations = educations;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    public String getAccompolishments() {
        return accompolishments;
    }

    public void setAccompolishments(String accompolishments) {
        this.accompolishments = accompolishments;
    }
}
