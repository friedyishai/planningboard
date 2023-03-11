package com.whiteboard.controller;

import com.whiteboard.dao.model.User;
import com.whiteboard.general.DBActionResult;
import com.whiteboard.general.UserSession;
import com.whiteboard.service.AlertService;
import com.whiteboard.service.NavigateService;
import com.whiteboard.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AlertService alertService;
    private final NavigateService navigateService;
    private final UserSession userSession;

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
        User user = userService.findByName(username);
        userSession.setUser(user);
        navigateService.navigateToScreen(event, "select-board.fxml");
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
