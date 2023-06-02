package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;

public class Deposit implements Serializable {
    private String id; //PK
    private float baseAmount;
    private float interestRate;
    private int numberOfMonths;
    private float maturityRate;
    private String bankAccountIban;  //Foreign Key for Bank Account


    public Deposit() {
    }

    public Deposit(String id, float baseAmount, float interestRate, int numberOfMonths, float maturityRate, String bankAccountIban) {
        this.id  = id;
        this.baseAmount = baseAmount;
        this.interestRate = interestRate;
        this.numberOfMonths = numberOfMonths;
        this.maturityRate = maturityRate;
        this.bankAccountIban = bankAccountIban;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(float baseAmount) {
        this.baseAmount = baseAmount;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public int getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public String getBankAccountIban() {
        return bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
    }

    public float getMaturityRate() {
        return maturityRate;
    }

    public void setMaturityRate(float maturityRate) {
        this.maturityRate = maturityRate;
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "baseAmount=" + baseAmount +
                ", interestRate=" + interestRate +
                ", numberOfMonths=" + numberOfMonths +
                ", maturityRate=" + maturityRate +
                ", bankAccountIban='" + bankAccountIban + '\'' +
                '}';
    }
}
