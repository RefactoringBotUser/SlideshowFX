package com.twasyl.slideshowfx.markup.html;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX
 */
public class HtmlMarkupTest {
    private static HtmlMarkup markup;

    @BeforeAll
    public static void setUp() {
        markup = new HtmlMarkup();
    }

    @Test
    public void generateWithNull() {
        assertThrows(IllegalArgumentException.class, () -> markup.convertAsHtml(null));
    }

    @Test
    public void generateH1() {
        final String result = markup.convertAsHtml("<h1>A title</h1>");

        assertEquals("<h1>A title</h1>", result);
    }

    @Test
    public void generateH2() {
        final String result = markup.convertAsHtml("<h2>A title</h2>");

        assertEquals("<h2>A title</h2>", result);
    }

    @Test
    public void generateInlineCode() {
        final String result = markup.convertAsHtml("<code>public class Java { }</code>");

        assertEquals("<code>public class Java { }</code>", result);
    }

    @Test
    public void generateCodeBloc() {
        final String result = markup.convertAsHtml("<pre><code>final String s;</code></pre>");

        assertEquals("<pre><code>final String s;</code></pre>", result);
    }

    @Test
    public void generateStrong() {
        final String result = markup.convertAsHtml("<strong>Strong text</strong>");

        assertEquals("<strong>Strong text</strong>", result);
    }

    @Test
    public void generateUnorderedList() {
        final String result = markup.convertAsHtml("<ul><li>One</li><li>Two</li></ul>");

        assertEquals("<ul><li>One</li><li>Two</li></ul>", result);
    }
}
