package com.twasyl.slideshowfx.content.extension.chart;


import com.twasyl.slideshowfx.content.extension.AbstractContentExtension;
import com.twasyl.slideshowfx.content.extension.chart.controllers.ChartContentExtensionController;
import com.twasyl.slideshowfx.markup.IMarkup;
import com.twasyl.slideshowfx.utils.ResourceHelper;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChartContentExtension extends AbstractContentExtension {
    private static final Logger LOGGER = Logger.getLogger(ChartContentExtension.class.getName());

    private ChartContentExtensionController controller;

    public ChartContentExtension() {
        super("CHART",
                ResourceHelper.getURL("/com/twasyl/slideshowfx/content/extension/chart/resources/amcharts.zip"),
                FontAwesomeIcon.BAR_CHART,
                "Insert a chart",
                "Insert a chart");


        final String baseURL = "amcharts/3.15.1/";

        // Add URL
        /*this.putResource(ResourceType.CSS_FILE, baseURL.concat("styles/github.css"));
        this.putResource(ResourceType.JAVASCRIPT_FILE, baseURL.concat("highlight.pack.js"));
        this.putResource(ResourceType.SCRIPT, "hljs.initHighlightingOnLoad();");*/
    }

    @Override
    public Pane getUI() {
        FXMLLoader loader = new FXMLLoader(ResourceHelper.getURL(getClass().getClassLoader(), "/com/twasyl/slideshowfx/content/extension/chart/fxml/ChartContentExtension.fxml"));
        Pane root = null;

        try {
            loader.setClassLoader(getClass().getClassLoader());
            root = loader.load();
            this.controller = loader.getController();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not load UI for ChartContentExtension", e);
        }

        return root;
    }

    @Override
    public String buildContentString(IMarkup markup) {
        final StringBuilder builder = new StringBuilder();

        if(markup == null || "HTML".equals(markup.getCode())) {
            builder.append(this.buildDefaultContentString());
        } else if("TEXTILE".equals(markup.getCode())) {

        } else {
            builder.append(this.buildDefaultContentString());
        }

        return builder.toString();
    }

    @Override
    public String buildDefaultContentString() {

        final StringBuilder builder = new StringBuilder();


        return builder.toString();
    }
}
