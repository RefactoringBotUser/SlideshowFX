module slideshowfx.dropbox.hosting.connector {
    requires slideshowfx.hosting.connector;
    requires java.logging;
    requires dropbox.core.sdk;
    requires slideshowfx.global.configuration;
    requires javafx.graphics;
    requires javafx.controls;
    requires slideshowfx.engines;
    requires javafx.web;
    requires org.apache.felix.framework;

    exports com.twasyl.slideshowfx.hosting.connector.dropbox.activator;

    provides com.twasyl.slideshowfx.hosting.connector.IHostingConnector with com.twasyl.slideshowfx.hosting.connector.dropbox.DropboxHostingConnector;
}