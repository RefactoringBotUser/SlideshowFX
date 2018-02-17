package com.twasyl.slideshowfx.icons;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class defining FontAwesome icons.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 2.0
 */
public class FontAwesome extends Text {
    private static final Logger LOGGER = Logger.getLogger(FontAwesome.class.getName());
    private final static String FONT_PATH = "/com/twasyl/slideshowfx/icons/fonts/fontawesome-webfont-4.7.0.ttf";
    public static final Font FONT;
    public static final String FONT_AWESOME_FAMILY = "FontAwesome";

    static {
        try {
            FONT = Font.loadFont(FontAwesome.class.getResource(FONT_PATH).openStream(), 10.0d);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final ObjectProperty<Icon> icon = new SimpleObjectProperty<>(Icon.FOLDER_OPEN);
    private final StringProperty size = new SimpleStringProperty("1em");
    private final StringProperty color = new SimpleStringProperty("white");

    public FontAwesome() {
        this.setFont(FONT);
        this.definePropertyListeners();
        setText(getIcon().getUnicode());
        recomputeStyle();
    }

    public FontAwesome(final Icon icon) {
        this();
        setIcon(icon);
    }

    public FontAwesome(final Icon icon, final String size) {
        this(icon);
        this.setSize(size);
    }

    protected void definePropertyListeners() {
        this.icon.addListener((value, oldIcon, newIcon) -> {
            setText(newIcon.getUnicode());
            recomputeStyle();
        });

        this.size.addListener((value, oldSize, newSize) -> recomputeStyle());
        this.color.addListener((value, oldSize, newSize) -> recomputeStyle());
    }

    public ObjectProperty<Icon> iconProperty() {
        return icon;
    }

    public Icon getIcon() {
        return icon.get();
    }

    public void setIcon(Icon icon) {
        this.icon.set(icon);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty colorProperty() {
        return color;
    }

    public String getColor() {
        return color.get();
    }

    public void setColor(String color) {
        this.color.set(color);
    }

    protected void recomputeStyle() {
        setStyle(String.format("-fx-font-family: %s; -fx-font-size: %s; -fx-fill: %s;",
                FONT_AWESOME_FAMILY, getSize(), getColor()));
    }
}
