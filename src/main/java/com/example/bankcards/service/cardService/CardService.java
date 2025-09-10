package com.example.bankcards.service.cardService;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.card.NumberTransactionCardForm;
import com.example.bankcards.model.dto.card.PhoneTransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface CardService {

    /** USER functions **/

    CardDto getCard(UUID cardId);

    List<CardDto> getCards(UUID userId);

    TransactionResponse transactionByCardNumber(UUID userId, NumberTransactionCardForm numberForm);

    TransactionResponse transactionByPhoneNumber(UUID userId, PhoneTransactionCardForm phoneForm);

    TransactionResponse transactionBetweenUserCards(UUID userId, NumberTransactionCardForm numberForm);

    TransactionResponse requestCreateCard(UUID userId, CreateProfileFormDto createProfileFormDto);

    TransactionResponse requestBlockCard(UUID userId, CardDto cardDto);

    TransactionResponse showBalance(UUID userId, CardDto cardDto);

    /** ADMIN functions **/

    TransactionResponse addCard(CreateCardFormDto cardFormDto);

    TransactionResponse activateCard(CardDto cardDto);

    TransactionResponse blockCard(CardDto cardDto);

    TransactionResponse unblockCard(CardDto cardDto);

    TransactionResponse deleteCard(CardDto cardDto);

    TransactionResponse softDeleteCard(CardDto cardDto);
}
