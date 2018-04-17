module slideshowfx.javascript.executor {
    requires slideshowfx.snippet.executor;
    requires javafx.base;
    requires javafx.graphics;
    requires java.scripting;
    requires java.logging;
    requires org.apache.felix.framework;

    provides com.twasyl.slideshowfx.snippet.executor.ISnippetExecutor with com.twasyl.slideshowfx.snippet.executor.javascript.JavaScriptSnippetExecutor;
}