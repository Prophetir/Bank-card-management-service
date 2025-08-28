package com.example.bankcards.repository;

import com.example.bankcards.model.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, UUID> {

    @Query(value = "select c from CardEntity c join c.owner u " +
            "where u.id = :userId and c.id = :cardId", nativeQuery = false)
    Optional<CardEntity> findUserCardById(@Param("cardId") UUID cardId, @Param("userId") UUID userId);

    @Query(value = "select c.* from cards c join users u on c.owner_id = :userId", nativeQuery = true)
    List<CardEntity> findAllUserCartByUserId(@Param("userId") UUID userId);

    @Query(value = "select c.* from cards c where c.card_number = :cardNumber", nativeQuery = true)
    Optional<CardEntity> findCardByCardNumber(@Param("cardNumber") String cardNumber);
}
