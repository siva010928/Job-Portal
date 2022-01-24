package com.jobportal.application.models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jobportal.application.App;

public class Pay {
    private Integer id;
    private BigDecimal from,to;
    private String payType;


    public Pay(Integer pay_id,BigDecimal from, BigDecimal to, String payType) {
        this.id=pay_id;
        this.from = from;
        this.to = to;
        this.payType = payType;
    }

    public Integer addPayToDb() throws SQLException{
        PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO PAYS VALUES(?,?,?)");
        stmt.setBigDecimal(1, this.from);
        stmt.setBigDecimal(2, this.to);
        stmt.setString(3, this.payType);
        int rowsAffected=stmt.executeUpdate();
        int last_pay_id=App.getLastInsertId();
        return last_pay_id;
    }
    

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getFrom() {
        return this.from;
    }

    public void setFrom(BigDecimal from) {
        this.from = from;
    }

    public BigDecimal getTo() {
        return this.to;
    }

    public void setTo(BigDecimal to) {
        this.to = to;
    }

    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
    
    
}

