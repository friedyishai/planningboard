package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import org.springframework.stereotype.Service;

import static com.whiteboard.constants.Constants.POINTS_IN_TRIANGLE;

@Service
public class PaintServiceImpl implements PaintService {

    public void createShapeAndAddToContext(ShapeEntity shapeEntity, GraphicsContext graphicsContext) {
        switch (shapeEntity.getShapeType()) {
            case Rectangle -> graphicsContext.fillRect(
                    shapeEntity.getX1(),
                    shapeEntity.getY1(),
                    shapeEntity.getWidth(),
                    shapeEntity.getHeight()
            );
            case Circle -> graphicsContext.fillOval(
                    shapeEntity.getX1(),
                    shapeEntity.getY1(),
                    shapeEntity.getRadiusX(),
                    shapeEntity.getRadiusY()
            );
            case Triangle -> {
                Polygon polygon = new Polygon(
                );
                graphicsContext.fillPolygon(
                        new double[]{
                                shapeEntity.getX1(),
                                shapeEntity.getX2(),
                                shapeEntity.getX3()
                        },
                        new double[]{
                                shapeEntity.getY1(),
                                shapeEntity.getY2(),
                                shapeEntity.getY3()
                        },
                        POINTS_IN_TRIANGLE
                );
            }
            case Line -> graphicsContext.strokeLine(
                    shapeEntity.getStartX(),
                    shapeEntity.getStartY(),
                    shapeEntity.getEndX(),
                    shapeEntity.getEndY()
            );
        }

        setColors(shapeEntity, graphicsContext);
    }

    @Override
    public void addTextToContext(TextEntity textEntity, GraphicsContext graphicsContext) {
        graphicsContext.setFont(Font.font(textEntity.getFont()));
        graphicsContext.setFill(Paint.valueOf(textEntity.getColor()));
        graphicsContext.fillText(textEntity.getColor(), textEntity.getStartX(), textEntity.getStartY());
    }

    private void setColors(ShapeEntity shapeEntity, GraphicsContext graphicsContext) {
        graphicsContext.setFill(Paint.valueOf(shapeEntity.getFillColor()));
        graphicsContext.setStroke(Paint.valueOf(shapeEntity.getFrameColor()));
    }
}