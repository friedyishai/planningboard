package com.whiteboard.service.utils;

import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;

import static com.whiteboard.constants.AppMessages.ERROR;
import static com.whiteboard.constants.AppMessages.OPERATION_FAILED;

@Service
public class AlertServiceImpl implements AlertService {

    @Override
    public Alert createErrorAlert(String alertContent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(ERROR);
        alert.setHeaderText(OPERATION_FAILED);
        alert.setContentText(alertContent);

        return alert;
    }

    @Override
    public void displayErrorAlert(String alertContent) {
        Alert alert = createErrorAlert(alertContent);
        alert.show();
    }

    @Override
    public Alert createConfirmAlert(String message) {
        return new Alert(Alert.AlertType.CONFIRMATION, message);
    }
}
