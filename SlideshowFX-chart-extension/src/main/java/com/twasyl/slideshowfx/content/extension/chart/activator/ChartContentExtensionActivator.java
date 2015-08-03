package com.twasyl.slideshowfx.content.extension.chart.activator;

import com.twasyl.slideshowfx.content.extension.IContentExtension;
import com.twasyl.slideshowfx.content.extension.chart.ChartContentExtension;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * Activator class for the content extension that allows to insert charts easily inside a SlideshowFX presentation.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0.0
 */
public class ChartContentExtensionActivator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        Hashtable<String, String> props = new Hashtable<>();

        bundleContext.registerService(IContentExtension.class.getName(), new ChartContentExtension(), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
