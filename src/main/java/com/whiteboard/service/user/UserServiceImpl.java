package com.whiteboard.service.user;

import com.whiteboard.dao.model.User;
import com.whiteboard.dao.repository.UserRepository;
import com.whiteboard.dto.DBActionResult;
import com.whiteboard.service.board.BoardUserConService;
import com.whiteboard.singletons.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import static com.whiteboard.constants.AppMessages.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, ApplicationListener<ContextClosedEvent> {

    private final BoardUserConService boardUserConService;
    private final ApplicationContext applicationContext;
    private final UserSession userSession;
    private final UserRepository userRepository;
    private final PasswordEncryptServiceImpl passwordEncryptService;

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

        if (userFromDb.getIsActive()) {
            return DBActionResult.builder()
                    .isSuccess(false)
                    .failureReason(USER_ALREADY_LOG_IN)
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


        try {
            user.setPassword(passwordEncryptService.encrypt(user.getPassword()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        userRepository.save(user);

        return DBActionResult.builder().isSuccess(true).build();
    }

    @Override
    public User findByName(String username) {
        return userRepository.findByName(username);
    }

    @Override
    public String getUserById(Integer uid) {
        return userRepository.findById(uid).orElseThrow().getName();
    }

    @Override
    public void logout() {
        if (null != userSession) {
            User user = userSession.getUser();
            if (null != user) {
                user.setIsActive(false);
                saveUser(user);
            }
        }
    }

    @Override
    public void saveUser(User user) {
        userRepository.saveAndFlush(user);
    }

    private boolean isEmailAvailable(String email) {
        User userFromDb = userRepository.findByEmail(email);
        return !(isUserExists(userFromDb));
    }

    private boolean isUserNameAvailable(String username) {
        User userFromDb = findByName(username);
        return !(isUserExists(userFromDb));
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            boardUserConService.removeUserFromBoard();
            logout();
        }
    }

    private boolean isUserExists(User user) {
        return null != user;
    }

    private boolean isPasswordEquals(String userPassword, String userFromDbPassword) {
        boolean result = false;

        try {
            result = userPassword.equals(passwordEncryptService.decrypt(userFromDbPassword));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;
    }
}
