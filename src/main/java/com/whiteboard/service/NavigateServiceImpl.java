package com.whiteboard.service;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Stack;

@RequiredArgsConstructor
@Service
public class NavigateServiceImpl implements NavigateService {

    private final ConfigurableApplicationContext context;
    private final AlertService alertService;

    private Stack<String> screenStack = new Stack<>();

    private String displayScreen = "welcome.fxml";

    @Override
    public void navigateToScreen(ActionEvent event, String screenToNavigate, Object... argsToNextPage) {
        screenStack.push(displayScreen);
        displayScreen = screenToNavigate;
        navigate(event, argsToNextPage);
    }

    @Override
    public void navigateToLastScreen(ActionEvent event) {
        displayScreen = screenStack.pop();
        navigate(event);
    }

    private void navigate(ActionEvent event, Object... argsToNextPage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + displayScreen));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.setUserData(argsToNextPage);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            alertService.displayErrorAlert(e.getMessage());
        }
    }
}
