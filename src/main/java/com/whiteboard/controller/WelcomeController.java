package com.whiteboard.controller;

import com.whiteboard.service.AlertService;
import com.whiteboard.service.NavigateService;
import com.whiteboard.service.UserService;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.whiteboard.constants.Constants.BEFORE_EXIT_MESSAGE;

@RequiredArgsConstructor
@Component
public class WelcomeController {

    private final NavigateService navigateService;
    private final AlertService alertService;

    public void setStageOnExit(Stage primaryStage) {
        setOnExitEventListener(primaryStage);
    }

    public void showLoginForm(ActionEvent event) {
        navigateService.navigateToScreen(event, "login.fxml");
    }

    public void showRegisterForm(ActionEvent event) {
        navigateService.navigateToScreen(event, "register.fxml");
    }

    private void setOnExitEventListener(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = alertService.createConfirmAlert(BEFORE_EXIT_MESSAGE);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    primaryStage.close();
                }
            });
        });
    }
}
