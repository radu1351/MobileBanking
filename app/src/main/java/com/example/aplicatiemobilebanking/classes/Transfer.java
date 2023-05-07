package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transfer implements Serializable {
    private String senderIban;
    private String recipientIban;
    private float ammount;  // fara comision (to be transfered)
    private float commision;  // 2.5f sau 5.0f
    private String description;
    private Date date;

    public Transfer(String senderIban, String recipientIban, float ammount, float commision, String description, Date date) {
        this.senderIban = senderIban;
        this.recipientIban = recipientIban;
        this.ammount = ammount;
        this.commision = commision;
        this.description = description;
        this.date = date;
    }

    public float getAmmount() {
        return ammount;
    }

    public void setAmmount(float ammount) {
        this.ammount = ammount;
    }

    public String getSenderIban() {
        return senderIban;
    }

    public void setSenderIban(String senderIban) {
        this.senderIban = senderIban;
    }

    public String getRecipientIban() {
        return recipientIban;
    }

    public void setRecipientIban(String recipientIban) {
        this.recipientIban = recipientIban;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "senderIban='" + senderIban + '\'' +
                ", recipientIban='" + recipientIban + '\'' +
                ", ammount=" + ammount +
                ", commision=" + commision +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
