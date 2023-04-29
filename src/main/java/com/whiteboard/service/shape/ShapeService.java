package com.whiteboard.service.shape;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.util.List;

public interface ShapeService {

    void init();

    List<String> getSizes();

    void createDefaultLine(GraphicsContext gc);

    void createDefaultCircle(GraphicsContext gc);

    void createDefaultRectangle(GraphicsContext gc);

    void createDefaultTriangle(GraphicsContext gc);

    void addBoardShapes(GraphicsContext gc);

    void undo();

    void redo();

    ShapeEntity findClickedShape(MouseEvent mouseEvent);

    void updateShapeLocation(ShapeEntity shapeEntity, MouseEvent mouseEvent);

    void updateShapeColor(ShapeEntity shapeEntity, GraphicsContext gc, boolean updateEntityColor);

    void deleteShape(ShapeEntity shapeEntity);

    void updateDisplayOrigColor(ShapeEntity shapeEntity, boolean displayOrigColor);

    void updateShapeSize(ShapeEntity shapeEntity, double[] points);
}
