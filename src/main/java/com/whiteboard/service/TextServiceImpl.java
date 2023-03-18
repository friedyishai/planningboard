package com.whiteboard.service;

import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.enums.FontEnum;
import com.whiteboard.util.DisplayedBoard;
import jakarta.annotation.PostConstruct;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Service
@Lazy
public class TextServiceImpl implements TextService {

    private final TextBoardConService textBoardConService;
    private final TextEntityService textEntityService;
    private final DisplayedBoard displayedBoard;
    private List<TextEntity> boardTexts;
    private final Stack<TextEntity> undoStack = new Stack<>();
    private final Stack<TextEntity> redoStack = new Stack<>();

    public List<String> getFonts() {
        return Arrays.asList(FontEnum.values()).stream().map(font -> font.value)
                .collect(Collectors.toList());
    }

    public void createDefaultText(GraphicsContext graphicsContext) {
        TextEntity textEntity = TextEntity.builder()
                .color(graphicsContext.getFill().toString())
                .font(graphicsContext.getFont().getName())
                .fontSize(DEFAULT_FONT_SIZE)
                .startX(DEFAULT_X1)
                .startY(DEFAULT_Y1)
                .content(DEFAULT_TEXT)
                .isActive(true)
                .build();

        textEntity = textEntityService.save(textEntity);
        undoStack.push(textEntity);
        boardTexts.add(textEntity);
        textBoardConService.save(textEntity.getId());

        addTextToContext(textEntity, graphicsContext);
    }

    @Override
    public void addBoardTexts(GraphicsContext graphicsContext2D) {
        boardTexts.forEach(textEntity -> addTextToContext(textEntity, graphicsContext2D));
    }

    @Override
    public void undo(Canvas canvas) {
        if (undoStack.empty()) {
            return;
        }

        TextEntity textEntity = undoStack.pop();
        textEntityService.remove(textEntity);
        textBoardConService.remove(textEntity);
        boardTexts.remove(textEntity);
        redoStack.push(textEntity);
    }

    @Override
    public void redo(Canvas canvas) {
        if (redoStack.empty()) {
            return;
        }

        TextEntity textEntity = redoStack.pop();
        textEntityService.save(textEntity);
        textBoardConService.save(textEntity.getId());
        boardTexts.add(textEntity);
        undoStack.push(textEntity);
    }

    public void addTextToContext(TextEntity textEntity, GraphicsContext graphicsContext) {
        graphicsContext.setFont(Font.font(textEntity.getFont()));
        graphicsContext.setFill(Paint.valueOf(textEntity.getColor()));
        graphicsContext.fillText(textEntity.getContent(), textEntity.getStartX(), textEntity.getStartY());
    }

    @PostConstruct
    private void init() {
        List<Integer> boardTextIds = textBoardConService.getAllTextIds(displayedBoard.getBoard());
        boardTexts = textEntityService.getAllTexts(boardTextIds);
    }
}
