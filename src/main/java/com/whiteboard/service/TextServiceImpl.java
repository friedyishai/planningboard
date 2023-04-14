package com.whiteboard.service;

import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.enums.FontEnum;
import com.whiteboard.util.DisplayedBoard;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Override
    public void init() {
        List<Integer> boardTextIds = textBoardConService.getAllTextIds(displayedBoard.getBoard());
        boardTexts = textEntityService.getAllTexts(boardTextIds);
    }

    @Override
    public List<String> getFonts() {
        return Arrays.asList(FontEnum.values()).stream().map(font -> font.value)
                .collect(Collectors.toList());
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
        textBoardConService.remove(textEntity);
        boardTexts.remove(textEntity);
        redoStack.push(textEntity);
    }

    @Override
    public void redo() {
        if (redoStack.empty()) {
            return;
        }

        TextEntity textEntity = redoStack.pop();
        textEntityService.save(textEntity);
        textBoardConService.save(textEntity.getId());
        boardTexts.add(textEntity);
        undoStack.push(textEntity);
    }

    @Override
    public TextEntity findClickedText(MouseEvent mouseEvent) {
        List<TextEntity> candidates = boardTexts.stream().filter(textEntity ->
                isClickedInTextArea(textEntity, mouseEvent)).collect(Collectors.toList());
        Optional<TextEntity> optionalTextEntity = candidates.stream().max(
                Comparator.comparing(TextEntity::getUpdateDate));
        return optionalTextEntity.orElse(null);
    }

    @Override
    public void updateTextLocation(TextEntity textEntity, MouseEvent mouseEvent) {
        textEntity.setStartX(mouseEvent.getX());
        textEntity.setStartY(mouseEvent.getY());
        textEntityService.save(textEntity);
    }

    @Override
    public void updateTextColor(TextEntity textEntity, GraphicsContext graphicsContext2D) {
        textEntity.setColor(graphicsContext2D.getFill().toString());
        textEntityService.save(textEntity);
    }

    @Override
    public void updateTextFont(TextEntity textEntity, Font font) {
        textEntity.setFont(font.getName());
        textEntity.setFontSize(font.getSize());
        textEntityService.save(textEntity);
    }

    @Override
    public void updateText(TextEntity textEntity, String text) {
        textEntity.setContent(text);
        textEntityService.save(textEntity);
    }

    @Override
    public void createText(String text, GraphicsContext graphicsContext) {
        TextEntity textEntity = TextEntity.builder()
                .color(graphicsContext.getFill().toString())
                .font(graphicsContext.getFont().getName())
                .fontSize(DEFAULT_FONT_SIZE)
                .startX(DEFAULT_X1)
                .startY(DEFAULT_Y1)
                .content(text)
                .isActive(true)
                .build();

        textEntity = textEntityService.save(textEntity);
        undoStack.push(textEntity);
        boardTexts.add(textEntity);
        textBoardConService.save(textEntity.getId());

        addTextToContext(textEntity, graphicsContext);
    }

    private void addTextToContext(TextEntity textEntity, GraphicsContext graphicsContext) {
        graphicsContext.setFont(Font.font(textEntity.getFont(), textEntity.getFontSize()));
        graphicsContext.setFill(Paint.valueOf(textEntity.getColor()));
        graphicsContext.fillText(textEntity.getContent(), textEntity.getStartX(), textEntity.getStartY());
    }

    private boolean isClickedInTextArea(TextEntity textEntity, MouseEvent mouseEvent) {
        Text text = new Text(textEntity.getContent());
        text.setX(textEntity.getStartX());
        text.setY(textEntity.getStartY());
        text.setFont(Font.font(textEntity.getFont(), textEntity.getFontSize()));

        return text.contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }
}
