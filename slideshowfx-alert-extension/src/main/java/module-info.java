module slideshowfx.alert.extension {
    requires slideshowfx.content.extension;
    requires java.logging;
    requires javafx.graphics;
    requires slideshowfx.markup;
    requires javafx.fxml;
    requires slideshowfx.icons;
    requires org.apache.felix.framework;
    requires slideshowfx.ui.controls;
    requires javafx.controls;

    opens com.twasyl.slideshowfx.content.extension.alert.resources;
    opens com.twasyl.slideshowfx.content.extension.alert.controllers to javafx.graphics, javafx.fxml;

    exports com.twasyl.slideshowfx.content.extension.alert.controllers;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.alert.AlertContentExtension;
}