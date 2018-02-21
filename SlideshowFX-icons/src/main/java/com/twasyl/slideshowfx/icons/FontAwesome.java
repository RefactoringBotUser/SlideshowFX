package com.twasyl.slideshowfx.icons;

import javafx.beans.property.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import static com.twasyl.slideshowfx.icons.FontType.SOLID;

/**
 * Class defining FontAwesome icons.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 2.0
 */
public class FontAwesome extends Text {
    private static class FontCacheKey {
        private double size;
        private FontType type;

        public FontCacheKey(double size, FontType type) {
            this.size = size;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FontCacheKey that = (FontCacheKey) o;
            return Double.compare(that.size, size) == 0 &&
                    type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(size, type);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(FontAwesome.class.getName());
    private final static String SOLID_FONT_PATH = "/com/twasyl/slideshowfx/icons/fonts/fontawesome-solid-5.0.6.otf";
    private final static String REGULAR_FONT_PATH = "/com/twasyl/slideshowfx/icons/fonts/fontawesome-regular-5.0.6.otf";
    private final static String BRAND_FONT_PATH = "/com/twasyl/slideshowfx/icons/fonts/fontawesome-brand-5.0.6.otf";
    private static final Map<FontCacheKey, Font> FONT_CACHE = new HashMap<>();

    private final ObjectProperty<Icon> icon = new SimpleObjectProperty<>(Icon.FOLDER_OPEN);
    private final DoubleProperty size = new SimpleDoubleProperty(10d);
    private final StringProperty color = new SimpleStringProperty("white");

    public FontAwesome() {
        setText(getIcon().getUnicode());
        this.setFont(getFontAwesomeFont(getIcon(), 10d));
        this.definePropertyListeners();
        recomputeStyle();
    }

    public FontAwesome(final Icon icon) {
        this();
        setIcon(icon);
    }

    public FontAwesome(final Icon icon, final Double size) {
        this(icon);
        this.setSize(size);
    }

    protected Font getFontAwesomeFont(final Icon icon, final double size) {
        final Font font;

        final FontCacheKey key = new FontCacheKey(size, icon.getType());
        synchronized (FONT_CACHE) {
            if (FONT_CACHE.containsKey(key)) {
                LOGGER.fine("Returned cached font for size " + size);
                font = FONT_CACHE.get(key);
            } else {
                LOGGER.fine("Font not found in cache for size " + size);
                final String fontPath;
                switch(icon.getType()) {
                    case SOLID:
                        fontPath = SOLID_FONT_PATH;
                        break;
                    case REGULAR:
                        fontPath = REGULAR_FONT_PATH;
                        break;
                    default:
                        fontPath = BRAND_FONT_PATH;
                }
                try (final InputStream stream = FontAwesome.class.getResource(fontPath).openStream()) {
                    font = Font.loadFont(stream, size);
                    FONT_CACHE.put(key, font);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return font;
    }

    protected void definePropertyListeners() {
        this.icon.addListener((value, oldIcon, newIcon) -> {
            this.setFont(getFontAwesomeFont(newIcon, getSize()));
            setText(newIcon.getUnicode());
            recomputeStyle();
        });

        this.size.addListener((value, oldSize, newSize) -> {
            this.setFont(getFontAwesomeFont(getIcon(), newSize.doubleValue()));
            recomputeStyle();
        });

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

    public DoubleProperty sizeProperty() {
        return size;
    }

    public Double getSize() {
        return size.get();
    }

    public void setSize(Double size) {
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
        setStyle(String.format("-fx-fill: %s;", getColor()));
    }
}
