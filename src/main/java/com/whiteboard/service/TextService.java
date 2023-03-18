package com.whiteboard.service;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public interface TextService {

    List<String> getFonts();

    void createDefaultText(GraphicsContext graphicsContext);

    void addBoardTexts(GraphicsContext graphicsContext2D);

    void undo(Canvas canvas);

    void redo(Canvas canvas);
}
