package com.example.bankcards.controller.cardController;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface CardController {

    /** USER endpoints **/

    CardDto getCard(String tokenHeader, UUID id);

    List<CardDto> getUserCards(String tokenHeader);

    void requestCreateCard(String tokenHeader, CreateCardFormDto cardFormDto);

    void requestBlockCard(String tokenHeader, UUID id);

    String showBalance(String tokenHeader, UUID id);

    String transaction(String tokenHeader, UUID senderId, UUID recipientId);

    /** ADMIN endpoints **/

    List<CardDto> getAllCards(String tokenHeader);

    void addCard(String userToken, CreateCardFormDto cardFormDto);

    void activateCard(String tokenHeader, UUID id);

    void updateCard(String tokenHeader, UUID id, CardDto card);

    void removeCard(String tokenHeader, UUID id);

    void blockCard(String tokenHeader, UUID id);

    void unblockCard(String tokenHeader, UUID id);
}
