package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.GraphicsContext;

public interface PaintService {

    void createShapeAndAddToContext(ShapeEntity shapeEntity, GraphicsContext graphicsContext);

    void addTextToContext(TextEntity textEntity, GraphicsContext graphicsContext);
}
