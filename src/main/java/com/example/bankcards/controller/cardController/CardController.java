package com.example.bankcards.controller.cardController;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.card.NumberTransactionCardForm;
import com.example.bankcards.model.dto.card.PhoneTransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;

import java.util.List;

public interface CardController {

    /** USER endpoints **/

    CardDto getCard(CardDto cardDto);

    List<CardDto> getUserCards(String tokenHeader);

    TransactionResponse requestCreateCard(String tokenHeader, CreateProfileFormDto cardFormDto);

    TransactionResponse requestBlockCard(String tokenHeader, CardDto cardDto);

    TransactionResponse showBalance(String tokenHeader, CardDto cardDto);

    TransactionResponse transactionByCardNumber(String tokenHeader, NumberTransactionCardForm numberForm);

    TransactionResponse transactionByPhoneNumber(String tokenHeader, PhoneTransactionCardForm phoneForm);

    /** ADMIN endpoints **/

    List<CardDto> getAllCards(String tokenHeader);

    TransactionResponse addCard(CreateCardFormDto cardFormDto);

    TransactionResponse activateCard(CardDto cardDto);

    TransactionResponse blockCard(CardDto cardDto);

    TransactionResponse unblockCard(CardDto cardDto);

    TransactionResponse deleteCard(CardDto cardDto);

    TransactionResponse softDeleteCard(CardDto cardDto);
}
