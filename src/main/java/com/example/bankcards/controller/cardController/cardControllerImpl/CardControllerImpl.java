package com.example.bankcards.controller.cardController.cardControllerImpl;

import com.example.bankcards.controller.cardController.CardController;
import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.service.cardService.serviceImpl.CardServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/card")
public class CardControllerImpl implements CardController {

    @Resource
    private CardServiceImpl service;

    /** USER endpoints **/

    @GetMapping("/user/get")
    @PreAuthorize("hasRole('USER')")
    @Override
    public CardDto getCard(@RequestHeader("Authorization") String tokenHeader, @PathVariable UUID id) {
        return null;
    }

    @GetMapping("/user/getMy")
    @PreAuthorize("hasRole('USER')")
    @Override
    public List<CardDto> getUserCards(@RequestHeader("Authorization") String tokenHeader) {
        service.getCards(tokenHeader);
        return null;
    }

    /** Служит для отправки другому сервису запрос на создание карты.
     * Пользователь отправляет обязательную для этой процедуры информацию через форму.
     * Админ в свою очередь просматривает эту заявку и регистрирует данные в БД,
     * а эти данные используются для создания БД. Там и дополняется другая информация,
     * такая как, например, номер карты.
    **/
    @PostMapping("/user/req_create")
    @PreAuthorize("hasRole('USER')")
    @Override
    public void requestCreateCard(@RequestHeader("Authorization") String tokenHeader, @RequestBody CreateCardFormDto cardFormDto) {}

    @PostMapping("/user/req_block")
    @PreAuthorize("hasRole('USER')")
    @Override
    public void requestBlockCard(@RequestHeader("Authorization") String tokenHeader, UUID id) {

    }

    @GetMapping("/user/balance")
    @PreAuthorize("hasRole('USER')")
    @Override
    public String showBalance(@RequestHeader("Authorization") String tokenHeader, UUID id) {
        return "";
    }

    @PostMapping("/user/translation")
    @PreAuthorize("hasRole('USER')")
    @Override
    public String transaction(@RequestHeader("Authorization") String tokenHeader, UUID senderId, UUID recipientId) {
        return "";
    }

    /** ADMIN endpoints **/

    @GetMapping("/admin/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<CardDto> getAllCards(@RequestHeader("Authorization") String tokenHeader) {
        return List.of();
    }

    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void addCard(@RequestHeader("Authorization") String userToken, CreateCardFormDto cardFormDto) {
        System.out.println("----| Token: " + userToken + " |----");
        service.addCard(userToken, cardFormDto);
    }

    @PatchMapping("/admin/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void updateCard(@RequestHeader("Authorization") String tokenHeader, @PathVariable UUID id, @RequestBody CardDto card) {

    }

    @DeleteMapping("/admin/remove")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void removeCard(@RequestHeader("Authorization") String tokenHeader, @PathVariable UUID id) {

    }

    @PostMapping("/admin/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void activateCard(@RequestHeader("Authorization") String tokenHeader, UUID id) {

    }

    @PatchMapping("/admin/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void blockCard(@RequestHeader("Authorization") String tokenHeader, UUID id) {

    }

    @PatchMapping("/admin/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void unblockCard(@RequestHeader("Authorization") String tokenHeader, UUID id) {

    }
}
