package com.whiteboard.service;

import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.general.DisplayedBoard;
import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;

import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Service
public class TextServiceImpl implements TextService {

    private final TextBoardConService textBoardConService;
    private final TextEntityService textEntityService;
    private final DisplayedBoard displayedBoard;

    private List<TextEntity> boardTexts;

    private final Stack<TextEntity> undoStack = new Stack<>();
    private final Stack<TextEntity> redoStack = new Stack<>();

    public List<TextEntity> getBoardTexts() {
        return boardTexts;
    }

    public void createDefaultText(GraphicsContext graphicsContext) {
        TextEntity textEntity = TextEntity.builder()
                .color(graphicsContext.getFill().toString())
                .font(graphicsContext.getFont().toString())
                .startX(DEFAULT_X1)
                .startY(DEFAULT_Y1)
                .build();

        textEntity = textEntityService.save(textEntity);
        undoStack.push(textEntity);
        boardTexts.add(textEntity);

        addTextToContext(textEntity, graphicsContext);
    }

    @Override
    public void addBoardTexts(GraphicsContext graphicsContext2D) {
        boardTexts.forEach(textEntity -> addTextToContext(textEntity, graphicsContext2D));
    }

    @Override
    public void undo() {
        if (undoStack.empty()) {
            return;
        }

        TextEntity textEntity = undoStack.pop();
        textEntityService.remove(textEntity);
        boardTexts.remove(textEntity);
        redoStack.push(textEntity);
    }

    @Override
    public void redo() {
        if (redoStack.empty()) {
            return;
        }

        TextEntity textEntity = undoStack.pop();
        textEntity.setIsActive(true);
        textEntityService.save(textEntity);
        boardTexts.remove(textEntity);
        undoStack.push(textEntity);
    }

    public void addTextToContext(TextEntity textEntity, GraphicsContext graphicsContext) {
        graphicsContext.setFont(Font.font(textEntity.getFont()));
        graphicsContext.setFill(Paint.valueOf(textEntity.getColor()));
        graphicsContext.fillText(textEntity.getColor(), textEntity.getStartX(), textEntity.getStartY());
    }

    @PostConstruct
    private void init() {
        List<Integer> boardTextIds = textBoardConService.getAllTextIds(displayedBoard.getBoard());
        boardTexts = textEntityService.getAllTexts(boardTextIds);
    }
}
