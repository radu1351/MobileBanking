package com.example.aplicatiemobilebanking.classes;

import com.vinaygaba.creditcardview.CardType;

import java.io.Serializable;
import java.util.Date;

public class CreditCard implements Serializable {
    private String cardNumber;
    private String cardholderName;
    private Date expirationDate;
    private int cvv;
    private int cardType; // 0 - Visa ,  1 - Mastercard

    public CreditCard() {
    }

    public CreditCard(String cardNumber, String cardholderName, Date expirationDate, int cvv, int cardType) {
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.cardType = cardType;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(short cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardholderName='" + cardholderName + '\'' +
                ", expirationDate=" + expirationDate +
                ", cvv=" + cvv +
                ", cardType=" + cardType +
                '}';
    }
}
