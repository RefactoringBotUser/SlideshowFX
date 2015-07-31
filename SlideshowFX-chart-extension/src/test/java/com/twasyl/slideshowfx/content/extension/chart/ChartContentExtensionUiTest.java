package com.twasyl.slideshowfx.content.extension.chart;

import com.twasyl.slideshowfx.utils.ResourceHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChartContentExtensionUiTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(ResourceHelper.getURL("/com/twasyl/slideshowfx/content/extension/chart/fxml/ChartContentExtension.fxml"));

        final Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
