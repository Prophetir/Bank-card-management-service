package com.example.bankcards.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Configuration
public class AuthorityServerConfig {

    @Value("${key.private}")
    private String privateKey;

    @Value("${key.public}")
    private String publicKey;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("my-client")
                .clientSecret("{noop}secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read")
                .scope("write")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        JWKSet jwkSet = new JWKSet(generateRsaKey());

        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    public RSAKey generateRsaKey() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey loadedPrivateKey = loadPrivateRsaKey(keyFactory);
        PublicKey loadedPublicKey = loadPublicRsaKey(keyFactory);

        return new RSAKey.Builder(
                (RSAPublicKey) loadedPublicKey)
                .privateKey(loadedPrivateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    public RSAPrivateKey loadPrivateRsaKey(KeyFactory keyFactory) throws Exception {

        byte[] loadedPrivate = readKey(privateKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(loadedPrivate);

        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public RSAPublicKey loadPublicRsaKey(KeyFactory keyFactory) throws Exception {

        byte[] loadedPublic = readKey(publicKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(loadedPublic);

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private byte[] readKey(String path) {

        try {
            return Base64.getDecoder().decode(Files.readString(Path.of(path)).replaceAll("\\s", ""));
        } catch (IOException exc) { throw new RuntimeException(exc); }
    }
}
