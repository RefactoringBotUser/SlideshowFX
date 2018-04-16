module slideshowfx.osgi {
    exports com.twasyl.slideshowfx.osgi;
    requires java.logging;
    requires org.apache.felix.framework;
    requires slideshowfx.global.configuration;
    requires slideshowfx.plugin;
    requires slideshowfx.engines;

    uses org.osgi.framework.launch.FrameworkFactory;
}