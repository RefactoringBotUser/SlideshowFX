module slideshowfx.markdown {
    requires slideshowfx.markup;
    requires txtmark;
    requires org.apache.felix.framework;

    exports com.twasyl.slideshowfx.markup.markdown.activator;

    provides com.twasyl.slideshowfx.markup.IMarkup with com.twasyl.slideshowfx.markup.markdown.MarkdownMarkup;
}