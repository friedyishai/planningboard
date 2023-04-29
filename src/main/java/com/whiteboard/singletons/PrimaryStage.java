package com.whiteboard.singletons;

import javafx.stage.Stage;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PrimaryStage {
    private Stage primaryStage;
}
