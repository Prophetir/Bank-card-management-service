package com.example.bankcards.service.cardService.serviceImpl;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.exception.exceptions.TransactionException;
import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.card.NumberTransactionCardForm;
import com.example.bankcards.model.dto.card.PhoneTransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.entity.CardEntity;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.service.profileService.ProfileDomainService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.GenerateCardNumber;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ProfileDomainService profileService;

    private final CardRepository cardRepository;

    private final ModelMapper modelMapper;

    /** USER business logic **/

    @Override
    public CardDto getCard(UUID cardId) {
        CardEntity cardEntity = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found by id"));

        return modelMapper.map(cardEntity, CardDto.class);
    }

    @Override
    public List<CardDto> getCards(UUID userId) {

        return cardRepository.findAllUserCartByUserId(userId)
                .stream()
                .map(cardEntity -> modelMapper.map(cardEntity, CardDto.class))
                .toList();
    }

    @Transactional
    @Override
    public TransactionResponse transactionByCardNumber(UUID userId, NumberTransactionCardForm numberForm) {

        CardEntity senderCard = cardRepository.findUserCardById(
                numberForm.getSenderCardId(), profileService.getProfileEntity(userId).getId())
                .orElseThrow(() -> new NotFoundException("Sender card not found by id"));

        CardEntity recipientCard = cardRepository.findCardByCardNumber(numberForm.getRecipientCardNumber())
                .orElseThrow(() -> new NotFoundException("Recipient card not found by cardNumber"));

        checkBalance(senderCard.getBalance(), numberForm.getAmount());

        setCardsBalance(senderCard, recipientCard, numberForm.getAmount());

        return new TransactionResponse("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    @Transactional
    @Override
    public TransactionResponse transactionByPhoneNumber(UUID userId, PhoneTransactionCardForm phoneForm) {

        CardEntity senderCard = cardRepository.findUserCardById(
                phoneForm.getSenderCardId(), profileService.getProfileEntity(userId).getId())
                .orElseThrow(() -> new NotFoundException("Not found sender card by id"));

        CardEntity recipientCard = cardRepository.findCardByPhone(phoneForm.getRecipientPhoneNumber())
                .orElseThrow(() -> new NotFoundException("Not found recipient card by phone number"));

        checkBalance(senderCard.getBalance(), phoneForm.getAmount());

        setCardsBalance(senderCard, recipientCard, phoneForm.getAmount());

        return new TransactionResponse("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    /** Методы по запросам создания, блокировки карты и т.д. должны использовать отдельный сервис.
     * Т.е. они должны посылать запросы на другие сервисы,
     * где и происходит процедура создания, блокировки и т.д., карты.
     * Потому оставлю эти методы на будущее.
     * **/
    @Override
    public TransactionResponse requestCreateCard(UUID userId, CreateProfileFormDto createProfileFormDto) {
        return new TransactionResponse("");
    }

    @Override
    public TransactionResponse requestBlockCard(UUID userId, CardDto cardDto) {
        return new TransactionResponse("");
    }

    @Override
    public TransactionResponse showBalance(UUID userId, CardDto cardDto) {

        CardEntity cardEntity = cardRepository.findUserCardById(cardDto.getId(), userId)
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        return new TransactionResponse("-----| Balance card by number %s user %s |----- \n ------| Balance: %s |----- \n"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName(), cardEntity.getBalance()));
    }

    /** ADMIN business logic **/

    @Transactional
    @Override
    public TransactionResponse addCard(CreateCardFormDto cardFormDto) {

        CardEntity cardEntity = CardEntity.builder()
                .owner(profileService.findProfileByPassportData(cardFormDto))
                .cardNumber(GenerateCardNumber.generateCardNumber())
                .status(CardStatus.ISSUED)
                .balance(new BigDecimal(0))
                .build();

        cardRepository.save(cardEntity);

        return new TransactionResponse("------| Card by number %s user %s was created and translated in status ISSUED |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Transactional
    @Override
    public TransactionResponse activateCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setStatus(CardStatus.ACTIVE);

        return new TransactionResponse("------| Card by number %s user %s was activated |------"
                .formatted(cardDto.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Transactional
    @Override
    public TransactionResponse blockCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setStatus(CardStatus.BLOCKED);

        return new TransactionResponse("Card by number %s user %s was blocked |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Transactional
    @Override
    public TransactionResponse unblockCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setStatus(CardStatus.ACTIVE);

        return new TransactionResponse("Card bu number %s user %s was unblocked |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Transactional
    @Override
    public TransactionResponse deleteCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        String cardNumber = cardEntity.getCardNumber();
        String ownerCardName = cardEntity.getOwner().getFullName();

        cardRepository.delete(cardEntity);

        return new TransactionResponse("-----| Card by number %s user %s was removed |------"
                .formatted(cardNumber, ownerCardName));
    }

    @Transactional
    @Override
    public TransactionResponse softDeleteCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setDeleteFlag(true);

        cardRepository.save(cardEntity);

        return new TransactionResponse("-----| Card by number %s user %s was soft-delete |------"
                .formatted(cardDto.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    /** Other method **/

    private void checkBalance(BigDecimal senderBalance, BigDecimal receiverBalance) {
        if (senderBalance.compareTo(receiverBalance) < 0)
            throw new TransactionException("Not enough funds on the sender's card");
    }

    private void setCardsBalance(CardEntity senderCard, CardEntity recipientCard, BigDecimal amount) {
        senderCard.setBalance(senderCard.getBalance().subtract(amount));
        recipientCard.setBalance(recipientCard.getBalance().add(amount));

        cardRepository.save(senderCard);
        cardRepository.save(recipientCard);
    }
}
