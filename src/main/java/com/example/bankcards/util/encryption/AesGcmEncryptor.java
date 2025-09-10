package com.example.bankcards.util.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Convert
public final class AesGcmEncryptor implements AttributeConverter<String, String> {

    private final static String ALGORITHM = "AES/GCM/NoPadding";
    private final static int TAG_LENGTH = 12;
    private final static int TAG_LENGTH_BITS = 128;

    private final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final SecretKey key;

    private AesGcmEncryptor(@Value("${key.aes}") String keyBase64) {
        key = new SecretKeySpec(Base64.getDecoder().decode(keyBase64), "AES");
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        byte[] iv = new byte[TAG_LENGTH];
        SECURE_RANDOM.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
        byte[] encryptedData = null;

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            byte[] cipherText = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            encryptedData = ByteBuffer.allocate(iv.length + cipherText.length)
                    .put(iv).put(cipherText).array();
        } catch (Exception e) {}

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        byte[] decoderData = Base64.getDecoder().decode(dbData);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decoderData);

        byte[] iv = new byte[TAG_LENGTH];
        byteBuffer.get(iv);

        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        byte[] decryptedData = null;

        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            decryptedData = cipher.doFinal(cipherText);
        } catch (Exception e) {}

        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
