package com.example.aplicatiemobilebanking.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Generator {

    public static String generateIban() {
        String countryCode = "RO";
        String bankCode = "BTRL";
        String currencyCode = "RON";
        String accountNumber = new Random().ints(0,10)
                .limit(13)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());

        String iban = countryCode + "00" + bankCode + currencyCode + accountNumber;

        int remainder = iban.chars().reduce(0,
                (r, c) -> (r * 10 + Character.getNumericValue(c)) % 97);
        String checkDigitsStr = String.format("%02d", 98 - remainder);

        return countryCode + checkDigitsStr + bankCode + currencyCode + accountNumber;
    }


    public static String generateCreditCardNumber() {
        Random random = new Random();
        String ccNumber = random.ints(11, 0, 10)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
        ccNumber = "4140" + ccNumber + getCheckDigit("4140" + ccNumber);
        return ccNumber;
    }

    private static int getCheckDigit(String ccNumber) {
        int sum = IntStream.range(0, ccNumber.length())
                .map(i -> ccNumber.charAt(i) - '0')
                .map(i -> ccNumber.length() % 2 == i % 2 ? (2 * i) % 9 : i)
                .sum();
        return (10 - sum % 10) % 10;
    }

    public static int CVVGenerator() {
        Random rand = new Random();
        int firstDigit = 3;
        int secondDigit = rand.nextInt(5) + 5; // Generate a random number between 5 and 9
        int thirdDigit = rand.nextInt(10); // Generate a random number between 0 and 9
        return firstDigit * 100 + secondDigit * 10 + thirdDigit;
    }

    public static Date expirationDateGenerator() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 3);
        Date date = calendar.getTime();
        return date;
    }
}
