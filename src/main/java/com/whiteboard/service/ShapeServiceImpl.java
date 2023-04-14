package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.enums.ShapeEnum;
import com.whiteboard.util.DisplayedBoard;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Service
@Lazy
public class ShapeServiceImpl implements ShapeService {

    private final ShapeBoardConService shapeBoardConService;
    private final ShapeEntityService shapeEntityService;
    private final DisplayedBoard displayedBoard;

    private List<ShapeEntity> boardShapes;

    private final Stack<ShapeEntity> undoStack = new Stack<>();
    private final Stack<ShapeEntity> redoStack = new Stack<>();

    @Override
    public void init() {
        List<Integer> boardShapesIds = shapeBoardConService.getAllShapeIds(displayedBoard.getBoard());
        boardShapes = shapeEntityService.getAllShapes(boardShapesIds);
    }

    private void createShapeAndAddToContext(ShapeEntity shapeEntity, GraphicsContext graphicsContext) {
        setColors(shapeEntity, graphicsContext);

        switch (shapeEntity.getShapeType()) {
            case Rectangle -> {
                graphicsContext.fillRect(
                        shapeEntity.getX1(),
                        shapeEntity.getY1(),
                        shapeEntity.getWidth(),
                        shapeEntity.getHeight());
                graphicsContext.strokeRect(
                        shapeEntity.getX1(),
                        shapeEntity.getY1(),
                        shapeEntity.getWidth(),
                        shapeEntity.getHeight());
            }
            case Circle -> {
                graphicsContext.fillOval(
                        shapeEntity.getX1(),
                        shapeEntity.getY1(),
                        shapeEntity.getRadiusX(),
                        shapeEntity.getRadiusY());
                graphicsContext.strokeOval(
                        shapeEntity.getX1(),
                        shapeEntity.getY1(),
                        shapeEntity.getRadiusX(),
                        shapeEntity.getRadiusY());
            }
            case Triangle -> {
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
                        POINTS_IN_TRIANGLE);
                graphicsContext.strokePolygon(
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
                        POINTS_IN_TRIANGLE);
            }
            case Line -> graphicsContext.strokeLine(
                    shapeEntity.getX1(),
                    shapeEntity.getY1(),
                    shapeEntity.getX2(),
                    shapeEntity.getY2());
        }
    }

    @Override
    public void createDefaultLine(GraphicsContext graphicsContext) {
        ShapeEntity shapeEntity = ShapeEntity.builder()
                .fillColor(graphicsContext.getFill().toString())
                .strokeColor(graphicsContext.getStroke().toString())
                .x1(DEFAULT_X1)
                .y1(DEFAULT_Y1)
                .x2(DEFAULT_X2)
                .y2(DEFAULT_Y2)
                .shapeType(ShapeEnum.Line)
                .isActive(true)
                .build();

        handleCreateAndSave(graphicsContext, shapeEntity);
    }

    @Override
    public void createDefaultCircle(GraphicsContext graphicsContext) {
        ShapeEntity shapeEntity = ShapeEntity.builder()
                .fillColor(graphicsContext.getFill().toString())
                .strokeColor(graphicsContext.getStroke().toString())
                .x1(DEFAULT_X1)
                .y1(DEFAULT_Y1)
                .radiusX(DEFAULT_RADIUS_X)
                .radiusY(DEFAULT_RADIUS_Y)
                .shapeType(ShapeEnum.Circle)
                .isActive(true)
                .build();

        handleCreateAndSave(graphicsContext, shapeEntity);
    }

    @Override
    public void createDefaultRectangle(GraphicsContext graphicsContext) {
        ShapeEntity shapeEntity = ShapeEntity.builder()
                .fillColor(graphicsContext.getFill().toString())
                .strokeColor(graphicsContext.getStroke().toString())
                .x1(DEFAULT_X1)
                .y1(DEFAULT_Y1)
                .width(DEFAULT_WIDTH)
                .height(DEFAULT_HEIGHT)
                .shapeType(ShapeEnum.Rectangle)
                .isActive(true)
                .build();

        handleCreateAndSave(graphicsContext, shapeEntity);
    }

    @Override
    public void createDefaultTriangle(GraphicsContext graphicsContext) {
        ShapeEntity shapeEntity = ShapeEntity.builder()
                .fillColor(graphicsContext.getFill().toString())
                .strokeColor(graphicsContext.getStroke().toString())
                .x1(DEFAULT_X1)
                .y1(DEFAULT_Y1)
                .x2(DEFAULT_X2)
                .y2(DEFAULT_Y2)
                .x3(DEFAULT_X3)
                .y3(DEFAULT_Y3)
                .shapeType(ShapeEnum.Triangle)
                .isActive(true)
                .build();

        handleCreateAndSave(graphicsContext, shapeEntity);
    }

    @Override
    public void addBoardShapes(GraphicsContext graphicsContext2D) {
        boardShapes.forEach(shapeEntity -> createShapeAndAddToContext(shapeEntity, graphicsContext2D));
    }

    @Override
    public void undo() {
        if (undoStack.empty()) {
            return;
        }

        ShapeEntity shapeEntity = undoStack.pop();
        shapeEntityService.remove(shapeEntity);
        shapeBoardConService.remove(shapeEntity.getId());
        redoStack.push(shapeEntity);
    }

    @Override
    public void redo() {
        if (redoStack.empty()) {
            return;
        }

        ShapeEntity shapeEntity = redoStack.pop();
        shapeEntity.setIsActive(true);
        shapeEntityService.save(shapeEntity);
        shapeBoardConService.save(shapeEntity.getId());
        undoStack.push(shapeEntity);
    }

    @Override
    public ShapeEntity findClickedShape(MouseEvent mouseEvent) {
        List<ShapeEntity> candidates = this.boardShapes.stream().filter(shapeEntity ->
                isClickedInShapeArea(shapeEntity, mouseEvent)).collect(Collectors.toList());
        Optional<ShapeEntity> optionalShapeEntity =
                candidates.stream().max(Comparator.comparing(ShapeEntity::getUpdateDate));
        return optionalShapeEntity.orElse(null);
    }

    @Override
    public void updateShapeLocation(ShapeEntity shapeEntity, MouseEvent mouseEvent) {
        switch (shapeEntity.getShapeType()) {
            case Circle -> {
                double mouseX = mouseEvent.getX();
                double mouseY = mouseEvent.getY();

                // calculate the difference between the mouse position and the center of the circle
                double deltaX = mouseX - shapeEntity.getX1();
                double deltaY = mouseY - shapeEntity.getY1();

                // update the shape entity's coordinates
                shapeEntity.setX1(shapeEntity.getX1() + deltaX);
                shapeEntity.setY1(shapeEntity.getY1() + deltaY);
            }
            case Rectangle -> {
                shapeEntity.setX1(mouseEvent.getX() - shapeEntity.getWidth() / 2);
                shapeEntity.setY1(mouseEvent.getY() - shapeEntity.getHeight() / 2);
            }
            case Triangle -> {
                double mouseX = mouseEvent.getX();
                double mouseY = mouseEvent.getY();

                // calculate the centroid of the triangle
                double centroidX = (shapeEntity.getX1() + shapeEntity.getX2() + shapeEntity.getX3()) / 3;
                double centroidY = (shapeEntity.getY1() + shapeEntity.getY2() + shapeEntity.getY3()) / 3;

                // calculate the new coordinates of the triangle vertices based on the relative position of the mouse to the centroid
                double dx = mouseX - centroidX;
                double dy = mouseY - centroidY;
                double x1 = shapeEntity.getX1() + dx;
                double y1 = shapeEntity.getY1() + dy;
                double x2 = shapeEntity.getX2() + dx;
                double y2 = shapeEntity.getY2() + dy;
                double x3 = shapeEntity.getX3() + dx;
                double y3 = shapeEntity.getY3() + dy;

                // update the shape entity's coordinates
                shapeEntity.setX1(x1);
                shapeEntity.setY1(y1);
                shapeEntity.setX2(x2);
                shapeEntity.setY2(y2);
                shapeEntity.setX3(x3);
                shapeEntity.setY3(y3);
            }
            case Line -> {
                double mouseX = mouseEvent.getX();
                double mouseY = mouseEvent.getY();

                // calculate the difference between the mouse position and the line's starting coordinates
                double deltaX = mouseX - shapeEntity.getX1();
                double deltaY = mouseY - shapeEntity.getY1();

                // update the shape entity's coordinates
                shapeEntity.setX1(mouseX);
                shapeEntity.setY1(mouseY);
                shapeEntity.setX2(shapeEntity.getX2() + deltaX);
                shapeEntity.setY2(shapeEntity.getY2() + deltaY);

            }
        }

        shapeEntityService.save(shapeEntity);
    }

    @Override
    public void updateShapeColor(ShapeEntity shapeEntity, GraphicsContext graphicsContext2D) {
        shapeEntity.setFillColor(graphicsContext2D.getFill().toString());
        shapeEntity.setStrokeColor(graphicsContext2D.getStroke().toString());
        shapeEntityService.save(shapeEntity);
    }

    private void setColors(ShapeEntity shapeEntity, GraphicsContext graphicsContext) {
        Paint fill = (null == shapeEntity.getFillColor()) ?
                graphicsContext.getFill() : Paint.valueOf(shapeEntity.getFillColor());

        Paint stroke = (null == shapeEntity.getStrokeColor()) ?
                graphicsContext.getStroke() : Paint.valueOf(shapeEntity.getStrokeColor());

        shapeEntity.setFillColor(fill.toString());
        shapeEntity.setStrokeColor(stroke.toString());

        shapeEntityService.save(shapeEntity);

        graphicsContext.setFill(fill);
        graphicsContext.setStroke(stroke);
    }

    private void handleCreateAndSave(GraphicsContext graphicsContext, ShapeEntity shapeEntity) {
        ShapeEntity savedShapeEntity = shapeEntityService.save(shapeEntity);
        shapeBoardConService.save(savedShapeEntity.getId());
        undoStack.push(savedShapeEntity);
        boardShapes.add(savedShapeEntity);
        createShapeAndAddToContext(savedShapeEntity, graphicsContext);
    }

    private boolean isClickedInShapeArea(ShapeEntity shapeEntity, MouseEvent mouseEvent) {
        Shape shape = null;

        switch (shapeEntity.getShapeType()) {
            case Circle -> shape = new Ellipse(
                    shapeEntity.getX1(),
                    shapeEntity.getY1(),
                    shapeEntity.getRadiusX(),
                    shapeEntity.getRadiusY()
            );
            case Rectangle -> shape = new Rectangle(
                    shapeEntity.getX1(),
                    shapeEntity.getY1(),
                    shapeEntity.getWidth(),
                    shapeEntity.getHeight()
            );
            case Triangle -> shape = new Polygon(
                    shapeEntity.getX1(), shapeEntity.getY1(),
                    shapeEntity.getX2(), shapeEntity.getY2(),
                    shapeEntity.getX3(), shapeEntity.getY3());
            case Line -> {
                double startX = shapeEntity.getX1();
                double startY = shapeEntity.getY1();
                double endX = shapeEntity.getX2();
                double endY = shapeEntity.getY2();

                double deltaX = endX - startX;
                double deltaY = endY - startY;
                double lineLength = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                // Compute the unit vector along the line
                double unitX = deltaX / lineLength;
                double unitY = deltaY / lineLength;

                // Compute the perpendicular vector with length of 5 pixels
                double perpX = unitY * 5;
                double perpY = -unitX * 5;

                // Create a polygon that includes the line and 5 pixel delta

                shape = new Polygon(
                        startX + perpX, startY + perpY,
                        endX + perpX, endY + perpY,
                        endX - perpX, endY - perpY,
                        startX - perpX, startY - perpY);
            }
        }

        return shape.contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }
}