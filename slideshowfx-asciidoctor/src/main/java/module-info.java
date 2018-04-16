module slideshowfx.asciidoctor {
    requires java.logging;
    requires slideshowfx.markup;
    requires asciidoctorj;
    requires jruby.complete;
    requires org.apache.felix.framework;

    exports com.twasyl.slideshowfx.markup.asciidoctor.activator;

    provides com.twasyl.slideshowfx.markup.IMarkup with com.twasyl.slideshowfx.markup.asciidoctor.AsciidoctorMarkup;
}