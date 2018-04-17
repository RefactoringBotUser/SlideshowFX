module slideshowfx.java.executor {
    requires slideshowfx.snippet.executor;
    requires javafx.base;
    requires slideshowfx.global.configuration;
    requires slideshowfx.utils;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.logging;
    requires org.apache.felix.framework;

    provides com.twasyl.slideshowfx.snippet.executor.ISnippetExecutor with com.twasyl.slideshowfx.snippet.executor.java.JavaSnippetExecutor;
}