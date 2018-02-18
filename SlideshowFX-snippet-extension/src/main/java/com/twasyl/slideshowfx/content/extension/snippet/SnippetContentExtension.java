package com.twasyl.slideshowfx.content.extension.snippet;

import com.twasyl.slideshowfx.content.extension.AbstractContentExtension;
import com.twasyl.slideshowfx.content.extension.ResourceType;
import com.twasyl.slideshowfx.content.extension.snippet.controllers.SnippetContentExtensionController;
import com.twasyl.slideshowfx.markup.IMarkup;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.twasyl.slideshowfx.global.configuration.GlobalConfiguration.getDefaultCharset;
import static com.twasyl.slideshowfx.icons.Icon.TERMINAL;

/**
 * The content extension that allows to insert code snippet in a presentation. This extension only supports HTML for
 * inserting the content in the presentation, meaning that HTML code will always be returned when calling
 * {@link #buildDefaultContentString()} and {@link #buildContentString(IMarkup)}.
 *
 * @author Thierry Wasylczenko
 * @version 1.1
 * @since SlideshowFX 1.0
 */
public class SnippetContentExtension extends AbstractContentExtension {
    private static final Logger LOGGER = Logger.getLogger(SnippetContentExtension.class.getName());

    private SnippetContentExtensionController controller;

    public SnippetContentExtension() {
        super("SNIPPET",
                SnippetContentExtension.class.getResource("/com/twasyl/slideshowfx/content/extension/snippet/resources/snippet-executor.zip"),
                TERMINAL,
                "Insert an executable code snippet",
                "Insert an executable code snippet");

        final String baseURL = "snippet-executor/";

        // Add URL
        this.putResource(ResourceType.CSS_FILE, baseURL.concat("font-awesome-4.6.3/css/font-awesome.min.css"));
        this.putResource(ResourceType.CSS_FILE, baseURL.concat("prism/1.11.0/prism.css"));
        this.putResource(ResourceType.JAVASCRIPT_FILE, baseURL.concat("prism/1.11.0/prism.js"));
    }

    @Override
    public Pane getUI() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("/com/twasyl/slideshowfx/content/extension/snippet/fxml/SnippetContentExtension.fxml"));
        Pane root = null;

        try {
            loader.setClassLoader(getClass().getClassLoader());
            root = loader.load();
            this.controller = loader.getController();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not load UI for SnippetContentExtension", e);
        }

        return root;
    }

    @Override
    public String buildContentString(IMarkup markup) {
        return this.buildDefaultContentString();
    }

    @Override
    public String buildDefaultContentString() {
        final StringBuilder builder = new StringBuilder();

        long id = System.currentTimeMillis();
        final String codeSnippetId = "code-snippet-" + id;
        final String codeSnippetConsoleOuputId = "code-snippet-output-" + id;
        final String codeSnippetConsoleId = "code-snippet-console-" + id;
        final String executeCodeSnippetId = "code-snippet-execute-" + id;

        builder.append("<div style=\"width: 100%; height: 50px; background-color: #ECECEC;")
                .append("border-radius: 10px 10px 0 0\" id=\"").append(codeSnippetId).append("\">\n")
                .append("   <i id=\"").append(executeCodeSnippetId).append("\" class=\"fa fa-terminal fa-fw\" ")
                .append("onclick=\"javascript:executeCodeSnippet('")
                .append(this.controller.getSnippetExecutor().getCode())
                .append("', '").append(Base64.getEncoder().encodeToString(this.controller.getCodeSnippet().toJson().getBytes(getDefaultCharset()))).append("', '")
                .append(id).append("');\"></i>\n")
                .append("</div>\n")
                .append("<pre id=\"").append(codeSnippetConsoleId).append("\" style=\"margin-top: 0\" ")
                .append("class=\"").append(this.controller.getSnippetExecutor().getCssClass()).append("\">")
                .append("<code class=\"").append(this.controller.getSnippetExecutor().getCssClass()).append("\">")
                .append(this.controller.getCodeSnippet().getCode())
                .append("</code></pre>\n")
                .append("<pre id=\"").append(codeSnippetConsoleOuputId).append("\" class=\"language-bash\" style=\"display: none; margin-top: 0;\">")
                .append("<code class=\"language-bash\"></code></pre>");
        return builder.toString();
    }

    @Override
    public ReadOnlyBooleanProperty areInputsValid() {
        return this.controller.areInputsValid();
    }
}
