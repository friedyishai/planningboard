package com.whiteboard.controller;

import com.whiteboard.dto.DBActionResult;
import com.whiteboard.service.AlertService;
import com.whiteboard.service.BoardService;
import com.whiteboard.service.NavigateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static com.whiteboard.constants.Constants.BOARD_NAME_IS_REQUIRED;
import static com.whiteboard.constants.Constants.UNAVAILABLE_BOARD_NAME;

@Component
@RequiredArgsConstructor
public class NewBoardController implements Initializable {

    private final BoardService boardService;
    private final AlertService alertService;
    private final NavigateService navigateService;

    @FXML
    private TextField boardNameField;

    @FXML
    private Button createNewBoardBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boardNameField.setOnAction(e -> {
            boolean hasBoardName = boardNameField.getText().trim().isEmpty();
            createNewBoardBtn.setDisable(!(hasBoardName));
        });
    }

    public void createNewBoard(ActionEvent event) {
        String boardName = boardNameField.getText();

        if (boardName.isEmpty()) {
            alertService.displayErrorAlert(BOARD_NAME_IS_REQUIRED);
            return;
        }

        DBActionResult result = boardService.createNewBoard(boardName);
        handle(event, result);
    }

    private void handle(ActionEvent event, DBActionResult result) {
        if (result.isSuccess()) {
            navigateService.navigateToScreen(event, "board.fxml");
        } else {
            alertService.displayErrorAlert(UNAVAILABLE_BOARD_NAME);
        }
    }

    public void goBack(ActionEvent event) {
        navigateService.navigateToLastScreen(event);
    }
}
