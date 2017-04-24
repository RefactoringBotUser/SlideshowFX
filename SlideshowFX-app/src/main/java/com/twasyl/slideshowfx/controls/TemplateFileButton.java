package com.twasyl.slideshowfx.controls;

import com.twasyl.slideshowfx.utils.JSONHelper;
import com.twasyl.slideshowfx.utils.ZipUtils;
import com.twasyl.slideshowfx.utils.io.IOUtils;
import io.vertx.core.json.JsonObject;
import javafx.scene.control.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.twasyl.slideshowfx.engine.template.configuration.TemplateConfiguration.TEMPLATE;
import static com.twasyl.slideshowfx.engine.template.configuration.TemplateConfiguration.TEMPLATE_NAME;
import static java.util.zip.ZipFile.OPEN_READ;

/**
 * Implementation of a {@link ToggleButton} representing a file of a template. The button has a CSS class named
 * {@code template-file-button}.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public class TemplateFileButton extends ToggleButton {
    private static Logger LOGGER = Logger.getLogger(TemplateFileButton.class.getName());

    private String templateName;
    private File extractionLocation = null;

    public TemplateFileButton(final File templateFile) {
        this.setUserData(templateFile);
        this.getStyleClass().add("template-file-button");

        this.templateName = determineTemplateName();

        this.setText(this.templateName);
    }

    /**
     * Get the template file associated to this button.
     *
     * @return The template file associated to this button.
     */
    public File getTemplateFile() {
        return (File) this.getUserData();
    }

    /**
     * Extracts the template to a desired location.
     *
     * @param destination The folder where extract the template.
     * @throws IOException If an error occurs during the extraction.
     */
    public void extractTemplate(final File destination) throws IOException {
        this.extractionLocation = destination;
        ZipUtils.unzip(getTemplateFile(), this.extractionLocation);
    }

    /**
     * Removes the content that has been extracted by the {@link #extractTemplate(File)} method.
     *
     * @throws IOException If an error occurs when trying to remove the extracted content.
     */
    public void removeExtractedContent() throws IOException {
        if (this.extractionLocation != null) {
            IOUtils.deleteDirectory(this.extractionLocation);
        }
    }

    protected final String determineTemplateName() {
        String name = getTemplateFile().getName();
        JsonObject jsonConfig = null;

        try (final ZipFile zip = new ZipFile(getTemplateFile(), OPEN_READ)) {

            final ZipEntry templateConfigEntry = zip.stream().filter(entry -> "template-config.json".equals(entry.getName())).findAny().orElseGet(null);

            if (templateConfigEntry != null) {
                try (final InputStreamReader input = new InputStreamReader(zip.getInputStream(templateConfigEntry))) {
                    jsonConfig = JSONHelper.readFromReader(input);
                }
            } else {
                LOGGER.log(Level.INFO, "No template-config.json file in the template");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Can not determine template's name", e);
        }

        if (jsonConfig != null) {
            name = jsonConfig.getJsonObject(TEMPLATE).getString(TEMPLATE_NAME);

            if (name.trim().isEmpty()) {
                name = getTemplateFile().getName();
            }
        }

        return name;
    }
}
