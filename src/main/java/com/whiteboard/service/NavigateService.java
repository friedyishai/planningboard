package com.whiteboard.service;

import javafx.event.ActionEvent;

public interface NavigateService {

    void navigateToScreen(ActionEvent event, String pageToNavigate, Object... argsToNextPage);

    void navigateToLastScreen(ActionEvent event);
}
