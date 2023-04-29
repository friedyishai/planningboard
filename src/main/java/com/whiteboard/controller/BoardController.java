package com.whiteboard.controller;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.enums.EntityEnum;
import com.whiteboard.enums.RabbitMessageTypeEnum;
import com.whiteboard.enums.ShapeEnum;
import com.whiteboard.rabbitmq.RabbitSender;
import com.whiteboard.service.board.BoardContentService;
import com.whiteboard.service.board.BoardUserConService;
import com.whiteboard.service.message.MessageService;
import com.whiteboard.service.shape.ShapeService;
import com.whiteboard.service.text.TextService;
import com.whiteboard.service.utils.AlertService;
import com.whiteboard.service.utils.NavigateService;
import com.whiteboard.singletons.PrimaryStage;
import com.whiteboard.singletons.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Stack;

import static com.whiteboard.constants.AppDefaultValues.DEFAULT_COLOR;
import static com.whiteboard.constants.AppMessages.BEFORE_EXIT_MESSAGE;
import static com.whiteboard.constants.AppMessages.BEFORE_LEAVE_BOARD_MESSAGE;
import static com.whiteboard.constants.Constants.PIX_DIFFERENCE_TO_UPDATE_VIEW;
import static com.whiteboard.constants.Constants.SELECT_BOARD_PAGE;

@Slf4j
@RequiredArgsConstructor
@Component
public class BoardController {

    private final AlertService alertService;
    private final PrimaryStage primaryStage;
    private final ShapeService shapeService;
    private final TextService textService;
    private final MessageService messageService;
    private final BoardUserConService boardUserConService;
    private final NavigateService navigateService;
    private final BoardContentService boardContentService;
    private final RabbitSender rabbitSender;
    private final UserSession userSession;

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
    private ChoiceBox<String> fontSizeChoiceBox;
    @FXML
    private ChoiceBox<String> widthChoiceBox;
    @FXML
    private ChoiceBox<String> heightChoiceBox;
    @FXML
    private ChoiceBox<String> radiusxChoiceBox;
    @FXML
    private ChoiceBox<String> radiusyChoiseBox;
    @FXML
    private ChoiceBox<String> startxChoiceBox;
    @FXML
    private ChoiceBox<String> startyChoiceBox;
    @FXML
    private ChoiceBox<String> endxChoiceBox;
    @FXML
    private ChoiceBox<String> endyChoiceBox;
    @FXML
    private TextArea addTextArea;

    private final Stack<EntityEnum> undoStack = new Stack<>();
    private final Stack<EntityEnum> redoStack = new Stack<>();

    private ShapeEntity currShapeToHandle;
    private TextEntity currTextToHandle;
    private Canvas offScreenCanvas;
    private int pixDiffCount = 0;

    @FXML
    public void initialize() {
        initCanvasRelatedVariables();
        initFonts();
        initShapeSizes();
        setColors(Paint.valueOf(DEFAULT_COLOR), Paint.valueOf(DEFAULT_COLOR));
        setCanvasEvents();
        initServices();
        setBoardContent();
        setOnExitHandle();
    }

    public void goBack(ActionEvent event) {
        Alert alert = alertService.createConfirmAlert(BEFORE_LEAVE_BOARD_MESSAGE);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userLeftBoardHandle();
                navigateService.navigateToScreen(event, SELECT_BOARD_PAGE);
            }
        });
    }

    public void createLine() {
        shapeService.createDefaultLine(canvasGraphicContext);
        handleAfterCreateShape();
    }

    public void createCircle() {
        shapeService.createDefaultCircle(canvasGraphicContext);
        handleAfterCreateShape();
    }

    public void createRectangle() {
        shapeService.createDefaultRectangle(canvasGraphicContext);
        handleAfterCreateShape();
    }

    public void createTriangle() {
        shapeService.createDefaultTriangle(canvasGraphicContext);
        handleAfterCreateShape();
    }

    private void handleAfterCreateShape() {
        sendRabbitMessage(RabbitMessageTypeEnum.Shape);
        undoStack.push(EntityEnum.SHAPE);
        setEntityToHandleOrigColor();
    }

    public void redo() {
        setEntityToHandleOrigColor();

        if (redoStack.empty()) {
            return;
        }

        EntityEnum entity = redoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.redo();
            sendRabbitMessage(RabbitMessageTypeEnum.Shape);
        } else {
            textService.redo();
            sendRabbitMessage(RabbitMessageTypeEnum.Text);
        }

        undoStack.push(entity);
    }

    public void undo() {
        setEntityToHandleOrigColor();

        if (undoStack.empty()) {
            return;
        }

        EntityEnum entity = undoStack.pop();

        if (entity.equals(EntityEnum.SHAPE)) {
            shapeService.undo();
            sendRabbitMessage(RabbitMessageTypeEnum.Shape);
        } else {
            textService.undo();
            sendRabbitMessage(RabbitMessageTypeEnum.Text);
        }

        redoStack.push(entity);
    }

    public void fillColorChangedHandle() {
        Paint stroke = canvasGraphicContext.getStroke();

        if (!(isShapeToHandleNull())) {
            stroke = Paint.valueOf(currShapeToHandle.getStrokeColor());
        }

        setColors(fillColor.getValue(), stroke);
    }

    public void strokeColorChangedHandle() {
        Paint fill;

        if (!(isShapeToHandleNull())) {
            fill = Paint.valueOf(currShapeToHandle.getFillColor());
        } else if (!(isTextToHandleNull())) {
            fill = Paint.valueOf(currTextToHandle.getColor());
        } else {
            fill = canvasGraphicContext.getFill();
        }

        setColors(fill, strokeColor.getValue());
    }

    private void setColors(Paint fillColor, Paint strokeColor) {
        canvasGraphicContext.setFill(fillColor);
        canvasGraphicContext.setStroke(strokeColor);

        if (!(isShapeToHandleNull())) {
            shapeService.updateDisplayOrigColor(currShapeToHandle, true);
            shapeService.updateShapeColor(currShapeToHandle, canvasGraphicContext, true);
            sendRabbitMessage(RabbitMessageTypeEnum.Shape);
            currShapeToHandle = null;
        } else if (!(isTextToHandleNull())) {
            textService.updateDisplayOrigColor(currTextToHandle, true);
            textService.updateTextColor(currTextToHandle, canvasGraphicContext);
            sendRabbitMessage(RabbitMessageTypeEnum.Text);
            currTextToHandle = null;
        }
    }

    public void fontFamilyChangeHandle() {
        Font font = Font.font(fontChoiceBox.getValue(), canvasGraphicContext.getFont().getSize());
        setFont(font);
    }

    public void fontSizeChangeHandle() {
        Font font = Font.font(canvasGraphicContext.getFont().getName(), Double.parseDouble(fontSizeChoiceBox.getValue()));
        setFont(font);
    }

    private void setFont(Font font) {
        canvasGraphicContext.setFont(font);

        if (isTextToHandleNull()) {
            return;
        }

        textService.updateTextFont(currTextToHandle, font);
        sendRabbitMessage(RabbitMessageTypeEnum.Text);
        setEntityToHandleOrigColor();
    }

    public void createMessage() {
        String messageContent = messageTextField.getText();

        messageTextField.clear();

        if (messageContent.isEmpty()) {
            return;
        }

        messageService.addMessage(messageContent, LocalDateTime.now());
        sendRabbitMessage(RabbitMessageTypeEnum.Message);
    }

    public void createText() {
        setEntityToHandleOrigColor();

        if (addTextArea.getText().isEmpty()) {
            return;
        }

        canvasGraphicContext.setFill(fillColor.getValue());
        canvasGraphicContext.setFont(Font.font(fontChoiceBox.getValue(),
                Double.parseDouble(fontSizeChoiceBox.getValue())));

        textService.createText(addTextArea.getText(), canvasGraphicContext);

        sendRabbitMessage(RabbitMessageTypeEnum.Text);
        addTextArea.clear();
        undoStack.push(EntityEnum.TEXT);
    }

    public void updateText() {
        if (addTextArea.getText().isEmpty() || isTextToHandleNull()) {
            return;
        }

        textService.updateText(currTextToHandle, addTextArea.getText());
        sendRabbitMessage(RabbitMessageTypeEnum.Text);
        setEntityToHandleOrigColor();
        addTextArea.setText("");
    }

    public void deletePressedHandle() {
        if (!(isShapeToHandleNull())) {
            undoStack.remove(EntityEnum.SHAPE);
            shapeService.deleteShape(currShapeToHandle);
            sendRabbitMessage(RabbitMessageTypeEnum.Shape);
        } else {
            undoStack.remove(EntityEnum.TEXT);
            textService.deleteText(currTextToHandle);
            sendRabbitMessage(RabbitMessageTypeEnum.Text);
        }
    }

    public void polygonWidthChangeHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Rectangle)) &&
                !(isCurrShapeToHandleIsOfType(ShapeEnum.Triangle))) {
            return;
        }

        double height = (currShapeToHandle.getShapeType().equals(ShapeEnum.Rectangle)) ?
                currShapeToHandle.getHeight() :
                Math.abs(currShapeToHandle.getY2() - currShapeToHandle.getY1());

        updateShapeSize(new double[]{Double.parseDouble(widthChoiceBox.getValue()), height});
    }

    public void polygonHeightChangeHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Rectangle)) &&
                !(isCurrShapeToHandleIsOfType(ShapeEnum.Triangle))) {
            return;
        }

        double width = (currShapeToHandle.getShapeType().equals(ShapeEnum.Rectangle)) ?
                currShapeToHandle.getWidth() :
                Math.abs(currShapeToHandle.getX3() - currShapeToHandle.getX1());

        updateShapeSize(new double[]{width, Double.parseDouble(heightChoiceBox.getValue())});
    }

    public void circleRadiusXChangedHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Circle))) {
            return;
        }

        updateShapeSize(new double[]{Double.parseDouble(radiusxChoiceBox.getValue()), currShapeToHandle.getRadiusY()});
    }

    public void circleRadiusYChangedHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Circle))) {
            return;
        }

        updateShapeSize(new double[]{currShapeToHandle.getRadiusX(), Double.parseDouble(radiusyChoiseBox.getValue())});
    }

    public void lineStartXChangedHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Line))) {
            return;
        }

        updateShapeSize(new double[]{
                Double.parseDouble(startxChoiceBox.getValue()),
                currShapeToHandle.getY1(),
                currShapeToHandle.getX2(),
                currShapeToHandle.getY2(),
        });
    }

    public void lineStartYChangedHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Line))) {
            return;
        }

        updateShapeSize(new double[]{
                currShapeToHandle.getX1(),
                Double.parseDouble(startyChoiceBox.getValue()),
                currShapeToHandle.getX2(),
                currShapeToHandle.getY2(),
        });
    }

    public void lineEndXChangedHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Line))) {
            return;
        }

        updateShapeSize(new double[]{
                currShapeToHandle.getX1(),
                currShapeToHandle.getY1(),
                Double.parseDouble(endxChoiceBox.getValue()),
                currShapeToHandle.getY2()});
    }

    public void lineEndYChangedHandle() {
        if (!(isCurrShapeToHandleIsOfType(ShapeEnum.Line))) {
            return;
        }

        updateShapeSize(new double[]{
                currShapeToHandle.getX1(),
                currShapeToHandle.getY1(),
                currShapeToHandle.getX2(),
                Double.parseDouble(endyChoiceBox.getValue())});
    }

    private void initCanvasRelatedVariables() {
        canvasGraphicContext = canvas.getGraphicsContext2D();
        canvasGraphicContext.setFill(Color.WHITE);
        canvasGraphicContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        offScreenCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
    }

    private void setCanvasEvents() {
        setOnMousePressed();
        setOnMouseDragged();
        setOnMouseReleased();
    }

    private void setOnMousePressed() {
        canvas.setOnMousePressed(mouseEvent -> {

            canvas.setCursor(Cursor.CLOSED_HAND);

            setEntityToHandleOrigColor();

            currShapeToHandle = shapeService.findClickedShape(mouseEvent);
            currTextToHandle = textService.findClickedText(mouseEvent);

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
                shapeService.updateDisplayOrigColor(currShapeToHandle, false);
                sendRabbitMessage(RabbitMessageTypeEnum.Shape);
            } else {
                textService.updateDisplayOrigColor(currTextToHandle, false);
                sendRabbitMessage(RabbitMessageTypeEnum.Text);
            }

            canvasGraphicContext.setFill(fillColor.getValue());
        });
    }

    private void setOnMouseDragged() {
        canvas.setOnMouseDragged(mouseEvent -> {
            if (isShapeToHandleNull() && isTextToHandleNull()) {
                return;
            }

            pixDiffCount++;

            if (!(isShapeToHandleNull()) && isTimeToUpdateView()) {
                shapeService.updateShapeLocation(currShapeToHandle, mouseEvent);
                sendRabbitMessage(RabbitMessageTypeEnum.Shape);
            } else if (isTimeToUpdateView()) {
                textService.updateTextLocation(currTextToHandle, mouseEvent);
                sendRabbitMessage(RabbitMessageTypeEnum.Text);
            }
        });
    }

    private void setOnMouseReleased() {
        canvas.setOnMouseReleased(mouseEvent -> canvas.setCursor(Cursor.OPEN_HAND));
    }

    private void setEntityToHandleOrigColor() {
        if (!(isShapeToHandleNull())) {
            shapeService.updateDisplayOrigColor(currShapeToHandle, true);
            sendRabbitMessage(RabbitMessageTypeEnum.Shape);
            currShapeToHandle = null;
        } else if (!(isTextToHandleNull())) {
            textService.updateDisplayOrigColor(currTextToHandle, true);
            sendRabbitMessage(RabbitMessageTypeEnum.Text);
            currTextToHandle = null;
        }

        canvasGraphicContext.setFill(fillColor.getValue());
        canvasGraphicContext.setStroke(strokeColor.getValue());
        canvasGraphicContext.setFont(Font.font(fontChoiceBox.getValue(),
                Double.parseDouble(fontSizeChoiceBox.getValue())));
    }

    private void initFonts() {
        setChoiceBoxSizeAndValue(textService.getFonts(), fontChoiceBox);
        setChoiceBoxSizeAndValue(textService.getFontSizes(), fontSizeChoiceBox);
        canvasGraphicContext.setFont(Font.font(fontChoiceBox.getValue(),
                Double.parseDouble(fontSizeChoiceBox.getValue())));
    }

    private void initShapeSizes() {
        List<String> sizes = shapeService.getSizes();
        setChoiceBoxSizeAndValue(sizes, widthChoiceBox);
        setChoiceBoxSizeAndValue(sizes, heightChoiceBox);
        setChoiceBoxSizeAndValue(sizes, radiusxChoiceBox);
        setChoiceBoxSizeAndValue(sizes, radiusyChoiseBox);
        setChoiceBoxSizeAndValue(sizes, startxChoiceBox);
        setChoiceBoxSizeAndValue(sizes, startyChoiceBox);
        setChoiceBoxSizeAndValue(sizes, endxChoiceBox);
        setChoiceBoxSizeAndValue(sizes, endyChoiceBox);
    }

    private void setChoiceBoxSizeAndValue(List<String> sizes, ChoiceBox<String> choiceBox) {
        choiceBox.getItems().addAll(sizes);
        choiceBox.setValue(choiceBox.getItems().get(0));
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
        boardContentService.init(canvas, offScreenCanvas, messagesList);
        shapeService.init();
        textService.init();
        messageService.init();
        messagesList.scrollTo(messagesList.getItems().size() - 1);
    }

    private void setBoardContent() {
        messageTextField.setText(userSession.getUser().getName() + " join the board!");
        createMessage();
        boardUserConService.addUserToBoard();
        messagesList.getItems().addAll(messageService.getMessages());
        shapeService.addBoardShapes(canvasGraphicContext);
        textService.addBoardTexts(canvasGraphicContext);
        messagesList.scrollTo(messagesList.getItems().size() - 1);
    }

    private boolean isTimeToUpdateView() {
        return pixDiffCount % PIX_DIFFERENCE_TO_UPDATE_VIEW == 0;
    }

    private void sendRabbitMessage(RabbitMessageTypeEnum rabbitMessageTypeEnum) {
        try {
            rabbitSender.sendMessage(rabbitMessageTypeEnum);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isCurrShapeToHandleIsOfType(ShapeEnum shapeType) {
        return !(isShapeToHandleNull()) && currShapeToHandle.getShapeType().equals(shapeType);
    }

    private void updateShapeSize(double[] points) {
        shapeService.updateShapeSize(currShapeToHandle, points);
        sendRabbitMessage(RabbitMessageTypeEnum.Shape);
    }

    private void setOnExitHandle() {
        primaryStage.getPrimaryStage().setOnCloseRequest(event -> {
            event.consume();
            Alert alert = alertService.createConfirmAlert(BEFORE_EXIT_MESSAGE);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    userLeftBoardHandle();
                    primaryStage.getPrimaryStage().close();                }
            });
        });
    }

    private void userLeftBoardHandle() {
        messageTextField.setText(userSession.getUser().getName() + " has left the board!");
        createMessage();
        boardUserConService.removeUserFromBoard();
    }
}
