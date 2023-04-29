package com.whiteboard.controller;

import com.whiteboard.service.utils.AlertService;
import com.whiteboard.service.utils.NavigateService;
import com.whiteboard.singletons.PrimaryStage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.whiteboard.constants.AppMessages.BEFORE_EXIT_MESSAGE;
import static com.whiteboard.constants.Constants.LOGIN_PAGE;
import static com.whiteboard.constants.Constants.REGISTER_PAGE;

@RequiredArgsConstructor
@Component
public class WelcomeController {

    private final NavigateService navigateService;
    private final AlertService alertService;
    private final PrimaryStage primaryStage;

    public void setStageOnExit(Stage primaryStage) {
        this.primaryStage.setPrimaryStage(primaryStage);
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

    public void showLoginForm(ActionEvent event) {
        navigateService.navigateToScreen(event, LOGIN_PAGE);
    }

    public void showRegisterForm(ActionEvent event) {
        navigateService.navigateToScreen(event, REGISTER_PAGE);
    }
}
