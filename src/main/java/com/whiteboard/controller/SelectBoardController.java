package com.whiteboard.controller;

import com.whiteboard.dao.model.Board;
import com.whiteboard.service.board.BoardService;
import com.whiteboard.service.user.UserService;
import com.whiteboard.service.utils.AlertService;
import com.whiteboard.service.utils.NavigateService;
import com.whiteboard.singletons.DisplayedBoard;
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

import static com.whiteboard.constants.AppMessages.UNSELECTED_BOARD_ERROR_MSG;
import static com.whiteboard.constants.Constants.BOARD_PAGE;
import static com.whiteboard.constants.Constants.NEW_BOARD_PAGE;

@Component
@RequiredArgsConstructor
public class SelectBoardController implements Initializable {

    private final BoardService boardService;
    private final UserService userService;
    private final AlertService alertService;
    private final NavigateService navigateService;
    private final DisplayedBoard displayedBoard;

    @FXML
    private ListView<String> boardsListView;

    @FXML
    private Button goToBoardBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBoardsList();
        setOnSelectBoardHandle();
    }

    public void goToBoardBtnClickedHandle(ActionEvent event) {
        String selectedBoardName = boardsListView.getSelectionModel().getSelectedItem();

        if (null != selectedBoardName) {
            Board selectedBoard = boardService.getBoardByName(selectedBoardName);
            displayedBoard.setBoard(selectedBoard);
            navigateService.navigateToScreen(event, BOARD_PAGE);
        } else {
            alertService.displayErrorAlert(UNSELECTED_BOARD_ERROR_MSG);
        }
    }

    public void showNewBoardForm(ActionEvent event) {
        this.navigateService.navigateToScreen(event, NEW_BOARD_PAGE);
    }

    public void goBack(ActionEvent event) {
        userService.logout();
        navigateService.navigateToLastScreen(event);
    }

    private void initBoardsList() {
        boardsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        List<Board> boardsList = boardService.getAllBoards();
        boardsListView.getItems().addAll(boardsList.stream().map(Board::getName).collect(Collectors.toList()));
    }

    private void setOnSelectBoardHandle() {
        boardsListView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> goToBoardBtn.setDisable(newValue.intValue() == -1)
        );
    }
}
