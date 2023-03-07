package com.whiteboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class SpringbootWhiteboardApplication extends Application {

    private ConfigurableApplicationContext context;
    private Parent rootNode;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void init() throws Exception {
        context = SpringApplication.run(SpringbootWhiteboardApplication.class);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/welcome.fxml"));
        loader.setControllerFactory(context::getBean);
        rootNode = loader.load();
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(rootNode, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}
