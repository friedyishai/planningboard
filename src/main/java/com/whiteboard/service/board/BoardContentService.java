package com.whiteboard.service.board;

import com.whiteboard.enums.RabbitMessageTypeEnum;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;

public interface BoardContentService {

    void init(Canvas canvas, Canvas offScreenCanvas, ListView<String> messagesList);

    void setBoardContent(RabbitMessageTypeEnum rabbitMessageTypeEnum);
}
