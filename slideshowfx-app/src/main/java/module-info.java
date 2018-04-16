module slideshowfx.app {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.web;
    requires java.logging;
    requires slideshowfx.plugin;
    requires slideshowfx.osgi;
    requires slideshowfx.markup;
    requires slideshowfx.content.extension;
    requires slideshowfx.snippet.executor;
    requires slideshowfx.hosting.connector;
    requires slideshowfx.utils;
    requires slideshowfx.engines;
    requires slideshowfx.global.configuration;
    requires slideshowfx.icons;
    requires slideshowfx.server;
    requires java.desktop;
    requires slideshowfx.logs;
    requires slideshowfx.ui.controls;
    requires org.apache.felix.framework;
    requires vertx.core;
    requires io.reactivex.rxjava2;
    requires jdk.jsobject;

    opens com.twasyl.slideshowfx.css;
    opens com.twasyl.slideshowfx.images;

    opens com.twasyl.slideshowfx.app to javafx.graphics, javafx.fxml;
    opens com.twasyl.slideshowfx.controllers /*to javafx.graphics, javafx.fxml*/;
    opens com.twasyl.slideshowfx.controls to javafx.fxml;
    opens com.twasyl.slideshowfx.controls.notification to javafx.fxml;


    // TODO
    /*requires core;
    requires javase;*/
}