package com.jobportal.application.models;

import java.time.LocalDateTime;
import java.util.Date;

public class Project {
    private String title,client,status,link,detail;
    private Date start,end;

    public Project(String title, String client, String status, String link, String detail, Date start, Date end) {
        this.title = title;
        this.client = client;
        this.status = status;
        this.link = link;
        this.detail = detail;
        this.start = start;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
}

