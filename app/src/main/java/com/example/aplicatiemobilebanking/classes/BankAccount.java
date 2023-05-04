package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.List;

public class BankAccount implements Serializable {
    private String iban;
    private String swift;
    private float balance;
    private String currency;

    public BankAccount() {
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

    @Override
    public String toString() {
        return "BankAccount{" +
                "iban='" + iban + '\'' +
                ", swift='" + swift + '\'' +
                ", balance='" + balance + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
