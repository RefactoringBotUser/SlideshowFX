module slideshowfx.html {
    requires slideshowfx.markup;
    requires org.apache.felix.framework;
    requires java.logging;

    exports com.twasyl.slideshowfx.markup.html.activator;

    provides com.twasyl.slideshowfx.markup.IMarkup with com.twasyl.slideshowfx.markup.html.HtmlMarkup;
}