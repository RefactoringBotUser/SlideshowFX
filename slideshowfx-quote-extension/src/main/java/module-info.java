module slideshowfx.quote.extension {
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

    opens com.twasyl.slideshowfx.content.extension.quote.controllers to javafx.fxml, javafx.graphics;

    provides com.twasyl.slideshowfx.content.extension.IContentExtension with com.twasyl.slideshowfx.content.extension.quote.QuoteContentExtension;
}