package com.example.bankcards.model.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class TransactionCardForm {

    private UUID senderCardId;

    private String recipientCardOrPhoneNumber;

    private BigDecimal amount;
}
