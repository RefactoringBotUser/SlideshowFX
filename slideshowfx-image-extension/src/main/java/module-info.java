module slideshowfx.image.extension {
    requires slideshowfx.icons;
    requires slideshowfx.content.extension;
    requires slideshowfx.markup;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires org.apache.felix.framework;
    requires slideshowfx.osgi;
    requires javafx.controls;

    opens com.twasyl.slideshowfx.content.extension.image.controllers to javafx.fxml, javafx.graphics;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.image.ImageContentExtension;
}