package com.example.bankcards.service.cardService;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.card.TransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface CardService {

    /** USER functions **/

    CardDto getCard(UUID cardId);

    List<CardDto> getCards(UUID userId);

    TransactionResponse transactionByCardNumber(UUID userId, TransactionCardForm numberForm);

    TransactionResponse transactionByPhoneNumber(UUID userId, TransactionCardForm phoneForm);

    TransactionResponse transactionBetweenUserCards(UUID userId, TransactionCardForm numberForm);

    TransactionResponse requestCreateCard(UUID userId, CreateProfileFormDto createProfileFormDto);

    TransactionResponse requestBlockCard(UUID userId, CardDto cardDto);

    TransactionResponse showBalance(UUID userId, CardDto cardDto);

    /** ADMIN functions **/

    TransactionResponse addCard(PassportData cardFormDto);

    TransactionResponse activateCard(CardDto cardDto);

    TransactionResponse blockCard(CardDto cardDto);

    TransactionResponse unblockCard(CardDto cardDto);

    TransactionResponse deleteCard(CardDto cardDto);

    TransactionResponse softDeleteCard(CardDto cardDto);
}
