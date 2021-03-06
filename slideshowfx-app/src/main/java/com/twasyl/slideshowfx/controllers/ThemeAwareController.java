package com.twasyl.slideshowfx.controllers;

import com.twasyl.slideshowfx.global.configuration.GlobalConfiguration;
import com.twasyl.slideshowfx.global.configuration.GlobalConfigurationObserver;
import com.twasyl.slideshowfx.theme.Themes;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public interface ThemeAwareController extends Initializable, GlobalConfigurationObserver {
    Logger LOGGER = Logger.getLogger(ThemeAwareController.class.getName());

    /**
     * Get the root {@link Parent} of the view.
     *
     * @return The root {@link Parent} of this view.
     */
    Parent getRoot();

    void postInitialize(URL location, ResourceBundle resources);

    default void applyTheme(final String theme) {
        Themes.applyTheme(getRoot(), theme);
    }

    @Override
    default void initialize(URL location, ResourceBundle resources) {
        this.applyTheme(GlobalConfiguration.getThemeName());
        GlobalConfiguration.addObserver(this);
        this.postInitialize(location, resources);
    }

    @Override
    default void updateTheme(String oldTheme, String newTheme) {
        applyTheme(newTheme);
    }
}
