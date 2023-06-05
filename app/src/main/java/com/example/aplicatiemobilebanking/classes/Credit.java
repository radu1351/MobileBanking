package com.example.aplicatiemobilebanking.classes;

import java.io.Serializable;
import java.util.Date;

public class Credit implements Serializable {
    private String id; // PK
    private float loanedAmount;
    private float interestRate;
    private float totalCost;
    private int numberOfMonths;
    private Date maturityDate;
    private Date lastMonthlyPayment;
    private float outstandingBalance;
    private String bankAccountIban; // Foreign Key for Bank Account

    public Credit() {
    }

    public Credit(String id, float loanedAmount, float interestRate, float totalCost, int numberOfMonths,
                  Date maturityDate, Date lastMonthlyPayment, float outstandingBalance, String bankAccountIban) {
        this.id = id;
        this.loanedAmount = loanedAmount;
        this.interestRate = interestRate;
        this.totalCost = totalCost;
        this.numberOfMonths = numberOfMonths;
        this.maturityDate = maturityDate;
        this.lastMonthlyPayment = lastMonthlyPayment;
        this.outstandingBalance = outstandingBalance;
        this.bankAccountIban = bankAccountIban;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getLoanedAmount() {
        return loanedAmount;
    }

    public void setLoanedAmount(float loanedAmount) {
        this.loanedAmount = loanedAmount;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public int getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public float getMonthlyInstalment() {
        return totalCost / numberOfMonths;
    }

    public Date getLastMonthlyPayment() {
        return lastMonthlyPayment;
    }

    public void setLastMonthlyPayment(Date lastMonthlyPayment) {
        this.lastMonthlyPayment = lastMonthlyPayment;
    }

    public float getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(float outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public String getBankAccountIban() {
        return bankAccountIban;
    }

    public void setBankAccountIban(String bankAccountIban) {
        this.bankAccountIban = bankAccountIban;
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id='" + id + '\'' +
                ", loanedAmount=" + loanedAmount +
                ", interestRate=" + interestRate +
                ", totalCost=" + totalCost +
                ", numberOfMonths=" + numberOfMonths +
                ", maturityDate=" + maturityDate +
                ", lastMonthlyPayment=" + lastMonthlyPayment +
                ", outstandingBalance=" + outstandingBalance +
                ", bankAccountIban='" + bankAccountIban + '\'' +
                '}';
    }
}
