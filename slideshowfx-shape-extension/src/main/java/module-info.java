module slideshowfx.shape.extension {
    requires slideshowfx.content.extension;
    requires slideshowfx.markup;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires slideshowfx.icons;
    requires org.apache.felix.framework;
    requires slideshowfx.ui.controls;
    requires javafx.controls;
    requires slideshowfx.utils;

    opens com.twasyl.slideshowfx.content.extension.shape.controllers to javafx.fxml, javafx.graphics;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.shape.ShapeContentExtension;
}