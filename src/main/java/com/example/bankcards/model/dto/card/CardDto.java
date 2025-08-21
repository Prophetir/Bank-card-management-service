package com.example.bankcards.model.dto.card;

import com.example.bankcards.util.CardStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class CardDto {

    private String cardNumber;

    private BigDecimal balance;

    private UUID ownerId;

    private CardStatus status;
}
