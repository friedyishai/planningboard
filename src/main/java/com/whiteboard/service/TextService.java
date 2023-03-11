package com.whiteboard.service;

import javafx.scene.canvas.GraphicsContext;

public interface TextService {

    void createDefaultText(GraphicsContext graphicsContext);

    void addBoardTexts(GraphicsContext graphicsContext2D);

    void undo();

    void redo();
}
