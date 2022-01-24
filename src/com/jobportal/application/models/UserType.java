package com.jobportal.application.models;

public enum UserType{
    JOB_SEEKER(1), JOB_PROVIDER(2);

    private int user_type_id;

    UserType(int user_type_id) {
        this.user_type_id = user_type_id;
    }

    int getUserTypeId(){
        return this.user_type_id;
    }
}