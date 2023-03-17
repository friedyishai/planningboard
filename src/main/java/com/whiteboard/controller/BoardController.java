package com.whiteboard.controller;

import com.sun.javafx.geom.Shape;
import com.whiteboard.enums.EntityEnum;
import com.whiteboard.util.UserSession;
import com.whiteboard.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Stack;

import static com.whiteboard.constants.Constants.DEFAULT_STROKE_WIDTH;

@RequiredArgsConstructor
@Component
@Lazy
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

    private final Stack<EntityEnum> undoStack = new Stack<>();
    private final Stack<EntityEnum> redoStack = new Stack<>();

    @FXML
    public void initialize() {
        canvas.getGraphicsContext2D().setLineWidth(DEFAULT_STROKE_WIDTH);
        boardUserConService.addUserToBoard();
        messagesList.getItems().addAll(messageService.getMessages());
        shapeService.addBoardShapes(canvas.getGraphicsContext2D());
        textService.addBoardTexts(canvas.getGraphicsContext2D());
        setColors();
    }

    public void goBack(ActionEvent event) {
        boardUserConService.removeUserFromBoard();
        navigateService.navigateToLastScreen(event);
    }

    public void createLine(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultLine(canvas.getGraphicsContext2D());
    }

    public void createCircle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultCircle(canvas.getGraphicsContext2D());
    }

    public void createRectangle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultRectangle(canvas.getGraphicsContext2D());
    }

    public void createTriangle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultTriangle(canvas.getGraphicsContext2D());
    }

    public void redo(ActionEvent event) {
        if (redoStack.empty()) {
            return;
        }

        EntityEnum entity = redoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.redo(canvas);
        } else {
            textService.redo();
        }

        undoStack.push(entity);
    }

    public void undo(ActionEvent event) {
        if (undoStack.empty()) {
            return;
        }

        EntityEnum entity = undoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.undo(canvas);
        } else {
            textService.undo();
        }

        redoStack.push(entity);
    }

    public void fillColorChangedHandle(ActionEvent event) {
        setColors();
    }

    public void outlineColorChangedHandle(ActionEvent event) {
        setColors();
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

    private void setColors() {
        canvas.getGraphicsContext2D().setFill(fillColor.getValue());
        canvas.getGraphicsContext2D().setStroke(outlineColor.getValue());
    }
}
