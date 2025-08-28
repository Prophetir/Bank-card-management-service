package com.example.bankcards.model.entity;

import com.example.bankcards.util.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cards")
public class CardEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cardNumber", nullable = false, unique = true, length = 16)
    @NotBlank
    private String cardNumber;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    private UserProfileEntity owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "soft-delete", nullable = false)
    private Boolean deleteFlag = false;

    @PrePersist
    private void prePersist() {
        if (expirationDate == null)
            expirationDate = LocalDate.now().plusYears(10);
    }
}
