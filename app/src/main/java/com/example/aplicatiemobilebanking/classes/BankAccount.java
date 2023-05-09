package com.example.aplicatiemobilebanking.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class BankAccount implements Serializable {
    private String iban; // PK
    private String swift;
    private float balance;
    private String currency;
    private String userPersonalID; //Foreign key for user


    public BankAccount() {
    }

    public BankAccount(String iban, String swift, float balance, String currency, String userPersonalID) {
        this.iban = iban;
        this.swift = swift;
        this.balance = balance;
        this.currency = currency;
        this.userPersonalID = userPersonalID;
    }

    public BankAccount(String iban, String swift, float balance, String currency) {
        this.iban = iban;
        this.swift = swift;
        this.balance = balance;
        this.currency = currency;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setUserPersonalID(String userPersonalID) {
        this.userPersonalID = userPersonalID;
    }

    public String getUserPersonalID() {
        return userPersonalID;
    }

    public void reduceBalance(float ammount){
        this.balance -= ammount;
    }
    @Override
    public String toString() {
        return "BankAccount{" +
                "iban='" + iban + '\'' +
                ", swift='" + swift + '\'' +
                ", balance='" + balance + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
