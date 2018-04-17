module slideshowfx.drive.hosting.connector {
    requires google.oauth.client;
    requires google.api.client;
    requires google.http.client;
    requires google.http.client.jackson2;
    requires google.api.services.drive.v3.rev103;
    requires slideshowfx.engines;
    requires slideshowfx.global.configuration;
    requires slideshowfx.hosting.connector;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires java.logging;
    requires org.apache.felix.framework;

    exports com.twasyl.slideshowfx.hosting.connector.drive.activator;

    provides com.twasyl.slideshowfx.hosting.connector.IHostingConnector with com.twasyl.slideshowfx.hosting.connector.drive.DriveHostingConnector;
}