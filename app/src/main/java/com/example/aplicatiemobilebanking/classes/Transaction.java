package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String merchant;
    private String category;
    private float ammount;
    private Date date;
    private CreditCard creditCard;


    public Transaction(String merchant, String category, float ammount, Date date, CreditCard creditCard) {
        this.merchant = merchant;
        this.category = category;
        this.ammount = ammount;
        this.date = date;
        this.creditCard = creditCard;

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

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "merchant='" + merchant + '\'' +
                ", category='" + category + '\'' +
                ", ammount=" + ammount +
                ", date=" + date +
                ", creditCard=" + creditCard +
                '}';
    }
}
