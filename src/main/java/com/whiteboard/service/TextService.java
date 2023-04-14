package com.whiteboard.service;

import com.whiteboard.dao.model.TextEntity;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.util.List;

public interface TextService {

    void init();

    List<String> getFonts();

    void addBoardTexts(GraphicsContext graphicsContext2D);

    void undo();

    void redo();

    TextEntity findClickedText(MouseEvent mouseEvent);

    void updateTextLocation(TextEntity currTextToHandle, MouseEvent mouseEvent);

    void updateTextColor(TextEntity currTextToHandle, GraphicsContext graphicsContext2D);

    void updateTextFont(TextEntity currTextToHandle, Font font);

    void updateText(TextEntity currTextToHandle, String text);

    void createText(String text, GraphicsContext graphicsContext);
}
