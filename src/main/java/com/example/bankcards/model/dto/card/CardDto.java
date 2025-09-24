package com.example.bankcards.model.dto.card;

import com.example.bankcards.util.CardStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private UUID id;

    private String cardNumber;

    private BigDecimal balance;

    private CardStatus status;
}
