package com.twasyl.slideshowfx.hosting.connector.drive.activator;

import com.twasyl.slideshowfx.hosting.connector.IHostingConnector;
import com.twasyl.slideshowfx.hosting.connector.drive.DriveHostingConnector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * Activator class for the connector that allows to interact with Google Drive.
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0
 */
public class DriveHostingConnectorActivator implements BundleActivator {
    private DriveHostingConnector hostingConnector;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.hostingConnector = new DriveHostingConnector();
        Hashtable<String, String> props = new Hashtable<>();

        bundleContext.registerService(IHostingConnector.class.getName(), this.hostingConnector, props);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if(this.hostingConnector.isAuthenticated()) this.hostingConnector.disconnect();
    }
}
