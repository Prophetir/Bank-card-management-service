package com.example.bankcards.controller.cardController.cardControllerImpl;

import com.example.bankcards.controller.cardController.CardController;
import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.PassportData;
import com.example.bankcards.model.dto.profile.CreateProfileFormDto;
import com.example.bankcards.model.dto.card.TransactionCardForm;
import com.example.bankcards.model.dto.response.TransactionResponse;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.cardService.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardControllerImpl implements CardController {

    private final CardService service;

    private final JwtTokenService tokenService;

    /** USER endpoints **/

    @GetMapping("/user/get")
    @PreAuthorize("hasRole('USER')")
    @Override
    public CardDto getCard(@RequestBody CardDto cardDto) {
        return service.getCard(cardDto.getId());
    }

    @GetMapping("/user/getMy")
    @PreAuthorize("hasRole('USER')")
    @Override
    public List<CardDto> getUserCards(@RequestHeader("Authorization") String tokenHeader) {
        return service.getCards(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()));
    }

    /** Служит для отправки другому сервису запрос на создание карты.
     * Пользователь отправляет обязательную для этой процедуры информацию через форму.
     * Админ в свою очередь просматривает эту заявку и регистрирует данные в БД,
     * а эти данные используются для создания сущности в БД.
    **/
    @PostMapping("/user/req_create")
    @PreAuthorize("hasRole('USER')")
    @Override
    public TransactionResponse requestCreateCard(@RequestHeader("Authorization") String tokenHeader, @RequestBody CreateProfileFormDto cardFormDto) {
        return service.requestCreateCard(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), cardFormDto);
    }

    @PostMapping("/user/req_block")
    @PreAuthorize("hasRole('USER')")
    @Override
    public TransactionResponse requestBlockCard(@RequestHeader("Authorization") String tokenHeader, CardDto cardDto) {
        return service.requestBlockCard(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), cardDto);
    }

    @GetMapping("/user/balance")
    @PreAuthorize("hasRole('USER')")
    @Override
    public TransactionResponse showBalance(@RequestHeader("Authorization") String tokenHeader, @RequestBody CardDto cardDto) {
        return service.showBalance(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), cardDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transaction/card-number")
    @Override
    public TransactionResponse transactionByCardNumber(@RequestHeader("Authorization") String tokenHeader, @RequestBody TransactionCardForm numberForm) {
        return service.transactionByCardNumber(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), numberForm);
    }

    @PostMapping("/transaction/phone-number")
    @PreAuthorize("hasRole('USER')")
    @Override
    public TransactionResponse transactionByPhoneNumber(@RequestHeader("Authorization") String tokenHeader, @RequestBody TransactionCardForm phoneForm) {
        return service.transactionByPhoneNumber(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), phoneForm);
    }

    @PostMapping("/transaction/user-cards")
    @PreAuthorize("hasRole('USER')")
    @Override
    public TransactionResponse transactionBetweenUserCards(@RequestHeader("Authorization") String tokenHeader, TransactionCardForm numberForm) {
        return service.transactionBetweenUserCards(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()), numberForm);
    }

    /** ADMIN endpoints **/

    @GetMapping("/admin/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<CardDto> getAllCards(@RequestHeader("Authorization") String tokenHeader) {
        return service.getCards(UUID.fromString(tokenService.decoderToken(tokenHeader).getSubject()));
    }

    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse addCard(@RequestBody PassportData cardFormDto) {
        return service.addCard(cardFormDto);
    }

    @PostMapping("/admin/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse activateCard(@RequestBody CardDto cardDto) {
        return service.activateCard(cardDto);
    }

    @PatchMapping("/admin/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse blockCard(@RequestBody CardDto cardDto) {
        return service.blockCard(cardDto);
    }

    @PatchMapping("/admin/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse unblockCard(@RequestBody CardDto cardDto) {
        return service.unblockCard(cardDto);
    }

    @DeleteMapping("/admin/remove")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse deleteCard(@RequestBody CardDto cardDto) {
        return service.deleteCard(cardDto);
    }

    @PostMapping("admin/soft-delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public TransactionResponse softDeleteCard(@RequestBody CardDto cardDto) {
        return service.softDeleteCard(cardDto);
    }
}
