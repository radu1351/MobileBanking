package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transfer implements Serializable {
    private String senderIban;
    private String recipientIban;
    private float amount;  // fara comision (to be transfered)
    private float commission;  // 2.5f sau 5.0f
    private String description;
    private Date date;

    public Transfer(String senderIban, String recipientIban, float amount, float commission, String description, Date date) {
        this.senderIban = senderIban;
        this.recipientIban = recipientIban;
        this.amount = amount;
        this.commission = commission;
        this.description = description;
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
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
                ", ammount=" + amount +
                ", commision=" + commission +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
