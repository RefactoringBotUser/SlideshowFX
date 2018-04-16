module slideshowfx.engines {
    requires slideshowfx.utils;
    requires vertx.core;
    requires java.logging;
    requires slideshowfx.global.configuration;
    requires java.desktop;
    requires javafx.graphics;
    requires freemarker;
    requires slideshowfx.content.extension;
    requires javafx.swing;
    requires jsoup;
    exports com.twasyl.slideshowfx.engine.presentation;
    exports com.twasyl.slideshowfx.engine.template.configuration;
    exports com.twasyl.slideshowfx.engine.presentation.configuration;
    exports com.twasyl.slideshowfx.engine.template;

    opens com.twasyl.slideshowfx.engine.js;
    opens com.twasyl.slideshowfx.engine to javafx.base;
}