package com.whiteboard.controller;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.enums.EntityEnum;
import com.whiteboard.enums.RabbitMessageTypeEnum;
import com.whiteboard.rabbitmq.RabbitSender;
import com.whiteboard.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Stack;

import static com.whiteboard.constants.Constants.*;

@Slf4j
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
    private GraphicsContext canvasGraphicContext;
    @FXML
    private ChoiceBox<String> fontChoiceBox;
    @FXML
    private TextField fontSizeTextField;
    @FXML
    private TextArea addTextArea;

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
        canvasGraphicContext = this.canvas.getGraphicsContext2D();
        offScreenCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
        canvasGraphicContext.setLineWidth(DEFAULT_STROKE_WIDTH);
        initFonts();
        setColors(Paint.valueOf(DEFAULT_COLOR), Paint.valueOf(DEFAULT_COLOR));
        setCanvasEvents();
        initServices();
        setBoardContent();
    }

    private void setCanvasEvents() {
        this.canvas.setOnMousePressed(mouseEvent -> {

            if (!(isShapeToHandleNull())) {
                canvasGraphicContext.setFill(Paint.valueOf(currShapeToHandleOrigColor));
                shapeService.updateShapeColor(currShapeToHandle, canvasGraphicContext);
                sendRabbitMessage();
            } else if (!(isTextToHandleNull())) {
                canvasGraphicContext.setFill(Paint.valueOf(currTextToHandleOrigColor));
                textService.updateTextColor(currTextToHandle, canvasGraphicContext);
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
                canvasGraphicContext.setFill(Paint.valueOf(SELECTED_ENTITY_COLOR));
                shapeService.updateShapeColor(currShapeToHandle, canvasGraphicContext);
            } else {
                currTextToHandleOrigColor = currTextToHandle.getColor();
                this.canvasGraphicContext.setFill(Paint.valueOf(SELECTED_ENTITY_COLOR));
                textService.updateTextColor(currTextToHandle, canvasGraphicContext);
            }

            sendRabbitMessage();
        });

        this.canvas.setOnMouseDragged(mouseEvent -> {
            if (isShapeToHandleNull() && isTextToHandleNull()) {
                return;
            }

            pixDiffCount++;

            if (!(isShapeToHandleNull()) && isTimeToUpdateView()) {
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

    public void goBack(ActionEvent event) {
        boardUserConService.removeUserFromBoard();
        navigateService.navigateToLastScreen(event);
    }

    public void createLine(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultLine(canvasGraphicContext);
        sendRabbitMessage();
    }

    public void createCircle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultCircle(canvasGraphicContext);
        sendRabbitMessage();
    }

    public void createRectangle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultRectangle(canvasGraphicContext);
        sendRabbitMessage();
    }

    public void createTriangle(ActionEvent event) {
        undoStack.push(EntityEnum.SHAPE);
        shapeService.createDefaultTriangle(canvasGraphicContext);
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
        setColors(fillColor.getValue(), canvasGraphicContext.getStroke());
    }

    public void outlineColorChangedHandle(ActionEvent event) {
        setColors(canvasGraphicContext.getFill(), strokeColor.getValue());
    }

    public void fontChangeHandle(ActionEvent event) {
        double fontSize;

        try {
            fontSize = Double.parseDouble(fontSizeTextField.getText());
        } catch (NumberFormatException e) {
            fontSize = DEFAULT_FONT_SIZE;
        }

        Font font = Font.font(fontChoiceBox.getValue(), fontSize);

        canvasGraphicContext.setFont(font);

        if (!(isTextToHandleNull())) {
            textService.updateTextFont(currTextToHandle, font);
            sendRabbitMessage();
        }

        fontSizeTextField.setText("");
    }

    public void createMessage(ActionEvent event) {
        String messageContent = messageTextField.getText();
        messageTextField.setText("");

        if (messageContent.isEmpty()) {
            return;
        }

        messageService.addMessage(messageContent);
        sendRabbitMessage(RabbitMessageTypeEnum.Message);
    }

    public void createText(ActionEvent event) {
        if (addTextArea.getText().isEmpty()) {
            return;
        }

        if (!(isTextToHandleNull())) {
            textService.updateText(currTextToHandle, addTextArea.getText());
        } else {
            textService.createText(addTextArea.getText(), canvasGraphicContext);
        }

        addTextArea.setText("");
        undoStack.push(EntityEnum.TEXT);
        sendRabbitMessage();
    }

    private void initFonts() {
        fontChoiceBox.getItems().addAll(textService.getFonts());
        fontChoiceBox.setValue(fontChoiceBox.getItems().get(0));
        canvasGraphicContext.setFont(Font.font(fontChoiceBox.getValue(), DEFAULT_FONT_SIZE));
    }

    private void setColors(Paint fillColor, Paint strokeColor) {
        canvasGraphicContext.setFill(fillColor);
        canvasGraphicContext.setStroke(strokeColor);

        if (isShapeToHandleNull() && isTextToHandleNull()) {
            return;
        }

        if (!(isShapeToHandleNull())) {
            shapeService.updateShapeColor(currShapeToHandle, this.canvasGraphicContext);
            currShapeToHandleOrigColor = currShapeToHandle.getFillColor();
        } else {
            textService.updateTextColor(currTextToHandle, this.canvasGraphicContext);
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
        shapeService.addBoardShapes(canvasGraphicContext);
        textService.addBoardTexts(canvasGraphicContext);
    }

    private boolean isTimeToUpdateView() {
        return pixDiffCount % PIX_DIFFERENCE_TO_UPDATE_VIEW == 0;
    }

    private void sendRabbitMessage(RabbitMessageTypeEnum... rabbitMessageTypeEnum) {
        try {
            RabbitMessageTypeEnum typeEnum = rabbitMessageTypeEnum.length > 0 ?
                    rabbitMessageTypeEnum[0] : RabbitMessageTypeEnum.Board;
            this.rabbitSender.sendMessage(typeEnum);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
