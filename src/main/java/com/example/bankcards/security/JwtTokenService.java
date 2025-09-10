package com.example.bankcards.security;

import com.example.bankcards.model.dto.jwt.JwtTokenDto;
import com.example.bankcards.model.dto.registerUser.RegisterUserDto;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    @Resource
    private JWKSource<SecurityContext> jwkSource;

    @Value("${security.provider}")
    private String provider;

    @Value("${security.port}")
    private String port;

    public JwtTokenDto generateToken(RegisterUserDto registerUserDto) {

        Instant now = Instant.now();

        try {

            RSAKey rsaKey = jwkSource.get(new JWKSelector(new JWKMatcher.Builder().build()), null).getFirst().toRSAKey();

            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .build();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer("http://%s:%s".formatted(provider, port))
                    .subject(registerUserDto.getId().toString())
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .expirationTime((Date.from(now.plusSeconds(86400))))
                    .claim("role", "ROLE_" + registerUserDto.getUserRole())
                    .claim("name", registerUserDto.getName())
                    .claim("email", registerUserDto.getEmail())
                    .claim("password", registerUserDto.getPassword())
                    .build();

            SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

            signedJWT.sign(new RSASSASigner(rsaKey.toPrivateKey()));

            System.out.println(
                      "\n-------| User ROLE before generate JWT token |-------\n"
                    + "              | Role: " + claimsSet.getClaims().get("role") + " |"
                    + "\n-------| User ROLE before generate JWT token |-------\n");

            return new JwtTokenDto(signedJWT.serialize());
        } catch (JOSEException e) { throw new RuntimeException(e); }
    }

    public JWTClaimsSet decoderToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token.replace("Bearer ", "").trim());

            System.out.println(
                    "\n------| JSON parse token |------\n"
                    + signedJWT.getHeader().toJSONObject() + "\n"
                    + signedJWT.getJWTClaimsSet().toJSONObject()
                    + "\n------| JSON parse token |------\n");

//            RSAKey publicKey = jwkSource
//                    .get(new JWKSelector(
//                                    new JWKMatcher.Builder()
//                                            .keyID(
//                                                    signedJWT.getHeader().getKeyID()).build()),
//                            null)
//                    .getFirst()
//                    .toPublicJWK()
//                    .toRSAKey();
//
//            JWSVerifier jwsVerifier = new RSASSAVerifier(publicKey);

            return signedJWT.getJWTClaimsSet();
        } catch (ParseException exc) { throw new RuntimeException(exc); }
    }


    public String extractRole(String token) {

        System.out.println(
                "\n----------| TOKEN |----------\n" +
                decoderToken(token).getClaims().get("role").toString()
                + "\n----------| TOKEN |----------\n");

        String decoderToken = decoderToken(token).getClaims().get("role").toString().replace("ROLE_", "");

        return decoderToken;
    }

}
