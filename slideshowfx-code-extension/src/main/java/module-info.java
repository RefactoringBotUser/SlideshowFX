module slideshowfx.code.extension {
    requires slideshowfx.content.extension;
    requires java.logging;
    requires slideshowfx.markup;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires slideshowfx.icons;
    requires org.apache.felix.framework;
    requires javafx.controls;
    requires slideshowfx.ui.controls;

    opens com.twasyl.slideshowfx.content.extension.code.controllers to javafx.graphics, javafx.fxml;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.code.CodeContentExtension;
}