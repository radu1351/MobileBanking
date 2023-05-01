package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transfer implements Serializable {
    private int id;
    private float ammount;
    private String senderIban;
    private String recipientIban;
    private String description;
    private Date date;

    public Transfer(int id, float ammount, String senderIban, String recipientIban, String description, Date date) {
        this.id = id;
        this.ammount = ammount;
        this.senderIban = senderIban;
        this.recipientIban = recipientIban;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
                "id=" + id +
                ", ammount=" + ammount +
                ", senderIban='" + senderIban + '\'' +
                ", recipientIban='" + recipientIban + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
