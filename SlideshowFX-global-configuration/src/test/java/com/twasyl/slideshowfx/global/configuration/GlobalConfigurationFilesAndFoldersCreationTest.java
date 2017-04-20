package com.twasyl.slideshowfx.global.configuration;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static com.twasyl.slideshowfx.global.configuration.GlobalConfiguration.APPLICATION_DIRECTORY_PROPERTY;
import static org.junit.Assert.assertTrue;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public class GlobalConfigurationFilesAndFoldersCreationTest {

    private static File tmpFolder;

    @BeforeClass
    public static void setUp() {
        final File buildFolder = new File("build");
        if (!buildFolder.exists()) {
            buildFolder.mkdirs();
        }

        tmpFolder = new File(buildFolder, "testsTmp");

        System.setProperty(APPLICATION_DIRECTORY_PROPERTY, tmpFolder.getAbsolutePath());
    }

    @After
    public void after() {
        if (GlobalConfiguration.getConfigurationFile().exists()) {
            GlobalConfiguration.getConfigurationFile().delete();
        }

        if (GlobalConfiguration.getLoggingConfigFile().exists()) {
            GlobalConfiguration.getLoggingConfigFile().delete();
        }

        if (GlobalConfiguration.getPluginsDirectory().exists()) {
            GlobalConfiguration.getPluginsDirectory().delete();
        }

        if (GlobalConfiguration.getApplicationDirectory().exists()) {
            GlobalConfiguration.getApplicationDirectory().delete();
        }
    }

    @Test
    public void createApplicationDirectory() {
        assertTrue(GlobalConfiguration.createApplicationDirectory());
        assertTrue(GlobalConfiguration.getApplicationDirectory().exists());
    }

    @Test
    public void createPluginsDirectory() {
        GlobalConfiguration.createApplicationDirectory();
        assertTrue(GlobalConfiguration.createPluginsDirectory());
        assertTrue(GlobalConfiguration.getPluginsDirectory().exists());
    }

    @Test
    public void createConfigurationFile() {
        GlobalConfiguration.createApplicationDirectory();
        assertTrue(GlobalConfiguration.createConfigurationFile());
        assertTrue(GlobalConfiguration.getConfigurationFile().exists());
    }

    @Test
    public void createLoggingConfigurationFile() {
        GlobalConfiguration.createApplicationDirectory();
        assertTrue(GlobalConfiguration.createLoggingConfigurationFile());
        assertTrue(GlobalConfiguration.getLoggingConfigFile().exists());
    }
}
