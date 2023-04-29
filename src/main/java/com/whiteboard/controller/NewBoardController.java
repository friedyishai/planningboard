package com.whiteboard.controller;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dto.DBActionResult;
import com.whiteboard.service.board.BoardService;
import com.whiteboard.service.utils.AlertService;
import com.whiteboard.service.utils.NavigateService;
import com.whiteboard.singletons.DisplayedBoard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static com.whiteboard.constants.AppMessages.BOARD_NAME_IS_REQUIRED;
import static com.whiteboard.constants.Constants.BOARD_PAGE;

@Component
@RequiredArgsConstructor
public class NewBoardController implements Initializable {

    private final BoardService boardService;
    private final AlertService alertService;
    private final NavigateService navigateService;
    private final DisplayedBoard displayedBoard;

    @FXML
    private TextField boardNameField;

    @FXML
    private Button createNewBoardBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setOnBoardNameChangeHandle();
    }

    public void createNewBoard(ActionEvent event) {
        String boardName = boardNameField.getText();

        if (boardName.isEmpty()) {
            alertService.displayErrorAlert(BOARD_NAME_IS_REQUIRED);
            return;
        }

        DBActionResult result = boardService.createNewBoard(boardName);
        handleResult(event, result, boardName);
    }

    public void goBack(ActionEvent event) {
        navigateService.navigateToLastScreen(event);
    }

    private void setOnBoardNameChangeHandle() {
        boardNameField.setOnAction(e -> {
            boolean noBoardName = boardNameField.getText().trim().isEmpty();
            createNewBoardBtn.setDisable(noBoardName);
        });
    }

    private void handleResult(ActionEvent event, DBActionResult result, String boardName) {
        if (result.isSuccess()) {
            Board newBoard = boardService.getBoardByName(boardName);
            displayedBoard.setBoard(newBoard);
            navigateService.navigateToScreen(event, BOARD_PAGE);
        } else {
            alertService.displayErrorAlert(result.getFailureReason());
        }
    }
}
