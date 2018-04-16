module slideshowfx.hosting.connector {
    exports com.twasyl.slideshowfx.hosting.connector;
    exports com.twasyl.slideshowfx.hosting.connector.exceptions;
    exports com.twasyl.slideshowfx.hosting.connector.io;
    requires slideshowfx.plugin;
    requires slideshowfx.engines;
    requires slideshowfx.utils;
    requires javafx.controls;
    requires java.logging;
}