package com.twasyl.slideshowfx.content.extension.chart.controllers;

import com.twasyl.slideshowfx.utils.beans.Pair;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is the controller used by the {@code ChartContentExtension.fxml} file.
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0.0
 */
public class ChartContentExtensionController implements Initializable {
    public static final String CHART_TYPE_SERIAL = "serial";
    public static final String CHART_TYPE_PIE = "pie";
    public static final String CHART_TYPE_XY = "xy";
    public static final String CHART_TYPE_RADAR = "radar";
    public static final String CHART_TYPE_FUNNEL = "funnel";
    public static final String CHART_TYPE_GAUGE = "gauge";
    public static final String CHART_TYPE_MAP = "map";
    public static final String CHART_TYPE_STOCK = "stock";

    public static final String CHART_THEME_CHALK = "chalk";

    @FXML private ChoiceBox<String> chartType;
    @FXML private ChoiceBox<String> chartStyle;
    @FXML private VBox dataContainer;

    private List<Pair<String, Double>> data = new ArrayList<>();

    @FXML private void addNewData(final ActionEvent event) {
        this.addData();
    }

    /**
     * Get the type of chart to generate that has been chosen by the user.
     * @return The type of chart to create.
     */
    public String getChartType() { return this.chartType.getValue(); }

    /**
     * Get the style of the chart to generate chosen by the user.
     * @return The style of chart to create.
     */
    public String getChartStyle() { return this.chartStyle.getValue(); }

    /**
     * Return the data the chart will contain. Typically it is the data for the {@code dataProvider} of amcharts.
     * @return Te list containing the data the user decides to have in the chart.
     */
    public List<Pair<String, Double>> getData() { return this.data; }

    /**
     * Add a {@link Pair} to {@link #data} and bind its values to {@link javafx.scene.control.TextField}.
     * The created {@link javafx.scene.control.TextField} are added to the container {@link #dataContainer}.
     */
    private void addData() {
        final Pair<String, Double> pair = new Pair<>();
        this.data.add(pair);

        final HBox line = new HBox(5);

        final TextField categoryName = new TextField();
        categoryName.setPromptText("Category name");
        categoryName.setTooltip(new Tooltip("The name of the category, i.e. the value on the X axis"));
        categoryName.setPrefColumnCount(10);
        categoryName.textProperty().bindBidirectional(pair.keyProperty());

        final TextField value = new TextField();
        value.setPromptText("Value");
        value.setTooltip(new Tooltip("The value, i.e. the value on the Y axis"));
        value.setPrefColumnCount(5);
        value.textProperty().bindBidirectional(pair.valueProperty(), new DoubleStringConverter());

        final FontAwesomeIconView deleteDataIcon = new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE);
        deleteDataIcon.setGlyphSize(20);
        deleteDataIcon.setGlyphStyle("-fx-fill: app-color-orange");

        final Button removeDataButton = new Button();
        removeDataButton.setTooltip(new Tooltip("Delete this value"));
        removeDataButton.setGraphic(deleteDataIcon);
        removeDataButton.setOnAction(event -> {
            this.dataContainer.getChildren().remove(line);
        });

        line.getChildren().addAll(categoryName, value, removeDataButton);

        this.dataContainer.getChildren().add(line);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
