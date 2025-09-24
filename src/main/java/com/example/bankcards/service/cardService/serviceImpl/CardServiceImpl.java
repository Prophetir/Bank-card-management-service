package com.example.bankcards.service.cardService.serviceImpl;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.exception.exceptions.TransactionException;
import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.card.TransactionCardForm;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.entity.CardEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.service.profileService.ProfileDomainService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.GenerateCardNumber;
import com.example.bankcards.util.MaskPhoneAndCardNumber;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
                .orElseThrow(() -> new NotFoundException("Card not found"));

        cardEntity.setCardNumber(MaskPhoneAndCardNumber.maskCardNumber(cardEntity.getCardNumber()));

        return modelMapper.map(cardEntity, CardDto.class);
    }

    @Override
    public List<CardDto> getCards(UUID userId) {

        List<CardDto> cardDtos = cardRepository.findAllUserCartByUserId(userId)
                .stream()
                .map(cardEntity -> modelMapper.map(cardEntity, CardDto.class))
                .toList();
        cardDtos.forEach(cardDto -> cardDto.setCardNumber(MaskPhoneAndCardNumber.maskCardNumber(cardDto.getCardNumber())));

        return cardDtos;
    }

    @Transactional
    @Override
    public TransactionResponse transactionByCardNumber(UUID userId, TransactionCardForm numberForm) {

        UUID profileId = profileService.getProfileByUserId(userId).getId();

        CardEntity senderCard = cardRepository.findUserCardById(numberForm.getSenderCardId(), profileId)
                .orElseThrow(() -> new NotFoundException("Sender card not found by id"));

        CardEntity recipientCard = cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(profileId, numberForm.getRecipientCardOrPhoneNumber())
                .orElseThrow(() -> new NotFoundException("Recipient card not found by cardNumber"));

        checkBalance(senderCard.getBalance(), numberForm.getAmount());

        setCardsBalance(senderCard, recipientCard, numberForm.getAmount());

        return new TransactionResponse("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    @Transactional
    @Override
    public TransactionResponse transactionByPhoneNumber(UUID userId, TransactionCardForm phoneForm) {

        UUID profileId = profileService.getProfileByUserId(userId).getId();

        CardEntity senderCard = cardRepository.findUserCardById(phoneForm.getSenderCardId(), profileId)
                .orElseThrow(() -> new NotFoundException("Not found sender card by id"));

        CardEntity recipientCard = cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(profileId, phoneForm.getRecipientCardOrPhoneNumber())
                .orElseThrow(() -> new NotFoundException("Not found recipient card by phone number"));

        checkBalance(senderCard.getBalance(), phoneForm.getAmount());

        setCardsBalance(senderCard, recipientCard, phoneForm.getAmount());

        return new TransactionResponse("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    /** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *                  ДОАБОТАТЬ МЕТОД
     *  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!**/
    @Transactional
    @Override
    public TransactionResponse transactionBetweenUserCards(UUID userId, TransactionCardForm numberForm) {

        CardEntity senderCard = cardRepository.findUserCardById(
                profileService.getProfileByUserId(userId).getId(), numberForm.getSenderCardId())
                .orElseThrow(() -> new NotFoundException("Sender card not found by id"));

        CardEntity recipientCard = cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(null, numberForm.getRecipientCardOrPhoneNumber())
                .orElseThrow(() -> new NotFoundException("Recipient card not found by cardNumber"));

        checkBalance(senderCard.getBalance(), recipientCard.getBalance());

        setCardsBalance(senderCard, recipientCard, numberForm.getAmount());

        return new TransactionResponse("------| Transaction from card by number: %s by card by card number: %s successfully |-------\n"
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

        CardEntity cardEntity = cardRepository.findUserCardById(cardDto.getId(), profileService.getProfileByUserId(userId).getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        return new TransactionResponse("-----| Balance card by number %s user %s: %s |-----"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName(), cardEntity.getBalance()));
    }

    /** ADMIN business logic **/

    @Transactional
    @Override
    public TransactionResponse addCard(PassportData cardFormDto) {

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

        cardRepository.save(cardEntity);

        return new TransactionResponse("------| Card by number %s user %s was activated |------"
                .formatted(cardDto.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Transactional
    @Override
    public TransactionResponse blockCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setStatus(CardStatus.BLOCKED);

        cardRepository.save(cardEntity);

        return new TransactionResponse("Card by number %s user %s was blocked |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Transactional
    @Override
    public TransactionResponse unblockCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setStatus(CardStatus.UNBLOCKED);

        cardRepository.save(cardEntity);

        return new TransactionResponse("Card by number %s user %s was unblocked |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    /**
     * Добавить проверку на флаг и отправлять сообщение, если на момент soft-delete флаг уже стоит true.
     * ! Также необходимо добавить метод, снимающий этот флаг !
     * **/
    @Transactional
    @Override
    public TransactionResponse softDeleteCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow(() -> new NotFoundException("Not found card by id"));

        cardEntity.setDeleteFlag(true);
        cardEntity.setStatus(CardStatus.DELETED);

        cardRepository.save(cardEntity);

        return new TransactionResponse("-----| Card by number %s user %s was soft-delete |------"
                .formatted(cardDto.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    /**
     * Стоит добавить исключение, если карта уже была ранее удалена у пользователя.
     * Также стоит выбрасывать предупреждение перед глубоким удаление карты.
     * **/
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

    /** Other method **/

    private void checkBalance(BigDecimal senderBalance, BigDecimal recipientBalance) {
        if (senderBalance.compareTo(recipientBalance) < 0)
            throw new TransactionException("Not enough funds on the sender's card");
    }

    private void setCardsBalance(CardEntity senderCard, CardEntity recipientCard, BigDecimal amount) {
        senderCard.setBalance(senderCard.getBalance().subtract(amount));
        recipientCard.setBalance(recipientCard.getBalance().add(amount));

        cardRepository.save(senderCard);
        cardRepository.save(recipientCard);
    }
}
