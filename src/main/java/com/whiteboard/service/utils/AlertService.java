package com.whiteboard.service.utils;

import javafx.scene.control.Alert;

public interface AlertService {

    Alert createErrorAlert(String alertContent);

    void displayErrorAlert(String alertContent);

    Alert createConfirmAlert(String message);
}
