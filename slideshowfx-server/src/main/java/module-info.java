module slideshowfx.server {
    requires javafx.base;
    requires vertx.core;
    requires slideshowfx.global.configuration;
    requires javafx.graphics;
    requires javafx.web;
    requires java.xml;
    requires java.logging;
    requires jdk.xml.dom;
    requires slideshowfx.utils;
    requires freemarker;
    requires vertx.web;
    requires slideshowfx.icons;
    exports com.twasyl.slideshowfx.server;
    exports com.twasyl.slideshowfx.server.service;
    exports com.twasyl.slideshowfx.server.bus;
    exports com.twasyl.slideshowfx.server.beans.quiz;
    exports com.twasyl.slideshowfx.server.beans.chat;
}