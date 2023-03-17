package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public interface ShapeService {

    void createShapeAndAddToContext(ShapeEntity shapeEntity, GraphicsContext graphicsContext);

    void createDefaultLine(GraphicsContext graphicsContext2D);

    void createDefaultCircle(GraphicsContext graphicsContext2D);

    void createDefaultRectangle(GraphicsContext graphicsContext2D);

    void createDefaultTriangle(GraphicsContext graphicsContext2D);

    void addBoardShapes(GraphicsContext graphicsContext2D);

    void undo(Canvas canvas);

    void redo(Canvas canvas);
}
