package com.whiteboard.controller;

import com.whiteboard.dao.model.User;
import com.whiteboard.dto.DBActionResult;
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

        handle(event, result);
    }

    public void login(ActionEvent event) {
        User user = User.builder()
                .name(usernameField.getText())
                .password(passwordField.getText())
                .isActive(true)
                .build();

        DBActionResult result = userService.login(user);

        handle(event, result);
    }

    private void handle(ActionEvent event, DBActionResult result) {
        if (result.isSuccess()) {
            navigateService.navigateToScreen(event, "select-board.fxml");
        } else {
            alertService.displayErrorAlert(result.getFailureReason());
            clearForm();
        }
    }

    private void clearForm() {
        usernameField.setText(null);
        passwordField.setText(null);
        if (null != emailField) {
            emailField.setText(null);
        }
    }

    public void goBack(ActionEvent event) {
        navigateService.navigateToLastScreen(event);
    }
}
