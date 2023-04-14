package com.whiteboard.service;

import com.whiteboard.constants.Constants;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl implements AlertService {

    @Override
    public Alert createErrorAlert(String alertContent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(Constants.ERROR);
        alert.setHeaderText(Constants.OPERATION_FAILED);
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
