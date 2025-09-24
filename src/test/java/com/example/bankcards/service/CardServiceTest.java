package com.example.bankcards.service;

import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.card.TransactionCardForm;
import com.example.bankcards.model.entity.CardEntity;
import com.example.bankcards.model.entity.ProfileEntity;
import com.example.bankcards.model.entity.RegisterUserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.ProfileRepository;
import com.example.bankcards.service.cardService.serviceImpl.CardServiceImpl;
import com.example.bankcards.service.profileService.serviceImpl.ProfileServiceImpl;
import com.example.bankcards.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CardServiceImpl.class,
        ProfileServiceImpl.class,
        ModelMapper.class
})
public class CardServiceTest {

    @MockitoBean
    private CardRepository cardRepository;

    @MockitoBean
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileServiceImpl profileService;

    @Autowired
    private CardServiceImpl cardService;

    @Autowired
    private ModelMapper modelMapper;

    private RegisterUserEntity registerEntity;

    private ProfileEntity profileEntity;

    private CardEntity cardEntity;

    @BeforeEach
    void init() {
        registerEntity = RegisterUserEntity.builder()
                .id(UUID.randomUUID())
                .name("Vlad Anonim")
                .userRole(UserRole.ADMIN)
                .email("vlad@anonim.com")
                .phone("+7 111 (222) 33-44")
                .password("vladAnonimPassword").build();

        profileEntity = ProfileEntity.builder()
                .id(UUID.randomUUID())
                .relatedUser(registerEntity)
                .cards(new ArrayList<>())
                .fullName("Valislav Babin")
                .gender(GenderType.MAN)
                .birthday(Instant.now().toString())
                .phoneNumber(registerEntity.getPhone())
                .documentType(DocumentType.PASSPORT)
                .passportNumber("11 22")
                .passportSeries("123456")
                .issueBy("Dimur Horbachov")
                .address("Vlad 17 h. 21")
                .taxId("1234567890")
                .citizenship("Russian").build();

        registerEntity.setUserProfile(profileEntity);

        cardEntity = CardEntity.builder()
                .id(UUID.randomUUID())
                .cardNumber(GenerateCardNumber.generateCardNumber())
                .balance(BigDecimal.valueOf(5000))
                .owner(profileEntity)
                .status(CardStatus.ACTIVE).build();

        profileEntity.getCards().add(cardEntity);
    }

    /** USER business logic **/

    @Test
    @DisplayName("Проверяет корректность возвращаемого CardDto с маскированным номером карты")
    void getCardTest() {

        String expectedCardNumber = MaskPhoneAndCardNumber.maskCardNumber(cardEntity.getCardNumber());

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));

        CardDto resultDto = cardService.getCard(cardEntity.getId());

        verify(cardRepository, times(1))
                .findById(any(UUID.class));

        assertThat(resultDto)
                .as("Проверка корректности маппинка и маскирования номера телефона")
                .extracting(CardDto::getId, CardDto::getCardNumber, CardDto::getBalance, CardDto::getStatus)
                .containsExactly(cardEntity.getId(), expectedCardNumber, cardEntity.getBalance(), cardEntity.getStatus());
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void getCard_whenCardNotFound_throwNotFoundExceptionTest() {

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> cardService.getCard(cardEntity.getId()));

        verify(cardRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность транзакции перевода денежных средств по номеру карты")
    void transactionByCardNumberTest() {
        CardEntity senderCard = cardEntity;

        CardEntity recipientCard = createRecipientCardEntity();

        TransactionCardForm transactionForm = createTransactionCardForm(senderCard.getId(), recipientCard.getOwner().getPhoneNumber());

        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));
        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(senderCard));
        when(cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString()))
                .thenReturn(Optional.of(recipientCard));

        String transactionResponse = cardService.transactionByCardNumber(registerEntity.getId(), transactionForm).response();

        verify(profileRepository, times(1))
                .findByUserId(any(UUID.class));
        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));
        verify(cardRepository, times(1))
                .findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString());

        assertThat(senderCard.getBalance())
                .as("Проверяет корректность списания средств у карты отправителя")
                .isEqualTo(new BigDecimal(1000));
        assertThat(recipientCard.getBalance())
                .as("Проверяет корректность пополнения средств у карты получателя")
                .isEqualTo(new BigDecimal(8000));
        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном переводе денежных средств по номеру карты")
                .isEqualTo("------| Transaction from card by number: %s by card by phone number: %s successfully |-------\n"
                        .formatted(senderCard.getCardNumber(), recipientCard.getOwner().getPhoneNumber()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта отправителя не была найдена")
    void transactionByCardNumberCard_whenSenderCardNotFound_throwNotFoundExceptionTest() {

        CardEntity recipientCard = createRecipientCardEntity();

        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));
        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.transactionByCardNumber(registerEntity.getId(),
                        createTransactionCardForm(cardEntity.getId(), recipientCard.getCardNumber())));

        verify(profileRepository, times(1))
                .findByUserId(any(UUID.class));
        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта получателя не была найдена")
    void transactionByCardNumberCard_whenRecipientCardNotFound_throwNotFoundExceptionTest() {

        CardEntity recipientCard = createRecipientCardEntity();

        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));
        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));
        when(cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString()))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.transactionByCardNumber(registerEntity.getId(),
                        createTransactionCardForm(cardEntity.getId(), recipientCard.getCardNumber())));

        verify(profileRepository, times(1))
                .findByUserId(any(UUID.class));
        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));
        verify(cardRepository, times(1))
                .findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString());
    }

    /** !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * ДОРАБОТАТЬ МЕТОД РЕПОЗИТОРИЯ ДЛЯ ПОИСКА КАРТЫ ПО НОМЕРУ КАРТЫ ИЛИ ПО ТЕЛЕФОНУ
     * А ТАКЖЕ ДОБАВИТЬ ДРУГИЕ ТЕСТЫ НА ИСКЛЮЧЕНИЯ
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! **/
    @Test
    @DisplayName("Проверяет корректность транзакции перевода денежных средств по номеру телефона")
    void transactionByPhoneNumberTest() {
        CardEntity senderCard = cardEntity;

        CardEntity recipientCard = createRecipientCardEntity();

        TransactionCardForm transactionForm = createTransactionCardForm(senderCard.getId(), recipientCard.getOwner().getPhoneNumber());

        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(senderCard));
        when(cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString()))
                .thenReturn(Optional.of(recipientCard));
        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));

        cardService.transactionByPhoneNumber(registerEntity.getId(), transactionForm);

        assertThat(senderCard.getBalance())
                .as("Проверяет корректность списания средств у карты отправителя")
                .isEqualTo(new BigDecimal(1000));

        assertThat(recipientCard.getBalance())
                .as("Проверяет корректность пополнения средств у карты получателя")
                .isEqualTo(new BigDecimal(8000));

        verify(cardRepository, times(1))
                .findUserCardById(senderCard.getId(), profileEntity.getId());
        verify(cardRepository, times(1))
                .findCardByCardNumberOrByCardNumberAndProfileId(profileEntity.getId(), transactionForm.getRecipientCardOrPhoneNumber());
        verify(profileRepository, times(1))
                .findByUserId(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта отправителя не была найдена")
    void transactionByPhoneNumber_whenSenderCardNotFound_throwNotFoundExceptionTest() {
        CardEntity recipientCard = createRecipientCardEntity();

        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));
        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.transactionByCardNumber(registerEntity.getId(),
                        createTransactionCardForm(cardEntity.getId(), recipientCard.getOwner().getPhoneNumber())));

        verify(profileRepository, times(1))
                .findByUserId(any(UUID.class));
        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта отправителя не была найдена")
    void transactionByPhoneNumber_whenRecipientCardNotFound_throwNotFoundExceptionTest() {
        CardEntity recipientCard = createRecipientCardEntity();

        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));
        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));
        when(cardRepository.findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.transactionByCardNumber(registerEntity.getId(),
                        createTransactionCardForm(cardEntity.getId(), recipientCard.getOwner().getPhoneNumber())));

        verify(profileRepository, times(1))
                .findByUserId(any(UUID.class));
        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));
        verify(cardRepository, times(1))
                .findCardByCardNumberOrByCardNumberAndProfileId(any(UUID.class), anyString());
    }


    @Test
    @DisplayName("Проверяет корректность возвращаемого значения баланса карты")
    void showBalanceTest() {

        CardDto cardDto = createCardDto();

        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));
        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));

        String transactionResponse = cardService.showBalance(registerEntity.getId(), cardDto).response();

        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном выводе баланса карты")
                .isEqualTo("-----| Balance card by number %s user %s: %s |-----"
                        .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName(), cardEntity.getBalance()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void showBalance_whenCardNotFound_throwNotFoundExceptionTest() {

        when(cardRepository.findUserCardById(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());
        when(profileRepository.findByUserId(any(UUID.class)))
                .thenReturn(Optional.of(profileEntity));

        assertThrows(NotFoundException.class, () ->
                cardService.showBalance(cardEntity.getId(), createCardDto()));

        verify(cardRepository, times(1))
                .findUserCardById(any(UUID.class), any(UUID.class));
    }

    /** ADMIN business logic **/

    /**
     * Позже необходимо реализовать тесты на некорректный ввод данных
     * **/
    @Test
    @DisplayName("Проверяет создание новой карты")
    void addCardTest() {

        cardEntity.setStatus(CardStatus.ISSUED);

        PassportData cardFormDto = PassportData.builder()
                .fullName(profileEntity.getFullName())
                .address(profileEntity.getAddress())
                .birthday(profileEntity.getBirthday())
                .citizenship(profileEntity.getCitizenship())
                .gender(profileEntity.getGender().toString())
                .documentType(profileEntity.getDocumentType().toString())
                .passportNumber(profileEntity.getPassportNumber())
                .passportSeries(profileEntity.getPassportSeries())
                .build();

        when(profileRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(profileEntity));
        when(cardRepository.save(any(CardEntity.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        String transactionResponse = cardService.addCard(cardFormDto).response();

        ArgumentCaptor<CardEntity> cardCaptor = ArgumentCaptor.forClass(CardEntity.class);

        verify(cardRepository, times(1))
                .save(cardCaptor.capture());

        CardEntity savedCard = cardCaptor.getValue();

        verify(cardRepository, times(1))
                .save(any(CardEntity.class));
        verify(profileRepository, times(1))
                .findOne(any(Specification.class));

        assertThat(savedCard)
                .as("Проверяет необходимые поля карты")
                .extracting(CardEntity::getOwner, CardEntity::getStatus, CardEntity::getBalance)
                .containsExactly(profileEntity, cardEntity.getStatus(), BigDecimal.ZERO);

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения при успешном завершении метода")
                .isEqualTo("------| Card by number %s user %s was created and translated in status ISSUED |------"
                        .formatted(savedCard.getCardNumber(), savedCard.getOwner().getFullName()));
    }

    @Test
    @DisplayName("Проверяет корректность установки статуса ACTIVE карты")
    void activateCardTest() {

        cardEntity.setStatus(CardStatus.ISSUED);
        CardDto cardDto = createCardDto();

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));
        when(cardRepository.save(any(CardEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<CardEntity> argumentCaptor = ArgumentCaptor.forClass(CardEntity.class);

        String transactionResponse = cardService.activateCard(cardDto).response();

        verify(cardRepository, times(1))
                .save(argumentCaptor.capture());

        CardEntity savedCard = argumentCaptor.getValue();

        verify(cardRepository, times(1))
                .findById(cardDto.getId());

        assertThat(savedCard.getStatus())
                .as("Проверяет корректность установленного статуса с ISSUED на ACTIVE")
                .isEqualTo(CardStatus.ACTIVE);

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном изменении статуса карты на ACTIVE")
                .isEqualTo("------| Card by number %s user %s was activated |------"
                        .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void activateCard_whenCardNotFound_throwNotFoundExceptionTest() {
        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.activateCard(createCardDto()));

        verify(cardRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность установки статуса BLOCK карты")
    void blockCardTest() {
        CardDto cardDto = createCardDto();

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));
        when(cardRepository.save(any(CardEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<CardEntity> argumentCaptor = ArgumentCaptor.forClass(CardEntity.class);

        String transactionResponse = cardService.blockCard(cardDto).response();

        verify(cardRepository, times(1))
                .save(argumentCaptor.capture());
        verify(cardRepository, times(1))
                .findById(cardDto.getId());

        CardEntity capturedCard = argumentCaptor.getValue();

        assertThat(capturedCard.getStatus())
                .as("Проверяет корректность установленного статуса с BLOCK")
                .isEqualTo(CardStatus.BLOCKED);

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном изменении статуса карты на BLOCK")
                .isEqualTo("Card by number %s user %s was blocked |------"
                        .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void blockCard_whenCardNotFound_throwNotFoundExceptionTest() {
        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.blockCard(createCardDto()));

        verify(cardRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность установки статуса UNBLOCK карты")
    void unblockCardTest() {

        when(cardRepository.save(any(CardEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));

        ArgumentCaptor<CardEntity> argumentCaptor = ArgumentCaptor.forClass(CardEntity.class);

        String transactionResponse = cardService.unblockCard(createCardDto()).response();

        verify(cardRepository, times(1))
                .save(argumentCaptor.capture());
        verify(cardRepository, times(1))
                .findById(any(UUID.class));

        CardEntity capturedCard = argumentCaptor.getValue();

        assertThat(capturedCard.getStatus())
                .as("Проверяет корректность установленного статуса с BLOCK на UNBLOCK")
                .isEqualTo(CardStatus.UNBLOCKED);

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном изменении статуса карты на BLOCK")
                .isEqualTo("Card by number %s user %s was unblocked |------"
                        .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void unblockCard_whenCardNotFound_throwNotFoundExceptionTest() {

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.unblockCard(createCardDto()));

        verify(cardRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность установки статуса DELETED карты и установку флага soft-delete на true")
    void softDeleteCardTest() {

        CardDto cardDto = createCardDto();

        when(cardRepository.save(any(CardEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));

        ArgumentCaptor<CardEntity> argumentCaptor = ArgumentCaptor.forClass(CardEntity.class);

        String transactionResponse = cardService.softDeleteCard(cardDto).response();

        verify(cardRepository, times(1))
                .save(argumentCaptor.capture());
        verify(cardRepository, times(1))
                .findById(cardDto.getId());

        CardEntity capturedCard = argumentCaptor.getValue();

        assertThat(capturedCard.getStatus())
                .as("Проверяет корректность установки статуса DELETED")
                .isEqualTo(CardStatus.DELETED);

        assertThat(capturedCard.getDeleteFlag())
                .as("Проверяет корректность установки флага soft-delete с false на true")
                .isEqualTo(true);

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном изменении статуса карты на DELETED и флага soft-delete на true")
                .isEqualTo("-----| Card by number %s user %s was soft-delete |------"
                        .formatted(cardDto.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void softDeleteCard_whenCardNotFound_throwNotFoundExceptionTest() {

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.softDeleteCard(createCardDto()));

        verify(cardRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    @DisplayName("Проверяет корректность глубокого удаления карты")
    void deleteCardTest() {

        CardDto cardDto = createCardDto();

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(cardEntity));

        String transactionResponse = cardService.deleteCard(cardDto).response();

        verify(cardRepository, times(1))
                .findById(cardDto.getId());
        verify(cardRepository, times(1))
                .delete(any(CardEntity.class));

        assertThat(transactionResponse)
                .as("Проверяет корректность ответного сообщения об успешном удалении карты")
                .isEqualTo("-----| Card by number %s user %s was removed |------"
                        .formatted(cardEntity.getCardNumber(), cardEntity.getOwner().getFullName()));
    }

    @Test
    @DisplayName("Проверяет корректность выбрасываемого исключения NotFoundException, если карта не была найдена")
    void deleteCard_whenCardNotFound_throwNotFoundExceptionTest() {

        when(cardRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                cardService.deleteCard(createCardDto()));

        verify(cardRepository, times(1))
                .findById(any(UUID.class));
    }

    private CardDto createCardDto() {
        return CardDto.builder()
                .id(cardEntity.getId())
                .cardNumber(cardEntity.getCardNumber())
                .status(cardEntity.getStatus())
                .balance(cardEntity.getBalance())
                .build();
    }

    private CardEntity createRecipientCardEntity() {
        return CardEntity.builder()
                .id(UUID.randomUUID())
                .cardNumber("4444 3333 2222 1111")
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(4000))
                .owner(profileEntity)
                .build();
    }

    private TransactionCardForm createTransactionCardForm(UUID senderCardId, String recipientPhoneNumber) {
        return TransactionCardForm.builder()
                .senderCardId(senderCardId)
                .recipientCardOrPhoneNumber(recipientPhoneNumber)
                .amount(new BigDecimal(4000))
                .build();
    }
}