package com.whiteboard.controller;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.enums.EntityEnum;
import com.whiteboard.rabbitmq.RabbitSender;
import com.whiteboard.service.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.Stack;

import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Component
public class BoardController {

    private final ShapeService shapeService;
    private final TextService textService;
    private final MessageService messageService;
    private final BoardUserConService boardUserConService;
    private final NavigateService navigateService;
    private final BoardContentService boardContentService;
    private final RabbitSender rabbitSender;

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

    private ShapeEntity currShapeToHandle;
    private TextEntity currTextToHandle;
    private String currShapeToHandleOrigColor;
    private String currTextToHandleOrigColor;
    private Canvas offScreenCanvas;
    private int pixDiffCount = 0;

    @FXML
    public void initialize() {
        offScreenCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
        initFonts();
        setColors();
        setCanvasEvents();
        initServices();
        setBoardContent();
    }

    private void setCanvasEvents() {
        this.canvas.setOnMousePressed(mouseEvent -> {

            if (!(isShapeToHandleNull())) {
                canvas.getGraphicsContext2D().setFill(Paint.valueOf(currShapeToHandleOrigColor));
                shapeService.updateShapeColor(currShapeToHandle, canvas.getGraphicsContext2D());
                sendRabbitMessage();
            } else if (!(isTextToHandleNull())) {
                canvas.getGraphicsContext2D().setFill(Paint.valueOf(currTextToHandleOrigColor));
                textService.updateTextColor(currTextToHandle, canvas.getGraphicsContext2D());
                sendRabbitMessage();
            }

            this.canvas.setCursor(Cursor.CLOSED_HAND);

            this.currShapeToHandle = shapeService.findClickedShape(mouseEvent);
            this.currTextToHandle = textService.findClickedText(mouseEvent);

            if (!(isShapeToHandleNull()) && !(isTextToHandleNull())) {
                if (isShapeToHandleMoreUpdatedThanTextToHandle()) {
                    currTextToHandle = null;
                } else {
                    currShapeToHandle = null;
                }
            }

            if (isShapeToHandleNull() && isTextToHandleNull()) {
                return;
            }

            if (!(isShapeToHandleNull())) {
                currShapeToHandleOrigColor = currShapeToHandle.getFillColor();
                this.canvas.getGraphicsContext2D().setFill(Paint.valueOf(SELECTED_ENTITY_COLOR));
                shapeService.updateShapeColor(currShapeToHandle, canvas.getGraphicsContext2D());
            } else {
                currTextToHandleOrigColor = currTextToHandle.getColor();
                this.canvas.getGraphicsContext2D().setFill(Paint.valueOf(SELECTED_ENTITY_COLOR));
                textService.updateTextColor(currTextToHandle, canvas.getGraphicsContext2D());
            }

            sendRabbitMessage();
        });

        this.canvas.setOnMouseDragged(mouseEvent -> {
            if (isShapeToHandleNull() && isTextToHandleNull()) {
                return;
            }

            pixDiffCount++;

            if (isTimeToUpdateView() && !(isShapeToHandleNull())) {
                this.shapeService.updateShapeLocation(currShapeToHandle, mouseEvent);
                sendRabbitMessage();
            } else if (isTimeToUpdateView()) {
                this.textService.updateTextLocation(currTextToHandle, mouseEvent);
                sendRabbitMessage();
            }
        });

        this.canvas.setOnMouseReleased(mouseEvent -> {
            sendRabbitMessage();
            this.canvas.setCursor(Cursor.OPEN_HAND);
        });
    }

    private boolean isTimeToUpdateView() {
        return pixDiffCount % PIX_DIFFERENCE_TO_UPDATE_VIEW == 0;
    }

    public void goBack(ActionEvent event) {
        boardUserConService.removeUserFromBoard();
        navigateService.navigateToLastScreen(event);
    }

    public void createLine(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultLine(canvas.getGraphicsContext2D());
        sendRabbitMessage();
    }

    public void createCircle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultCircle(canvas.getGraphicsContext2D());
        sendRabbitMessage();
    }

    public void createRectangle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultRectangle(canvas.getGraphicsContext2D());
        sendRabbitMessage();
    }

    public void createTriangle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultTriangle(canvas.getGraphicsContext2D());
        sendRabbitMessage();
    }

    public void redo(ActionEvent event) {
        if (redoStack.empty()) {
            return;
        }

        EntityEnum entity = redoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.redo();
        } else {
            textService.redo();
        }

        undoStack.push(entity);
        sendRabbitMessage();
    }

    public void undo(ActionEvent event) {
        if (undoStack.empty()) {
            return;
        }

        EntityEnum entity = undoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.undo();
        } else {
            textService.undo();
        }

        redoStack.push(entity);
        sendRabbitMessage();
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

        Font font = Font.font(fontChoiceBox.getValue(), fontSize);

        canvas.getGraphicsContext2D().setFont(font);

        if (!(isTextToHandleNull())) {
            textService.updateTextFont(currTextToHandle, font);
            sendRabbitMessage();
        }
    }

    public void createMessage(ActionEvent event) {
        String messageContent = messageTextField.getText();
        messageTextField.setText("");

        if (messageContent.isEmpty()) {
            return;
        }

        messageService.addMessage(messageContent);
        sendRabbitMessage();
    }

    public void createText(ActionEvent event) {
        undoStack.push(EntityEnum.TEXT);
        textService.createDefaultText(canvas.getGraphicsContext2D());
        sendRabbitMessage();
    }

    private void initFonts() {
        fontChoiceBox.getItems().addAll(textService.getFonts());
        fontChoiceBox.setValue(fontChoiceBox.getItems().get(0));
        canvas.getGraphicsContext2D().setFont(Font.font(fontChoiceBox.getValue(), DEFAULT_FONT_SIZE));
    }

    private void setColors() {
        canvas.getGraphicsContext2D().setFill(fillColor.getValue());
        canvas.getGraphicsContext2D().setStroke(strokeColor.getValue());

        if (isShapeToHandleNull() && isTextToHandleNull()) {
            return;
        }

        if (!(isShapeToHandleNull())) {
            shapeService.updateShapeColor(currShapeToHandle, this.canvas.getGraphicsContext2D());
            currShapeToHandleOrigColor = currShapeToHandle.getFillColor();
        } else {
            textService.updateTextColor(currTextToHandle, this.canvas.getGraphicsContext2D());
            currTextToHandleOrigColor = currTextToHandle.getColor();
        }

        sendRabbitMessage();
    }

    private boolean isShapeToHandleMoreUpdatedThanTextToHandle() {
        return currShapeToHandle.getUpdateDate().isAfter(currTextToHandle.getUpdateDate());
    }

    private boolean isShapeToHandleNull() {
        return null == currShapeToHandle;
    }

    private boolean isTextToHandleNull() {
        return null == currTextToHandle;
    }

    private void initServices() {
        boardContentService.init(canvas, offScreenCanvas, this.messagesList);
        this.shapeService.init();
        this.textService.init();
        this.messageService.init();
        messagesList.scrollTo(messagesList.getItems().size() - 1);
    }

    private void setBoardContent() {
        boardUserConService.addUserToBoard();
        messagesList.getItems().addAll(messageService.getMessages());
        shapeService.addBoardShapes(canvas.getGraphicsContext2D());
        textService.addBoardTexts(canvas.getGraphicsContext2D());
    }

    private void sendRabbitMessage() {
        this.rabbitSender.sendMessage(new Message(new byte[]{}));
    }
}
