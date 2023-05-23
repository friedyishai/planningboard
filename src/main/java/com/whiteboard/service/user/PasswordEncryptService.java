package com.whiteboard.service.user;

public interface PasswordEncryptService {

    String encrypt(String password) throws Exception;

    String decrypt(String encryptedPassword) throws Exception;
}
