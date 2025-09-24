package com.example.bankcards.controller.cardController;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.card.TransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;

import java.util.List;

public interface CardController {

    /** USER endpoints **/

    CardDto getCard(CardDto cardDto);

    List<CardDto> getUserCards(String tokenHeader);

    TransactionResponse requestCreateCard(String tokenHeader, CreateProfileFormDto cardFormDto);

    TransactionResponse requestBlockCard(String tokenHeader, CardDto cardDto);

    TransactionResponse showBalance(String tokenHeader, CardDto cardDto);

    TransactionResponse transactionByCardNumber(String tokenHeader, TransactionCardForm numberForm);

    TransactionResponse transactionByPhoneNumber(String tokenHeader, TransactionCardForm phoneForm);

    TransactionResponse transactionBetweenUserCards(String tokenHeader, TransactionCardForm numberForm);

    /** ADMIN endpoints **/

    List<CardDto> getAllCards(String tokenHeader);

    TransactionResponse addCard(PassportData cardFormDto);

    TransactionResponse activateCard(CardDto cardDto);

    TransactionResponse blockCard(CardDto cardDto);

    TransactionResponse unblockCard(CardDto cardDto);

    TransactionResponse deleteCard(CardDto cardDto);

    TransactionResponse softDeleteCard(CardDto cardDto);
}
