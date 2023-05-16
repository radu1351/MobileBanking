package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String id; // Pk
    private String merchant;
    private String category;
    private float amount;
    private Date date;
    private String creditCardNumber;  //Foreign key for CreditCard
    private String bankAccountIban;  //Foreign key for BankAccount


    public Transaction(){

    }

    public Transaction(String id, String merchant, String category, float amount, Date date, String creditCardNumber, String bankAccountIban) {
        this.id = id;
        this.merchant = merchant;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.creditCardNumber = creditCardNumber;
        this.bankAccountIban = bankAccountIban;
    }

    public Transaction(String merchant, String category, float amount, Date date, String creditCardNumber) {
        this.merchant = merchant;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.creditCardNumber = creditCardNumber;

    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCard(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
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
        return "Transaction{" +
                "merchant='" + merchant + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", creditCardNumber=" + creditCardNumber +
                '}';
    }
}
