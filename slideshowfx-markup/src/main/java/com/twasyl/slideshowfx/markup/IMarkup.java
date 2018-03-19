package com.twasyl.slideshowfx.markup;

import com.twasyl.slideshowfx.plugin.IPlugin;

/**
 * Defines the contract to be considered as a supported markup language for SlideshowFX.
 * A supported markup language allows the user to define the content of each slides instead of writing HTML code directly.
 * As supported language, a mechanism has to be provided in order to convert the markup language to HTML.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0
 */
public interface IMarkup extends IPlugin {
    /**
     * Get the code for the markup. The code uniquely identifies the markup language in the SlideshowFX application. It
     * is used for example used to retrieve the correct bundle to call when the user decides to define the slide content.
     * @return The code of the markup.
     */
    String getCode();

    /**
     * Convert the given string written in the markup syntax as HTML.
     * @param markupString The string written in the markup syntax to convert as HTML.
     * @return The HTML representation of the given String.
     * @throws IllegalArgumentException If the given string is null or empty this exception is thrown.
     */
    String convertAsHtml(String markupString) throws IllegalArgumentException;

    /**
     * Return the ACE mode that is used for the editor available in SlideshowFX. The editor uses ACE (from Cloud9) in
     * order to define slides' content. In order to get the right syntax highlighting, ACE needs the mode.
     * @return The ACE mode corresponding to this markup language.
     */
    String getAceMode();
}
