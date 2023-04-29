package com.whiteboard.controller;

import com.whiteboard.dao.model.User;
import com.whiteboard.dto.DBActionResult;
import com.whiteboard.service.user.UserService;
import com.whiteboard.service.utils.AlertService;
import com.whiteboard.service.utils.NavigateService;
import com.whiteboard.singletons.PrimaryStage;
import com.whiteboard.singletons.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.whiteboard.constants.Constants.SELECT_BOARD_PAGE;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AlertService alertService;
    private final NavigateService navigateService;
    private final UserSession userSession;
    private final PrimaryStage primaryStage;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    public void register(ActionEvent event) {
        User user = User.builder()
                .email(emailField.getText())
                .name(usernameField.getText())
                .password(passwordField.getText())
                .isActive(true)
                .build();

        DBActionResult result = userService.register(user);

        handleResult(event, user.getName(), result);
    }

    public void login(ActionEvent event) {
        User user = User.builder()
                .name(usernameField.getText())
                .password(passwordField.getText())
                .isActive(true)
                .build();

        DBActionResult result = userService.login(user);

        handleResult(event, user.getName(), result);
    }

    public void goBack(ActionEvent event) {
        navigateService.navigateToLastScreen(event);
    }

    private void handleResult(ActionEvent event, String username, DBActionResult result) {
        if (result.isSuccess()) {
            handleSuccess(event, username);
        } else {
            handleFailure(result);
        }
    }

    private void handleSuccess(ActionEvent event, String username) {
        primaryStage.getPrimaryStage().setTitle(username);
        User user = userService.findByName(username);
        user.setIsActive(true);
        userService.saveUser(user);
        userSession.setUser(user);
        navigateService.navigateToScreen(event, SELECT_BOARD_PAGE);
    }

    private void handleFailure(DBActionResult result) {
        alertService.displayErrorAlert(result.getFailureReason());
        clearForm();
    }

    private void clearForm() {
        usernameField.setText(null);
        passwordField.setText(null);
        if (null != emailField) {
            emailField.setText(null);
        }
    }
}
