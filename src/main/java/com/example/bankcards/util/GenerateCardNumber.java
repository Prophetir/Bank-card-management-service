package com.example.bankcards.util;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Эту ерунду надо переработать, но пока пусть побудет так,
 * т.к. есть более приоритетные задачи,
 * а пока поставлю задачу на её выполнение.
 * **/
public class GenerateCardNumber {

    private static final String BIN = "3502 50";
    private static final SecureRandom random = new SecureRandom();

    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder(BIN);

        for (int i = 0; i < 9; i++) {
            cardNumber.append(random.nextInt(10));

            if (cardNumber.toString().replaceAll("\\s+", "").length() % 4 == 0)  cardNumber.append(" ");
        }

        cardNumber.append(generateLuneDigitNumber(cardNumber.toString()));

        return cardNumber.toString();
    }

    private static String generateLuneDigitNumber(String cardNumber) {
        int[] numbers = cardNumber.chars().map(ch -> ch - '0').toArray();
        int resultNumber = 0;
        boolean doubleNumber = true;

        for (int i = numbers.length - 1; i >= 0; i--) {
            if (doubleNumber) {
                int doubledNumber = numbers[i] * 2;

                if (doubledNumber > 9)
                    resultNumber += String.valueOf(doubledNumber)
                            .chars().map(Character::getNumericValue).sum();
                else resultNumber += doubledNumber;
            } else
                resultNumber += numbers[i];

            doubleNumber = !doubleNumber;
        }

        return String.valueOf((10 - (resultNumber % 10)) % 10);
    }
}
