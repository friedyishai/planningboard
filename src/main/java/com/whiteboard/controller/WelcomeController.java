package com.whiteboard.controller;

import com.whiteboard.service.NavigateService;
import javafx.event.ActionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WelcomeController {

    private final NavigateService navigateService;

    public void showLoginForm(ActionEvent event) {
        navigateService.navigateToScreen(event, "login.fxml");
    }

    public void showRegisterForm(ActionEvent event) {
        navigateService.navigateToScreen(event, "register.fxml");
    }
}
