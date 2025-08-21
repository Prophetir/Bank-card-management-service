package com.example.bankcards.service.cardService.serviceImpl;

import com.example.bankcards.model.dto.card.CardDto;
import com.example.bankcards.model.dto.card.CreateCardFormDto;
import com.example.bankcards.model.entity.CardEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenService;
import com.example.bankcards.service.cardService.CardService;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.GenerateCardNumber;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CardServiceImpl implements CardService {

    @Resource
    private CardRepository cardRepository;

    @Resource
    private JwtTokenService tokenService;

    @Resource
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    /** USER business logic **/

    @Override
    public void getCard(String tokenHeader, CardDto cardDto) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();
        String id = tokenService.decoderToken(tokenHeader).getJWTID();

        System.out.printf("----| Get Card %s : %d |----\n", role, id);
    }

    @Override
    public void getCards(String tokenHeader) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();

        System.out.printf("----| Get Cards %s |----", role);
    }

    @Override
    public void transaction(String tokenHeader, CardDto cardDto) {

    }

    @Override
    public void requestCreateCard(String tokenHeader, CreateCardFormDto createCardFormDto) {

    }

    @Override
    public void requestBlockCard(String tokenHeader, CardDto cardDto) {

    }

    @Override
    public void showBalance(String tokenHeader, CardDto cardDto) {

    }

    /** ADMIN business logic **/

    @Override
    public void addCard(String userToken, CreateCardFormDto cardFormDto) {
        JWTClaimsSet userTokenClaims = tokenService.decoderToken(userToken);

        CardEntity cardEntity = CardEntity.builder()
                .owner(userRepository.findById(
                        UUID.fromString(userTokenClaims.getClaims().get("id").toString()))
                        .get())
                .cardNumber(GenerateCardNumber.generateCardNumber(userToken, userTokenClaims.getClaims().get("id").toString()))
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal(0))
                .build();

        cardRepository.save(cardEntity);

        String role = tokenService.decoderToken(userToken).getClaims().get("role").toString();
        String id = tokenService.decoderToken(userToken).getJWTID();
        System.out.printf("----| Add Card for User id: %s with role: %s |----\n", role, id);

        System.out.printf("----| Add Card with id: %s and with number: %s, and with owner: %s, and with status: %s|----\n"
        ,cardEntity.getId(), cardEntity.getCardNumber(), cardEntity.getOwner(), cardEntity.getStatus());
    }

    @Override
    public void activateCard(String tokenHeader, CardDto cardDto) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();
        String id = tokenService.decoderToken(tokenHeader).getJWTID();

        System.out.println("----| Activate Card $s : $d |----".formatted(role, id));
    }

    @Override
    public void updateCard(String tokenHeader, CardDto cardDto) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();
        String id = tokenService.decoderToken(tokenHeader).getJWTID();

        System.out.println("----| Update Card $s : $d |----".formatted(role, id));
    }

    @Override
    public void removeCard(String tokenHeader, UUID id) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();
        String idd = tokenService.decoderToken(tokenHeader).getSubject();

        System.out.println("----| Remove Card $s : $d |----".formatted(role, idd));
    }

    @Override
    public void blockCard(String tokenHeader, UUID id) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();
        String idd = tokenService.decoderToken(tokenHeader).getJWTID();

        System.out.println("----| Block Card $s : $d |----".formatted(role, idd));
    }

    @Override
    public void unblockCard(String tokenHeader, UUID id) {
        String role = tokenService.decoderToken(tokenHeader).getClaims().get("role").toString();
        String idd = tokenService.decoderToken(tokenHeader).getJWTID();

        System.out.println("----| Unblock Card $s : $d |----".formatted(role, idd));
    }
}
