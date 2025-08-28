package com.example.bankcards.model.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberTransactionCardForm {

    private UUID senderCardId;

    private String recipientCardNumber;

    private BigDecimal amount;
}
