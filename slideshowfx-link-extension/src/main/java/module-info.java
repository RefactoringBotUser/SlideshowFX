module slideshowfx.link.extension {
    requires slideshowfx.content.extension;
    requires slideshowfx.markup;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.logging;
    requires slideshowfx.icons;
    requires slideshowfx.ui.controls;
    requires org.apache.felix.framework;

    opens com.twasyl.slideshowfx.content.extension.link.controllers to javafx.fxml, javafx.graphics;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.link.LinkContentExtension;

}