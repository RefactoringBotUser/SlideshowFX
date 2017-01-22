package com.twasyl.slideshowfx.controls.builder;

import com.twasyl.slideshowfx.controls.builder.nodes.TemplateConfigurationFilePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public class ConfigurationFilePaneTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        final TemplateConfigurationFilePane templateConfigurationFilePane = new TemplateConfigurationFilePane();
        final ScrollPane scrollPane = new ScrollPane(templateConfigurationFilePane);

        final Scene scene = new Scene(scrollPane, 500, 600);
        scene.getStylesheets().addAll(
                ConfigurationFilePaneTest.class.getResource("/com/twasyl/slideshowfx/css/Default.css").toExternalForm(),
                ConfigurationFilePaneTest.class.getResource("/com/twasyl/slideshowfx/css/TemplateBuilder.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
