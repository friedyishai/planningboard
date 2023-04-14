package com.whiteboard.constants;

public class Constants {

    public static final String
            INCORRECT_USERNAME_OR_PASSWORD = "The username or password is incorrect.",
            USER_ALREADY_LOG_IN = "The user is already log in, please logout first",
            UNAVAILABLE_USER_NAME = "The username is already taken.",
            UNAVAILABLE_EMAIL = "The email is already taken.",
            ERROR = "error",
            FATAL_ERROR = "There was an error load the application, try again later.",
            OPERATION_FAILED = "The operation failed.",
            UNSELECTED_BOARD_ERROR_MSG = "A planning board must be selected.",
            UNAVAILABLE_BOARD_NAME = "The board name is already taken.",
            BOARD_NAME_IS_REQUIRED = "The board name field is required",
            DEFAULT_COLOR = "#000000",
            DEFAULT_FONT = "Arial",
            DEFAULT_TEXT = "Text",
            SELECTED_ENTITY_COLOR = "#D3D3D3",
            RABBIT_QUEUE = "Plan-Board-Messages",
            RABBIT_EXCHANGE = "Plan-Board-Messages",
            BEFORE_EXIT_MESSAGE = "Are you sure you want to exit?";

    public static final Integer
            POINTS_IN_TRIANGLE = 3,
            PIX_DIFFERENCE_TO_UPDATE_VIEW = 7;

    public static final Double
            DEFAULT_X1 = 50.0,
            DEFAULT_Y1 = 150.0,
            DEFAULT_X2 = 100.0,
            DEFAULT_Y2 = 100.0,
            DEFAULT_X3 = 150.0,
            DEFAULT_Y3 = 150.0,
            DEFAULT_WIDTH = 100.0,
            DEFAULT_HEIGHT = 100.0,
            DEFAULT_RADIUS_X = 50.0,
            DEFAULT_RADIUS_Y = 50.0,
            DEFAULT_STROKE_WIDTH = 4.0,
            DEFAULT_FONT_SIZE = 12.0;

    private Constants() {
    }
}
