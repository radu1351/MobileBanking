package com.example.aplicatiemobilebanking.classes;

import java.util.Date;

public class Transaction {
    private int id;
    private String merchant;
    private float ammount;
    private Date date;


    public Transaction(int id, String merchant, float ammount, Date date) {
        this.id = id;
        this.merchant = merchant;
        this.ammount = ammount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public float getAmmount() {
        return ammount;
    }

    public void setAmmount(float ammount) {
        this.ammount = ammount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "merchant='" + merchant + '\'' +
                ", ammount=" + ammount +
                ", date=" + date +
                '}';
    }
}
