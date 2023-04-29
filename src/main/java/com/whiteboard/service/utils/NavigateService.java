package com.whiteboard.service.utils;

import javafx.event.ActionEvent;

public interface NavigateService {

    void navigateToScreen(ActionEvent event, String pageToNavigate);

    void navigateToLastScreen(ActionEvent event);
}
