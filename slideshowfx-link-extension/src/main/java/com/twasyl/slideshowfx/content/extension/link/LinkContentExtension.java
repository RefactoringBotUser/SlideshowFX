package com.twasyl.slideshowfx.content.extension.link;

import com.twasyl.slideshowfx.content.extension.AbstractContentExtension;
import com.twasyl.slideshowfx.content.extension.link.controllers.LinkContentExtensionController;
import com.twasyl.slideshowfx.markup.IMarkup;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.twasyl.slideshowfx.icons.Icon.LINK;

/**
 * The LinkContentExtension extends the AbstractContentExtension. It allows to build a content containing links to insert
 * inside a SlideshowFX presentation.
 * This extension doesn't use other resources
 * This extension supports HTML and Textile markup languages.
 *
 * @author Thierry Wasylczenko
 * @version 1.2
 * @since SlideshowFX 1.0
 */
public class LinkContentExtension extends AbstractContentExtension<LinkContentExtensionController> {
    private static final Logger LOGGER = Logger.getLogger(LinkContentExtension.class.getName());

    public LinkContentExtension() {
        super("LINK", null,
                LINK,
                "Insert a link",
                "Insert a link");
    }

    @Override
    public Pane getUI() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("/com/twasyl/slideshowfx/content/extension/link/fxml/LinkContentExtension.fxml"));
        Pane root = null;

        try {
            loader.setClassLoader(getClass().getClassLoader());
            root = loader.load();
            this.controller = loader.getController();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not load UI for LinkContentExtension", e);
        }

        return root;
    }

    @Override
    public String buildContentString(IMarkup markup) {
        final StringBuilder builder = new StringBuilder();
        final boolean addressNotEmpty = this.getController().getAddress() != null && !this.getController().getAddress().trim().isEmpty();

        if (addressNotEmpty) {
            if (markup == null || "HTML".equals(markup.getCode())) {
                builder.append(this.buildDefaultContentString());
            } else if ("TEXTILE".equals(markup.getCode())) {
                builder.append(this.buildTextileContentString());
            } else if ("MARKDOWN".equals(markup.getCode())) {
                builder.append(this.buildMarkdownContentString());
            } else {
                builder.append(this.buildDefaultContentString());
            }
        }
        return builder.toString();
    }

    @Override
    public String buildDefaultContentString() {

        final StringBuilder builder = new StringBuilder();
        builder.append("<a href=\"").append(this.getController().getAddress()).append("\">")
                .append(this.getController().getText() == null || this.getController().getText().trim().isEmpty() ?
                        this.getController().getAddress() : this.getController().getText())
                .append("</a>");

        return builder.toString();
    }

    /**
     * Build the string representing a link in textile.
     *
     * @return The built string in the textile markup language.
     */
    private String buildTextileContentString() {
        final StringBuilder builder = new StringBuilder("\"");

        if (this.getController().getText() == null || this.getController().getText().trim().isEmpty()) {
            builder.append(this.getController().getAddress());
        } else {
            builder.append(this.getController().getText());
        }

        builder.append("\":").append(this.getController().getAddress());
        return builder.toString();
    }

    /**
     * Build the string representing a link in markdown.
     *
     * @return The built string in the markdown language.
     */
    private String buildMarkdownContentString() {
        final StringBuilder builder = new StringBuilder();

        final boolean emptyText = this.getController().getText() == null || this.getController().getText().trim().isEmpty();

        if (!emptyText) {
            builder.append("[").append(this.getController().getText().trim()).append("](");
        }

        builder.append(this.getController().getAddress());

        if (!emptyText) {
            builder.append(")");
        }

        return builder.toString();
    }

    @Override
    public ReadOnlyBooleanProperty areInputsValid() {
        return this.getController().areInputsValid();
    }
}
