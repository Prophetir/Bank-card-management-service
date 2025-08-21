package com.example.bankcards.util;

public class GenerateCardNumber {

    public static String generateCardNumber(String tokenId, String userId) {

        StringBuilder cardNumber = new StringBuilder().append("2");

        char[] charactersUserId = userId.toCharArray();

        for (int i = 0; i < 14; i++) {
            if (Character.isDigit(charactersUserId[i])) {
                if (charactersUserId.length - i % 4 == 0)
                    cardNumber.append(" ");

                cardNumber.append(userId.toCharArray()[i]);
            }
        }

        cardNumber.append(tokenId.toCharArray()[0]);

        return cardNumber.toString();
    }
}
