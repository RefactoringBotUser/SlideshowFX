package com.twasyl.slideshowfx.markup.markdown;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Thierry Wasylczenko
 * @since SlideshowFX
 */
public class MarkdownMarkupTest {

    private static MarkdownMarkup markup;

    @BeforeAll
    public static void setUp() {
        markup = new MarkdownMarkup();
    }

    @Test
    public void generateWithNull() {
        assertThrows(IllegalArgumentException.class, () -> markup.convertAsHtml(null));
    }

    @Test
    public void generateH1() {
        final String result = markup.convertAsHtml("# A title");

        assertEquals("<h1>A title</h1>", result);
    }

    @Test
    public void generateH2() {
        final String result = markup.convertAsHtml("## A title");

        assertEquals("<h2>A title</h2>", result);
    }

    @Test
    public void generateInlineCode() {
        final String result = markup.convertAsHtml("`public class Java { }`");

        assertEquals("<p><code>public class Java { }</code></p>", result);
    }

    @Test
    public void generateCodeBloc() {
        final String result = markup.convertAsHtml("    final String s;");

        assertEquals("<pre><code>final String s;\n</code></pre>", result);
    }

    @Test
    public void generateStrong() {
        final String result = markup.convertAsHtml("*Strong text*");

        assertEquals("<p><em>Strong text</em></p>", result);
    }

    @Test
    public void generateUnorderedList() {
        final String result = markup.convertAsHtml("* One\n* Two");

        assertEquals("<ul>\n<li>One</li>\n<li>Two</li>\n</ul>", result);
    }
}
