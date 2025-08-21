package com.example.bankcards.service.cardService;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;

import java.util.UUID;

public interface CardService {

    /** USER functions **/

    void getCard(String tokenHeader, CardDto cardDto);

    void getCards(String tokenHeader);

    void transaction(String tokenHeader, CardDto cardDto);

    void requestCreateCard(String tokenHeader, CreateCardFormDto createCardFormDto);

    void requestBlockCard(String tokenHeader, CardDto cardDto);

    void showBalance(String tokenHeader, CardDto cardDto);

    /** ADMIN functions **/

    void addCard(String userToken, CreateCardFormDto cardFormDto);

    void activateCard(String tokenHeader, CardDto cardDto);

    void updateCard(String tokenHeader, CardDto cardDto);

    void removeCard(String tokenHeader, UUID idd);

    void blockCard(String tokenHeader, UUID id);

    void unblockCard(String tokenHeader, UUID id);
}
