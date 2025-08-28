package com.example.bankcards.service.cardService.serviceImpl;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.dto.card.NumberTransactionCardForm;
import com.example.bankcards.model.dto.card.PhoneTransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.model.entity.CardEntity;
import com.example.bankcards.model.entity.UserEntity;
import com.example.bankcards.model.entity.UserProfileEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.ProfileRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.GenerateCardNumber;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class CardServiceImpl implements CardService {

    @Resource
    private CardRepository cardRepository;

    @Resource
    private ProfileRepository profileRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ModelMapper modelMapper;

    /** USER business logic **/

    @Override
    public CardDto getCard(UUID cardId) {
        return modelMapper.map(cardRepository.findById(cardId), CardDto.class);
    }

    @Override
    public List<CardDto> getCards(UUID userId) {

        return cardRepository.findAllUserCartByUserId(userId)
                .stream()
                .map(cardEntity -> modelMapper.map(cardEntity, CardDto.class))
                .toList();
    }

    /** Требуется доработка этого метода в будущем. Пока оставлю на некоторое время. **/
    @Override
    public TransactionResponse transactionByCardNumber(UUID userId, NumberTransactionCardForm numberForm) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow();

        CardEntity senderCard = cardRepository.findUserCardById(numberForm.getSenderCardId(), userEntity.getUserProfile().getId())
                .orElseThrow();

        CardEntity recipientCard = cardRepository.findCardByCardNumber(numberForm.getRecipientCardNumber())
                .orElseThrow();

        senderCard.setBalance(senderCard.getBalance().subtract(numberForm.getAmount()));
        recipientCard.setBalance(recipientCard.getBalance().add(numberForm.getAmount()));

        return new TransactionResponse("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    /** Требуется доработка этого метода в будущем. Пока оставлю на некоторое время. **/
    @Override
    public TransactionResponse transactionByPhoneNumber(UUID userId, PhoneTransactionCardForm phoneForm) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow();

        CardEntity senderCard = cardRepository.findUserCardById(phoneForm.getSenderCardId(), userEntity.getUserProfile().getId())
                .orElseThrow();

        CardEntity recipientCard = cardRepository.findCardByCardNumber(phoneForm.getRecipientPhoneNumber())
                .orElseThrow();

        senderCard.setBalance(senderCard.getBalance().subtract(phoneForm.getAmount()));
        recipientCard.setBalance(recipientCard.getBalance().add(phoneForm.getAmount()));

        return new TransactionResponse("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    /** Методы по запросам создания, блокировки карты и т.д. должны использовать отдельный сервис.
     * Т.е. они должны посылать запросы на другие сервисы,
     * где и происходит процедура создания, блокировки и т.д., карты.
     * Потому оставлю эти методы на будущее.
     * **/
    @Override
    public TransactionResponse requestCreateCard(UUID userId, CreateCardFormDto createCardFormDto) {
        return new TransactionResponse("");
    }

    @Override
    public TransactionResponse requestBlockCard(UUID userId, CardDto cardDto) {
        return new TransactionResponse("");
    }

    @Override
    public TransactionResponse showBalance(UUID userId, CardDto cardDto) {

        CardEntity cardEntity = cardRepository.findUserCardById(cardDto.getId(), userId)
                .orElseThrow();

        return new TransactionResponse("-----| Balance card by number %s user %s |----- \n ------| Balance: %s |----- \n"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName(), cardEntity.getBalance()));
    }

    /** ADMIN business logic **/

    @Override
    public TransactionResponse addCard(CreateCardFormDto cardFormDto) {

        UserProfileEntity profileEntity = UserProfileEntity.builder()
                .fullName(cardFormDto.getFullName())
                .email(cardFormDto.getEmail())
                .password(cardFormDto.getPassword())
                .gender(cardFormDto.getGender())
                .birthday(cardFormDto.getBirthday())
                .phoneNumber(cardFormDto.getPhoneNumber())
                .documentType(cardFormDto.getDocumentType())
                .passportNumber(cardFormDto.getPassportNumber())
                .passportSeries(cardFormDto.getPassportSeries())
                .issueBy(cardFormDto.getIssueBy())
                .address(cardFormDto.getAddress())
                .taxId(cardFormDto.getTaxId())
                .citizenship(cardFormDto.getCitizenship())
                .employmentInfo(cardFormDto.getEmploymentInfo())
                .build();

        profileRepository.save(profileEntity);

        CardEntity cardEntity = CardEntity.builder()
                .owner(profileEntity)
                .cardNumber(GenerateCardNumber.generateCardNumber())
                .status(CardStatus.ISSUED)
                .balance(new BigDecimal(0))
                .build();

        cardRepository.save(cardEntity);

        return new TransactionResponse("------| Card by number %s user %s was created and translated in status ISSUED |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Override
    public TransactionResponse activateCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow();

        cardEntity.setStatus(CardStatus.ACTIVE);

        return new TransactionResponse("------| Card by number %s user %s was activated |------"
                .formatted(cardDto.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Override
    public TransactionResponse blockCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow();

        cardEntity.setStatus(CardStatus.BLOCKED);

        return new TransactionResponse("Card by number %s user %s was blocked |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Override
    public TransactionResponse unblockCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow();

        cardEntity.setStatus(CardStatus.ACTIVE);

        return new TransactionResponse("Card bu number %s user %s was unblocked |------"
                .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Override
    public TransactionResponse deleteCard(CardDto cardDto) {
        CardEntity cardEntity = cardRepository.findById(cardDto.getId())
                .orElseThrow();

        String cardNumber = cardEntity.getCardNumber();
        String ownerCardName = cardEntity.getOwner().getFullName();

        cardRepository.delete(cardEntity);

        return new TransactionResponse("-----| Card by number %s user %s was removed |------"
                .formatted(cardNumber, ownerCardName));
    }

    @Override
    public TransactionResponse softDeleteCard(CardDto cardDto) {
        cardRepository.findById(cardDto.getId())
                .orElseThrow()
                .setDeleteFlag(true);

        return new TransactionResponse("-----| Card bu number %s user %s was soft-delete |------");
    }
}
