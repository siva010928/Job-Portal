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
    public Pay(){
        this.from=new BigDecimal(0);
        this.to=new BigDecimal(0);
    }

    public Integer addPayToDb() throws SQLException {
        try{
            PreparedStatement stmt=App.conn.prepareStatement("INSERT INTO pays VALUES(DEFAULT,?,?,?)");
            stmt.setBigDecimal(1, this.from);
            stmt.setBigDecimal(2, this.to);
            stmt.setString(3, this.payType);
            int rowsAffected=stmt.executeUpdate();
            int last_pay_id=App.getLastInsertId();
            return last_pay_id;
        }catch(SQLException e){
            throw e;
        }
        
    }

    public void updatePay() throws SQLException{
        System.out.println("Pay.updatePay()");
        PreparedStatement stmt=App.conn.prepareStatement("UPDATE "+DB_VARIABLES.DB+".`pays` SET `from` = ?, `to` =?, `pay_type` = ? WHERE (`pay_id` = ?)");
        stmt.setBigDecimal(1, this.getFrom());
        stmt.setBigDecimal(2, this.getTo());
        stmt.setString(3, this.getPayType());
        stmt.setInt(4, this.getId());
        int rowsAffected=stmt.executeUpdate();
    }


    @Override
    public String toString() {
        return "Salary  ₹"+this.getFrom()+" - "+"₹"+this.getTo()+" per "+(this.getPayType().equals("MONTHLY")?"Month":"Annum");
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

