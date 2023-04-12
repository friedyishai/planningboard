package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public interface ShapeService {

    void init();

    void createDefaultLine(GraphicsContext graphicsContext2D);

    void createDefaultCircle(GraphicsContext graphicsContext2D);

    void createDefaultRectangle(GraphicsContext graphicsContext2D);

    void createDefaultTriangle(GraphicsContext graphicsContext2D);

    void addBoardShapes(GraphicsContext graphicsContext2D);

    void undo();

    void redo();

    ShapeEntity findClickedShape(MouseEvent mouseEvent);

    void updateShapeLocation(ShapeEntity currShapeToHandle, MouseEvent mouseEvent);

    void updateShapeColor(ShapeEntity currShapeToHandle, GraphicsContext graphicsContext2D);
}
