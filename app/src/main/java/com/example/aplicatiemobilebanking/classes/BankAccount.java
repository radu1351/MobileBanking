package com.example.aplicatiemobilebanking.classes;

import java.util.List;

public class BankAccount {
    private String iban;
    private String swift;
    private String balance;
    private String currency;
    private List<CreditCard> creditCards;

    public BankAccount() {
    }

    public BankAccount(String iban, String swift, String balance, String currency, List<CreditCard> creditCards) {
        this.iban = iban;
        this.swift = swift;
        this.balance = balance;
        this.currency = currency;
        this.creditCards = creditCards;
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "iban='" + iban + '\'' +
                ", swift='" + swift + '\'' +
                ", balance='" + balance + '\'' +
                ", currency='" + currency + '\'' +
                ", creditCards=" + creditCards +
                '}';
    }
}
