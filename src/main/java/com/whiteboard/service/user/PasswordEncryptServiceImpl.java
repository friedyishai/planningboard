package com.whiteboard.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Service
public class PasswordEncryptServiceImpl {

    @Value("${spring.security.encrypt-algorithm}")
    private String encryptAlgorithm;

    @Value("${spring.security.secret-key}")
    private String secretKey;

    public String encrypt(String password) throws Exception {
        Key key = new SecretKeySpec(secretKey.getBytes(), encryptAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedPassword = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedPassword);
    }

    public String decrypt(String encryptedPassword) throws Exception {
        Key key = new SecretKeySpec(secretKey.getBytes(), encryptAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedPassword = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedPassword);
    }
}