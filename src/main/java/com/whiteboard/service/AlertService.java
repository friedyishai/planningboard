package com.whiteboard.service;

import javafx.scene.control.Alert;

public interface AlertService {
    Alert createErrorAlert(String alertContent);
    void displayErrorAlert(String alertContent);
}
