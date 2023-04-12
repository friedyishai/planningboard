package com.whiteboard.service;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.shape.Rectangle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardContentService {

    private final ShapeService shapeService;
    private final TextService textService;
    private final MessageService messageService;

    private Canvas canvas;
    private Canvas offScreenCanvas;

    private ListView<String> messageList;

    public void init(Canvas canvas, Canvas offScreenCanvas, ListView<String> messagesList) {
        this.canvas = canvas;
        this.offScreenCanvas = offScreenCanvas;
        this.messageList = messagesList;
    }

    public void setBoardContent() {
        if (null == this.canvas) {
            return;
        }

        GraphicsContext gc = offScreenCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, offScreenCanvas.getWidth(), offScreenCanvas.getHeight());

        shapeService.init();
        textService.init();
        messageService.init();

        shapeService.addBoardShapes(offScreenCanvas.getGraphicsContext2D());
        textService.addBoardTexts(offScreenCanvas.getGraphicsContext2D());

        Platform.runLater(() -> {
            canvas.getGraphicsContext2D().drawImage(offScreenCanvas.snapshot(null, null), 0, 0);
            messageList.getItems().clear();
            messageList.getItems().addAll(messageService.getMessages());
            messageList.scrollTo(messageList.getItems().size() - 1);
        });
    }
}
