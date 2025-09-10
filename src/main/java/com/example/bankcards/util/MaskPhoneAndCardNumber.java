package com.example.bankcards.util;

public class MaskPhoneAndCardNumber {

    public static String maskPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(?<=\\d{2})\\d(?=\\d{4})", "*");
    }

    public static String maskCardNumber(String cardNumber) {

        return cardNumber
                .substring(0, cardNumber.length() - 4)
                .replaceAll("[A-Z0-9a-z]", "*") +
                cardNumber.substring(cardNumber.length() - 4);
    }
}
