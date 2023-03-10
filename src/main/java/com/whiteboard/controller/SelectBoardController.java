package com.whiteboard.controller;

import com.whiteboard.dao.model.Board;
import com.whiteboard.service.AlertService;
import com.whiteboard.service.BoardService;
import com.whiteboard.service.NavigateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.whiteboard.constants.Constants.UNSELECTED_BOARD_ERROR_MSG;

@Component
@RequiredArgsConstructor
public class SelectBoardController implements Initializable {

    private final BoardService boardService;
    private final AlertService alertService;
    private final NavigateService navigateService;
    private final BoardController boardController;

    @FXML
    private ListView<String> boardsListView;

    @FXML
    private Button goToBoardBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Board> boardsList = boardService.getAllBoards();
        boardsListView.getItems().addAll(boardsList.stream().map(Board::getName).collect(Collectors.toList()));
        boardsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        boardsListView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> goToBoardBtn.setDisable(newValue.intValue() == -1)
        );
    }

    public void goToBoardBtnClickedHandle(ActionEvent event) {
        String selectedBoardName = boardsListView.getSelectionModel().getSelectedItem();

        if (selectedBoardName != null) {
            Board selectedBoard = boardService.getBoardByName(selectedBoardName);
            boardController.setDisplayedBoard(selectedBoard);
            navigateService.navigateToScreen(event, "board.fxml");
        } else {
            alertService.displayErrorAlert(UNSELECTED_BOARD_ERROR_MSG);
        }
    }

    public void showNewBoardForm(ActionEvent event) {
        this.navigateService.navigateToScreen(event, "new-board.fxml");
    }

    public void goBack(ActionEvent event) {
        navigateService.navigateToLastScreen(event);
    }
}
