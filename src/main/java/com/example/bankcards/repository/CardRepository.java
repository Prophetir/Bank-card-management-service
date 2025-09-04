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

    @Query("select c from CardEntity c join ProfileEntity p on c.owner = p.id " +
            "where p.id = :profileId and c.id = :cardId")
    Optional<CardEntity> findUserCardById(@Param("cardId") UUID cardId, @Param("profileId") UUID userId);

    @Query(value = "select c.* from cards c where c.owner_id = :profileId", nativeQuery = true)
    List<CardEntity> findAllUserCartByUserId(@Param("profileId") UUID profileId);

    @Query(value = "select c.* from cards c where c.card_number = :cardNumber", nativeQuery = true)
    Optional<CardEntity> findCardByCardNumber(@Param("cardNumber") String cardNumber);

    @Query("select c from CardEntity c where c.owner.phoneNumber = :phoneNumber")
    Optional<CardEntity> findCardByPhone(@Param("phoneNumber") String phoneNumber);
}
