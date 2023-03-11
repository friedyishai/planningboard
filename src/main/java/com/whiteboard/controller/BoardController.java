package com.whiteboard.controller;

import com.whiteboard.dao.model.Board;
import com.whiteboard.enums.Entity;
import com.whiteboard.general.UserSession;
import com.whiteboard.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Stack;

@RequiredArgsConstructor
@Component
public class BoardController {

    private final ShapeService shapeService;
    private final TextService textService;
    private final MessageService messageService;
    private final BoardUserConService boardUserConService;
    private final NavigateService navigateService;
    private final UserSession userSession;

    @FXML
    private ListView<String> messagesList;
    @FXML
    private TextField messageTextField;
    @FXML
    private ColorPicker fillColor;
    @FXML
    private ColorPicker outlineColor;
    @FXML
    private Canvas canvas;

    private final Stack<Entity> undoStack = new Stack<>();
    private final Stack<Entity> redoStack = new Stack<>();

    @FXML
    public void initialize() {
        boardUserConService.addUserToBoard();
        messagesList.getItems().addAll(messageService.getMessages());
        shapeService.addBoardShapes(canvas.getGraphicsContext2D());
        textService.addBoardTexts(canvas.getGraphicsContext2D());
    }

    public void goBack(ActionEvent event) {
        boardUserConService.removeUserFromBoard();
        navigateService.navigateToLastScreen(event);
    }

    public void createLine(ActionEvent event) {
        undoStack.push(Entity.SHAPE);
        shapeService.createDefaultLine(canvas.getGraphicsContext2D());
    }

    public void createCircle(ActionEvent event) {
        undoStack.push(Entity.SHAPE);
        shapeService.createDefaultCircle(canvas.getGraphicsContext2D());
    }

    public void createRectangle(ActionEvent event) {
        undoStack.push(Entity.SHAPE);
        shapeService.createDefaultRectangle(canvas.getGraphicsContext2D());
    }

    public void createTriangle(ActionEvent event) {
        undoStack.push(Entity.SHAPE);
        shapeService.createDefaultTriangle(canvas.getGraphicsContext2D());
    }

    public void redo(ActionEvent event) {
        if (redoStack.empty()) {
            return;
        }

        Entity entity = redoStack.pop();

        if (entity.equals(Entity.SHAPE)) {
            shapeService.redo();
        } else {
            textService.redo();
        }

        undoStack.push(entity);
    }

    public void undo(ActionEvent event) {
        if (undoStack.empty()) {
            return;
        }

        Entity entity = undoStack.pop();

        if (entity.equals(Entity.SHAPE)) {
            shapeService.undo();
        } else {
            textService.undo();
        }

        redoStack.push(entity);
    }

    public void fillColorChangedHandle(ActionEvent event) {
        canvas.getGraphicsContext2D().setFill(fillColor.getValue());
    }

    public void outlineColorChangedHandle(ActionEvent event) {
        canvas.getGraphicsContext2D().setStroke(outlineColor.getValue());
    }

    public void fontChangeHandle(ActionEvent event) {
    }

    public void createMessage(ActionEvent event) {
        String messageContent = messageTextField.getText();

        if(messageContent.isEmpty()) {
            return;
        }

        String messageToDisplay = messageService.addMessage(messageContent);
        messagesList.getItems().add(messageToDisplay);
    }

    public void createText(ActionEvent event) {
        textService.createDefaultText(canvas.getGraphicsContext2D());
    }
}
