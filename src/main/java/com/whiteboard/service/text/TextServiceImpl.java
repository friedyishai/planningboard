package com.whiteboard.service.text;

import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.enums.FontEnum;
import com.whiteboard.singletons.DisplayedBoard;
import javafx.geometry.Point2D;
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

import static com.whiteboard.constants.AppDefaultValues.*;
import static com.whiteboard.constants.Constants.*;

@RequiredArgsConstructor
@Service
@Lazy
public class TextServiceImpl implements TextService {

    private final TextBoardConService textBoardConService;
    private final TextEntityService textEntityService;
    private final DisplayedBoard displayedBoard;
    private List<TextEntity> boardTexts;
    private final Stack<Integer> undoStack = new Stack<>();
    private final Stack<Integer> redoStack = new Stack<>();

    @Override
    public void init() {
        List<Integer> boardTextIds = textBoardConService.getAllTextIds(displayedBoard.getBoard());
        boardTexts = textEntityService.getAllTexts(boardTextIds);
    }

    @Override
    public List<String> getFonts() {
        return Arrays.stream(FontEnum.values()).map(font -> font.value)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFontSizes() {
        List<String> sizesList = new ArrayList<>();
        double size = MIN_FONT_SIZE;

        while (size <= MAX_FONT_SIZE) {
            sizesList.add(String.valueOf(size));
            size += 2;
        }

        return sizesList;
    }

    @Override
    public void deleteText(TextEntity textEntity) {
        undoStack.remove(textEntity.getId());
        this.textEntityService.remove(textEntity);
    }

    @Override
    public void updateDisplayOrigColor(TextEntity textEntity, boolean displayOrigColor) {
        textEntity.setDisplayOrigColor(displayOrigColor);
        textEntityService.save(textEntity);
    }

    @Override
    public void addBoardTexts(GraphicsContext gc) {
        boardTexts.forEach(textEntity -> addTextToContext(textEntity, gc));
    }

    @Override
    public void undo() {
        if (undoStack.empty()) {
            return;
        }

        int id = undoStack.pop();
        TextEntity textEntity = textEntityService.findById(id);
        textEntityService.remove(textEntity);
        textBoardConService.remove(textEntity);
        redoStack.push(id);
    }

    @Override
    public void redo() {
        if (redoStack.empty()) {
            return;
        }

        int id = redoStack.pop();
        TextEntity textEntity = textEntityService.findById(id);
        textEntity.setIsActive(true);
        textEntityService.save(textEntity);
        textBoardConService.save(textEntity.getId());
        undoStack.push(id);
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
    public void updateTextColor(TextEntity textEntity, GraphicsContext gc) {
        textEntity.setColor(gc.getFill().toString());
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
    public void createText(String text, GraphicsContext gc) {
        TextEntity textEntity = TextEntity.builder()
                .color(gc.getFill().toString())
                .font(gc.getFont().getName())
                .fontSize(gc.getFont().getSize())
                .startX(DEFAULT_X1)
                .startY(DEFAULT_Y1)
                .content(text)
                .displayOrigColor(true)
                .isActive(true)
                .build();

        textEntity = textEntityService.save(textEntity);
        undoStack.push(textEntity.getId());
        boardTexts.add(textEntity);
        textBoardConService.save(textEntity.getId());

        addTextToContext(textEntity, gc);
    }

    private void addTextToContext(TextEntity textEntity, GraphicsContext gc) {
        Paint textColor = textEntity.getDisplayOrigColor() ?
                Paint.valueOf(textEntity.getColor()) :
                Paint.valueOf(SELECTED_ENTITY_COLOR);

        gc.setFont(Font.font(textEntity.getFont(), textEntity.getFontSize()));
        gc.setFill(textColor);
        gc.fillText(textEntity.getContent(), textEntity.getStartX(), textEntity.getStartY());
    }

    private boolean isClickedInTextArea(TextEntity textEntity, MouseEvent mouseEvent) {
        Text text = new Text(textEntity.getContent());
        text.setX(textEntity.getStartX());
        text.setY(textEntity.getStartY());
        text.setFont(Font.font(textEntity.getFont(), textEntity.getFontSize()));

        return text.contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }
}
