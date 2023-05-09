package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transfer implements Serializable {
    private String id; // PK
    private String recipientIban;
    private float amount;  // fara comision (to be transfered)
    private float commission;  // 2.5f sau 5.0f
    private String description;
    private Date date;
    private String bankAccountIban;  //Foreign key for BankAccount

    public Transfer(){

    }

    public Transfer(String id, String recipientIban, float amount, float commission, String description, Date date, String bankAccountIban) {
        this.id = id;
        this.recipientIban = recipientIban;
        this.amount = amount;
        this.commission = commission;
        this.description = description;
        this.date = date;
        this.bankAccountIban = bankAccountIban;
    }

    public Transfer(String recipientIban, float amount, float commission, String description, Date date) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccountIban() {
        return bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                ", recipientIban='" + recipientIban + '\'' +
                ", ammount=" + amount +
                ", commision=" + commission +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
