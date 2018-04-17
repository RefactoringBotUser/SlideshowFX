module slideshowfx.groovy.executor {
    requires slideshowfx.global.configuration;
    requires slideshowfx.snippet.executor;
    requires slideshowfx.utils;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.logging;
    requires org.apache.felix.framework;

    provides com.twasyl.slideshowfx.snippet.executor.ISnippetExecutor with com.twasyl.slideshowfx.snippet.executor.groovy.GroovySnippetExecutor;
}