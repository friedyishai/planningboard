package com.whiteboard.controller;

import com.whiteboard.enums.EntityEnum;
import com.whiteboard.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Stack;

import static com.whiteboard.constants.Constants.DEFAULT_FONT_SIZE;

@RequiredArgsConstructor
@Component
@Lazy
public class BoardController {

    private final ShapeService shapeService;
    private final TextService textService;
    private final MessageService messageService;
    private final BoardUserConService boardUserConService;
    private final NavigateService navigateService;

    @FXML
    private ListView<String> messagesList;
    @FXML
    private TextField messageTextField;
    @FXML
    private ColorPicker fillColor;
    @FXML
    private ColorPicker strokeColor;
    @FXML
    private Canvas canvas;
    @FXML
    private ChoiceBox<String> fontChoiceBox;
    @FXML
    private TextField fontSizeTextField;

    private final Stack<EntityEnum> undoStack = new Stack<>();
    private final Stack<EntityEnum> redoStack = new Stack<>();

    @FXML
    public void initialize() {
//        canvas.getGraphicsContext2D().setLineWidth(DEFAULT_STROKE_WIDTH);
        setFont();
        setColors();
        setBoardContent();
        //TODO Test undo redo (maybe need to empty stacks when action chain is break)
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
            textService.redo(canvas);
        }

        undoStack.push(entity);
        canvasRepaint();
    }

    public void undo(ActionEvent event) {
        if (undoStack.empty()) {
            return;
        }

        EntityEnum entity = undoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.undo(canvas);
        } else {
            textService.undo(canvas);
        }

        redoStack.push(entity);
        canvasRepaint();
    }

    public void fillColorChangedHandle(ActionEvent event) {
        setColors();
    }

    public void outlineColorChangedHandle(ActionEvent event) {
        setColors();
    }

    public void fontChangeHandle(ActionEvent event) {
        double fontSize;

        try {
            fontSize = Double.parseDouble(fontSizeTextField.getText());
        } catch (NumberFormatException e) {
            fontSize = DEFAULT_FONT_SIZE;
        }

        canvas.getGraphicsContext2D().setFont(Font.font(fontChoiceBox.getValue(), fontSize));
    }

    public void createMessage(ActionEvent event) {
        String messageContent = messageTextField.getText();

        if (messageContent.isEmpty()) {
            return;
        }

        String messageToDisplay = messageService.addMessage(messageContent);
        messagesList.getItems().add(messageToDisplay);
    }

    public void createText(ActionEvent event) {
        undoStack.push(EntityEnum.TEXT);
        textService.createDefaultText(canvas.getGraphicsContext2D());
    }

    private void setFont() {
        List<String> f = textService.getFonts();
        fontChoiceBox.getItems().addAll(textService.getFonts());
        fontChoiceBox.setValue(fontChoiceBox.getItems().get(0));
        canvas.getGraphicsContext2D().setFont(Font.font(fontChoiceBox.getItems().get(0), DEFAULT_FONT_SIZE));
    }

    private void setColors() {
        canvas.getGraphicsContext2D().setFill(fillColor.getValue());
        canvas.getGraphicsContext2D().setStroke(strokeColor.getValue());
    }

    private void setBoardContent() {
        boardUserConService.addUserToBoard();
        messagesList.getItems().addAll(messageService.getMessages());
        shapeService.addBoardShapes(canvas.getGraphicsContext2D());
        textService.addBoardTexts(canvas.getGraphicsContext2D());
    }

    private void canvasRepaint() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        shapeService.addBoardShapes(canvas.getGraphicsContext2D());
        textService.addBoardTexts(canvas.getGraphicsContext2D());
    }
}
