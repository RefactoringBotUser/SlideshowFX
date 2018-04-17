module slideshowfx.box.hosting.connector {
    requires slideshowfx.hosting.connector;
    requires java.logging;
    requires box.java.sdk;
    requires slideshowfx.global.configuration;
    requires slideshowfx.engines;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires org.apache.felix.framework;

    exports com.twasyl.slideshowfx.hosting.connector.box.activator;

    provides com.twasyl.slideshowfx.hosting.connector.IHostingConnector with com.twasyl.slideshowfx.hosting.connector.box.BoxHostingConnector;
}