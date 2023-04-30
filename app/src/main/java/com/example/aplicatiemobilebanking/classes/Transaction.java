package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private int id;
    private String merchant;
    private String category;
    private float ammount;
    private Date date;


    public Transaction(int id, String merchant, String category, float ammount, Date date) {
        this.id = id;
        this.merchant = merchant;
        this.category=category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", merchant='" + merchant + '\'' +
                ", category='" + category + '\'' +
                ", ammount=" + ammount +
                ", date=" + date +
                '}';
    }
}
