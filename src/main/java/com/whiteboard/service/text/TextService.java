package com.whiteboard.service.text;

import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.util.List;

public interface TextService {

    void init();

    void addBoardTexts(GraphicsContext gc);

    void undo();

    void redo();

    void updateTextLocation(TextEntity textEntity, MouseEvent mouseEvent);

    void updateTextColor(TextEntity textEntity, GraphicsContext gc);

    void updateTextFont(TextEntity textEntity, Font font);

    void updateText(TextEntity textEntity, String text);

    void createText(String text, GraphicsContext gc);

    TextEntity findClickedText(MouseEvent mouseEvent);

    List<String> getFonts();

    List<String> getFontSizes();

    void deleteText(TextEntity textEntity);

    void updateDisplayOrigColor(TextEntity textEntity, boolean displayOrigColor);
}
