package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.enums.ShapeEnum;
import com.whiteboard.util.DisplayedBoard;
import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;

import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Service
@Lazy
public class ShapeServiceImpl implements ShapeService {

    private final ShapeBoardConService shapeBoardConService;
    private final ShapeEntityService shapeService;
    private final DisplayedBoard displayedBoard;

    private List<ShapeEntity> boardShapes;

    private final Stack<ShapeEntity> undoStack = new Stack<>();
    private final Stack<ShapeEntity> redoStack = new Stack<>();

    @Override
    public void createShapeAndAddToContext(ShapeEntity shapeEntity, GraphicsContext graphicsContext) {
        setColors(shapeEntity, graphicsContext);

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
            case Triangle -> graphicsContext.fillPolygon(
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
            case Line -> graphicsContext.strokeLine(
                    shapeEntity.getX1(),
                    shapeEntity.getY1(),
                    shapeEntity.getX2(),
                    shapeEntity.getY2()
            );
        }
    }

    @Override
    public void createDefaultLine(GraphicsContext graphicsContext) {
        ShapeEntity shapeEntity = ShapeEntity.builder()
                .frameColor(graphicsContext.getFill().toString())
                .fillColor(graphicsContext.getFill().toString())
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
                .frameColor(graphicsContext.getFill().toString())
                .fillColor(graphicsContext.getFill().toString())
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
                .frameColor(graphicsContext.getFill().toString())
                .fillColor(graphicsContext.getFill().toString())
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
                .frameColor(graphicsContext.getFill().toString())
                .fillColor(graphicsContext.getFill().toString())
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
    public void undo(Canvas canvas) {
        if (undoStack.empty()) {
            return;
        }

        ShapeEntity shapeEntity = undoStack.pop();
        shapeService.remove(shapeEntity);
        boardShapes.remove(shapeEntity);
        shapeBoardConService.remove(shapeEntity.getId());
        redoStack.push(shapeEntity);
        canvasRepaint(canvas);
    }

    @Override
    public void redo(Canvas canvas) {
        if (redoStack.empty()) {
            return;
        }

        ShapeEntity shapeEntity = redoStack.pop();
        shapeService.save(shapeEntity);
        boardShapes.add(shapeEntity);
        shapeBoardConService.save(shapeEntity.getId());
        undoStack.push(shapeEntity);
        canvasRepaint(canvas);
    }

    private void setColors(ShapeEntity shapeEntity, GraphicsContext graphicsContext) {
        Paint fill = (null == shapeEntity.getFillColor()) ?
                graphicsContext.getFill() : Paint.valueOf(shapeEntity.getFillColor());

        Paint stroke = (null == shapeEntity.getFrameColor()) ?
                graphicsContext.getStroke() : Paint.valueOf(shapeEntity.getFrameColor());

        shapeEntity.setFillColor(fill.toString());
        shapeEntity.setFrameColor(stroke.toString());

        graphicsContext.setFill(fill);
        graphicsContext.setStroke(stroke);
    }

    @PostConstruct
    private void init() {
        List<Integer> boardShapesIds = shapeBoardConService.getAllShapeIds(displayedBoard.getBoard());
        boardShapes = shapeService.getAllShapes(boardShapesIds);
    }

    private void handleCreateAndSave(GraphicsContext graphicsContext, ShapeEntity shapeEntity) {
        ShapeEntity savedShapeEntity = shapeService.save(shapeEntity);

        shapeBoardConService.save(savedShapeEntity.getId());
        undoStack.push(savedShapeEntity);
        boardShapes.add(savedShapeEntity);

        createShapeAndAddToContext(undoStack.peek(), graphicsContext);
    }

    private void canvasRepaint(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        addBoardShapes(gc);
    }
}