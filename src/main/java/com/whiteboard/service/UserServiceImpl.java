package com.whiteboard.service;

import com.whiteboard.dao.model.User;
import com.whiteboard.dao.repository.UserRepository;
import com.whiteboard.util.DBActionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public DBActionResult login(User user) {

        User userFromDb = findByName(user.getName());

        if (!(isUserExists(userFromDb)) ||
                !(isPasswordEquals(user.getPassword(), userFromDb.getPassword()))) {

            return DBActionResult.builder()
                    .isSuccess(false)
                    .failureReason(INCORRECT_USERNAME_OR_PASSWORD)
                    .build();
        }

        userFromDb.setIsActive(true);

        return DBActionResult.builder().isSuccess(true).build();
    }

    @Override
    public DBActionResult register(User user) {

        if (!(isUserNameAvailable(user.getName()))) {
            return DBActionResult.builder()
                    .isSuccess(false)
                    .failureReason(UNAVAILABLE_USER_NAME).build();
        }

        if (!(isEmailAvailable(user.getEmail()))) {
            return DBActionResult.builder()
                    .isSuccess(false)
                    .failureReason(UNAVAILABLE_EMAIL).build();
        }

        userRepository.save(user);

        return DBActionResult.builder().isSuccess(true).build();
    }

    @Override
    public User findByName(String username) {
        return userRepository.findByName(username);
    }

    private boolean isEmailAvailable(String email) {
        User userFromDb = userRepository.findByEmail(email);
        return !(isUserExists(userFromDb));
    }

    private boolean isUserNameAvailable(String username) {
        User userFromDb = findByName(username);
        return !(isUserExists(userFromDb));
    }

    private boolean isUserExists(User user) {
        return null != user;
    }

    private boolean isPasswordEquals(String userPassword, String userFromDbPassword) {
        return userPassword.equals(userFromDbPassword);
    }
}
