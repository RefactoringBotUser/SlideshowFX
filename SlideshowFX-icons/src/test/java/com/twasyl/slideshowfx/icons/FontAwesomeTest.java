package com.twasyl.slideshowfx.icons;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.twasyl.slideshowfx.icons.FontType.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test of class {@link FontAwesome}.
 *
 * @author Thierry Wasylczenko
 * @since SlideshowFX 2.0
 */
public class FontAwesomeTest {

    @Test
    public void obtainRegularFontFile() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeFontFile(REGULAR)) {
            assertNotNull(stream);
        }
    }

    @Test
    public void obtainSolidFontFile() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeFontFile(SOLID)) {
            assertNotNull(stream);
        }
    }

    @Test
    public void obtainBrandFontFile() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeFontFile(BRAND)) {
            assertNotNull(stream);
        }
    }

    @Test
    public void obtainCSSFontFile() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeCSSFile()) {
            assertNotNull(stream);
        }
    }

    @Test
    public void obtainJavaScriptFontFile() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeJSFile()) {
            assertNotNull(stream);
        }
    }

    @Test
    public void obtainJavaScriptFileFromRelativePath() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeFile("js/fontawesome-all.min.js")) {
            assertNotNull(stream);
        }
    }

    @Test
    public void obtainCSSFileFromRelativePath() throws IOException {
        try (final InputStream stream = FontAwesome.getFontAwesomeFile("css/fa-svg-with-js.css")) {
            assertNotNull(stream);
        }
    }
}
