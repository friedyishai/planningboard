package com.whiteboard.service.board;

import com.whiteboard.enums.RabbitMessageTypeEnum;
import com.whiteboard.service.message.MessageService;
import com.whiteboard.service.shape.ShapeService;
import com.whiteboard.service.text.TextService;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardContentServiceImpl implements BoardContentService {

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

    public void setBoardContent(RabbitMessageTypeEnum rabbitMessageTypeEnum) {

        if (null == this.canvas) {
            return;
        }

        if (rabbitMessageTypeEnum.equals(RabbitMessageTypeEnum.Message)) {
            messageService.init();
            Platform.runLater(() -> {
                messageList.getItems().clear();
                messageList.getItems().addAll(messageService.getMessages());
                messageList.scrollTo(messageList.getItems().size() - 1);
            });
        } else if (rabbitMessageTypeEnum.equals(RabbitMessageTypeEnum.Shape)) {
            GraphicsContext gc = offScreenCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, offScreenCanvas.getWidth(), offScreenCanvas.getHeight());

            shapeService.init();

            shapeService.addBoardShapes(offScreenCanvas.getGraphicsContext2D());
            textService.addBoardTexts(offScreenCanvas.getGraphicsContext2D());

            Platform.runLater(() -> {
                canvas.getGraphicsContext2D().drawImage(offScreenCanvas.snapshot(null, null), 0, 0);
            });
        } else {
            GraphicsContext gc = offScreenCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, offScreenCanvas.getWidth(), offScreenCanvas.getHeight());

            textService.init();

            shapeService.addBoardShapes(offScreenCanvas.getGraphicsContext2D());
            textService.addBoardTexts(offScreenCanvas.getGraphicsContext2D());

            Platform.runLater(() -> {
                canvas.getGraphicsContext2D().drawImage(offScreenCanvas.snapshot(null, null), 0, 0);
            });
        }
    }
}
