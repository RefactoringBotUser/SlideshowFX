module slideshowfx.textile {
    requires slideshowfx.markup;
    requires slideshowfx.plugin;
    requires java.logging;
    requires org.eclipse.mylyn.wikitext;
    requires org.eclipse.mylyn.wikitext.textile;
    requires org.apache.felix.framework;

    exports com.twasyl.slideshowfx.markup.textile.activator;

    provides com.twasyl.slideshowfx.markup.IMarkup with com.twasyl.slideshowfx.markup.textile.TextileMarkup;
}