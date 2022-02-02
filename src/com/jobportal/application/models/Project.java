package com.jobportal.application.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Date;

import com.jobportal.application.App;

public class Project {
    private Integer id;
    private String title,client,status,link,detail;
    private boolean isCurrentProject=false;
    private Date start,end;

    public Project(Integer project_id,String title, String client, String status, String link, String detail, Date start, Date end) {
        this.id=project_id;
        this.title = title;
        this.client = client;
        this.status = status;
        this.link = link;
        this.detail = detail;
        this.start = start;
        //if job seeker works in current project then he will not give end input and db will store it as null
        if(end.equals(App.nullDate)){
            this.isCurrentProject=true;
            this.end=App.nullDate;
        }else this.end=end;
    }

    public Project() {
    }

    public void updateProject() throws SQLException{

        
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE projects SET start_date=?,end_date=?,title=?,client=?,status=?,link=?,details=? WHERE project_id=?");
        stmt.setDate(1, this.getStart());
        //if job seeker does not fill end date in this project in edit education then set null in databse
        if(this.getEnd().equals(App.nullDate)){
            stmt.setNull(2, java.sql.Types.DATE);//updating null to education db
            this.isCurrentProject=true;
        }else{
            stmt.setDate(2, this.getEnd());
            this.isCurrentProject=false;
        }
        stmt.setString(3, this.getTitle());
        stmt.setString(4, this.getClient());
        stmt.setString(5, this.getStatus());
        stmt.setString(6, this.getLink());
        stmt.setString(7, this.getDetail());
        stmt.setInt(8, this.getId());

        System.err.println(stmt.toString());
        int updatedResults=stmt.executeUpdate();
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

    public Integer getId(){
        return this.id;
    }

    public void setId(int id) {
        this.id=id;
    }

    @Override
    public String toString() {
        return "Project [client=" + client + ", detail=" + detail + ", end=" + end + ", isCurrentProject="
                + isCurrentProject + ", link=" + link + ", start=" + start + ", status=" + status + ", title=" + title
                + "]";
    }
    
}

