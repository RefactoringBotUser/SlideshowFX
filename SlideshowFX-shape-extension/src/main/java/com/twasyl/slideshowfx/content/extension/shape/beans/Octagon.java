package com.twasyl.slideshowfx.content.extension.shape.beans;

import com.twasyl.slideshowfx.ui.controls.ExtendedTextField;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.twasyl.slideshowfx.ui.controls.validators.Validators.isInteger;
import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

/**
 * An implementation allowing to create octagons.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public class Octagon extends AbstractShape {
    private ExtendedTextField x;
    private ExtendedTextField y;
    private ExtendedTextField diameter;

    public Octagon() {
        this.x = new ExtendedTextField("X", true, 3);
        this.x.setValidator(isInteger());

        this.y = new ExtendedTextField("Y", true, 3);
        this.y.setValidator(isInteger());

        this.diameter = new ExtendedTextField("Diameter", true, 3);
        this.diameter.setValidator(isInteger());
    }

    @Override
    public Node getUI() {
        final HBox container = new HBox(10);
        container.getChildren().addAll(this.x, this.y, this.diameter);

        final FlowPane attributes = new FlowPane(10, 10);
        attributes.getChildren().addAll(this.getCommonAttributes());

        return new VBox(10, container, attributes);
    }

    @Override
    public String buildCreatingInstruction(String paper) {
        final int x = parseInt(this.x.getText());
        final int y = parseInt(this.y.getText());
        final int diameter = parseInt(this.diameter.getText());
        final double radius = diameter / 2;
        final double centerX = x + radius;
        final double centerY = y + radius;
        final double angleRadians = toRadians(45);
        final double cosine = cos(angleRadians);
        final double sine = sin(angleRadians);

        final double x1 = centerX;
        final double y1 = y;
        final double x2 = centerX + radius * cosine;
        final double y2 = centerY - radius * sine;
        final double x3 = x + diameter;
        final double y3 = y + radius;
        final double x4 = x2;
        final double y4 = centerY + radius * sine;
        final double x5 = centerX;
        final double y5 = y + diameter;
        final double x6 = centerX - radius * cosine;
        final double y6 = y4;
        final double x7 = x;
        final double y7 = y3;
        final double x8 = centerX - radius * cosine;
        final double y8 = y2;

        final StringBuilder builder = new StringBuilder(paper)
                .append(".polyline([")
                .append(x1).append(", ").append(y1).append(", ")
                .append(x2).append(", ").append(y2).append(", ")
                .append(x3).append(", ").append(y3).append(", ")
                .append(x4).append(", ").append(y4).append(", ")
                .append(x5).append(", ").append(y5).append(", ")
                .append(x6).append(", ").append(y6).append(", ")
                .append(x7).append(", ").append(y7).append(", ")
                .append(x8).append(", ").append(y8).append(", ")
                .append(x1).append(", ").append(y1).append("])")
                .append(this.buildAttributesInstruction())
                .append(this.buildDragInstruction())
                .append(";");

        return builder.toString();
    }
}
