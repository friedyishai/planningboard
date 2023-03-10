package com.whiteboard.controller;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.MessageEntity;
import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.dto.UserSession;
import com.whiteboard.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BoardController {

    private final ShapeBoardConService shapeBoardConService;
    private final ShapeEntityService shapeService;
    private final TextBoardConService textBoardConService;
    private final TextEntityService textEntityService;
    private final MessageBoardUserConSerivce messageBoardUserConSerivce;
    private final MessageEntityService messageEntityService;
    private final PaintService paintService;
    private final BoardUserConService boardUserConService;
    private final NavigateService navigateService;
    private final UserSession userSession;

    @FXML
    private AnchorPane anchorPane;

    private Board displayedBoard;

    @FXML
    public void initialize() {
        List<Integer> boardShapesIds = shapeBoardConService.getAllShapeIds(displayedBoard);
        List<ShapeEntity> boardShapes = shapeService.getAllShapes(boardShapesIds);
        List<Integer> boardTextIds = textBoardConService.getAllTextIds(displayedBoard);
        List<TextEntity> boardTexts = textEntityService.getAllTexts(boardTextIds);
        List<Integer> messageIds = messageBoardUserConSerivce.getMessageIds(displayedBoard);
        List<MessageEntity> boardMessages = messageEntityService.getAllMessages(messageIds);
        Canvas canvas = new Canvas();
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        boardShapes.forEach(shapeEntity -> {
            paintService.createShapeAndAddToContext(shapeEntity, graphicsContext);
        });
        boardTexts.forEach(textEntity -> {
            paintService.addTextToContext(textEntity, graphicsContext);
        });
        anchorPane.getChildren().add(canvas);
    }

    public void setDisplayedBoard(Board board) {
        displayedBoard = board;
        boardUserConService.addUserToBoard(displayedBoard);
    }

    public void goBack(ActionEvent event) {
        boardUserConService.removeUserFromBoard(displayedBoard);
        navigateService.navigateToLastScreen(event);
    }
}
