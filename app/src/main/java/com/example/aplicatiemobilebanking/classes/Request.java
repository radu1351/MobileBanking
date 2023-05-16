package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable {
    private String requesterFullName;  // The one who requested the money
    private String requesterIban;
    private String senderFullName;  // The one who sends the money
    private String senderIban;
    private float amount;
    private Date date; // Last state change
    private int state;  // 0 - in progress, 1 - accepted, 2 - denied

    public Request() {
    }

    public Request(String requesterFullName, String requesterIban, String senderFullName, String senderIban,
                   float amount, Date date, int state) {
        this.requesterFullName = requesterFullName;
        this.requesterIban = requesterIban;
        this.senderFullName = senderFullName;
        this.senderIban = senderIban;
        this.amount = amount;
        this.date = date;
        this.state = state;
    }

    public String getRequesterFullName() {
        return requesterFullName;
    }

    public void setRequesterFullName(String requesterFullName) {
        this.requesterFullName = requesterFullName;
    }

    public String getRequesterIban() {
        return requesterIban;
    }

    public void setRequesterIban(String requesterIban) {
        this.requesterIban = requesterIban;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    public String getSenderIban() {
        return senderIban;
    }

    public void setSenderIban(String senderIban) {
        this.senderIban = senderIban;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requesterFullName='" + requesterFullName + '\'' +
                ", requesterIban='" + requesterIban + '\'' +
                ", senderFullName='" + senderFullName + '\'' +
                ", senderIban='" + senderIban + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", state=" + state +
                '}';
    }
}
