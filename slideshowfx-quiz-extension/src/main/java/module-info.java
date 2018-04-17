module slideshowfx.quiz.extension {
    requires slideshowfx.content.extension;
    requires java.logging;
    requires javafx.graphics;
    requires javafx.fxml;
    requires slideshowfx.markup;
    requires slideshowfx.server;
    requires slideshowfx.global.configuration;
    requires slideshowfx.icons;
    requires slideshowfx.ui.controls;
    requires javafx.controls;
    requires org.apache.felix.framework;

    opens com.twasyl.slideshowfx.content.extension.quiz.controllers to javafx.fxml, javafx.graphics;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.quiz.QuizContentExtension;
}