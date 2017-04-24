package com.twasyl.slideshowfx.controllers;

import com.twasyl.slideshowfx.controls.TemplateFileButton;
import com.twasyl.slideshowfx.global.configuration.GlobalConfiguration;
import com.twasyl.slideshowfx.io.SlideshowFXExtensionFilter;
import com.twasyl.slideshowfx.utils.DialogHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.twasyl.slideshowfx.engine.template.TemplateEngine.DEFAULT_ARCHIVE_EXTENSION;
import static com.twasyl.slideshowfx.io.SlideshowFXFileFilters.TEMPLATE_FILE_FILTER;

/**
 * Controller of the {@code TemplateChooser.fxml} view.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX @@NEXT-VERSION@@
 */
public class TemplateChooserController implements Initializable {

    private static Logger LOGGER = Logger.getLogger(TemplateChooserController.class.getName());

    @FXML private TilePane templates;
    @FXML private Button importNewTemplate;
    @FXML private WebView overview;

    /**
     * Get the template that has been chosen by the user.
     * @return The file chosen by the user or {@code null} if no selection.
     */
    public File getChosenTemplate() {
        final TemplateFileButton button = this.templates.getChildren().stream()
                .filter(node -> node != null && node instanceof TemplateFileButton)
                .map(node -> (TemplateFileButton) node)
                .filter(TemplateFileButton::isSelected)
                .findAny()
                .orElse(null);

        if(button != null) {
            return button.getTemplateFile();
        } else {
            return null;
        }
    }

    @FXML
    private void dragFilesOverImportButton(final DragEvent event) {
        final Dragboard dragboard = event.getDragboard();

        if (event.getGestureSource() != this.importNewTemplate && dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }

        event.consume();
    }

    @FXML
    private void dropFileOverImportButton(final DragEvent event) {
        final Dragboard dragboard = event.getDragboard();
        boolean allFilesAreValid;

        if (event.getGestureSource() != this.importNewTemplate && dragboard.hasFiles()) {
            allFilesAreValid = true;
            File templateFile;
            int index = 0;

            while (allFilesAreValid && index < dragboard.getFiles().size()) {
                templateFile = dragboard.getFiles().get(index++);

                allFilesAreValid = this.isTemplateFileValid(templateFile);
            }
        } else {
            allFilesAreValid = false;
        }

        event.setDropCompleted(allFilesAreValid);
        event.consume();
    }

    @FXML
    private void importNewTemplate(final ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(SlideshowFXExtensionFilter.TEMPLATE_FILTER);
        File templateFile = chooser.showOpenDialog(null);

        if (templateFile != null) {
            this.isTemplateFileValid(templateFile);
        }
    }

    /**
     * Checks if a file chosen by the user is valid or not. In case the file is not a valid template, then an error
     * message is displayed. If it is valid, the template file is added to the list of templates to install and displayed
     * in the templates table.
     *
     * @param templateFile The template file to check.
     * @return {@code true} if the file is a valid template, {@code false} otherwise.
     */
    protected boolean isTemplateFileValid(final File templateFile) {
        boolean valid = false;

        try {
            if (fileSeemsValid(templateFile)) {
                final TemplateFileButton pluginFileButton = createTemplateFileButton(templateFile);
                pluginFileButton.setSelected(true);

                this.templates.getChildren().add(pluginFileButton);

                valid = true;
            } else {
                DialogHelper.showError("Invalid template", "The chosen template seems invalid");
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Can not determine if the template file seems valid", e);
        }

        return valid;
    }

    private TemplateFileButton createTemplateFileButton(File templateFile) {
        final TemplateFileButton pluginFileButton = new TemplateFileButton(templateFile);
        pluginFileButton.selectedProperty().addListener((selectedValue, oldSelected, newSelected) -> {
            try {
                if (newSelected) {
                    final File extraction = new File(System.getProperty("java.io.tmpdir"), "sfx-new-presentation-" + templateFile.getName());
                    pluginFileButton.extractTemplate(extraction);

                    final File sample = new File(extraction, "sample.html");

                    if(sample.exists()) {
                        this.overview.getEngine().load(sample.toURI().toURL().toExternalForm());
                    }
                } else {
                    pluginFileButton.removeExtractedContent();
                }
            } catch (IOException ex) {

            }
        });

        return pluginFileButton;
    }

    /**
     * Checks if the given {@link File file} is seems to be a valid template file.
     *
     * @param file The file to check.
     * @return {@code true} if the file seems to be a template, {@code false} otherwise.
     * @throws NullPointerException  If the file is {@code null}.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    protected boolean fileSeemsValid(final File file) throws FileNotFoundException {
        if (file == null) throw new NullPointerException("The file to check can not be null");
        if (!file.exists()) throw new FileNotFoundException("The file to check must exist");

        boolean isValid = file.getName().endsWith(DEFAULT_ARCHIVE_EXTENSION);

        return isValid;
    }

    protected void populatePluginsView() {
        final File templateLibraryDirectory = GlobalConfiguration.getTemplateLibraryDirectory();

        if (templateLibraryDirectory.exists() && templateLibraryDirectory.canRead()) {
            final File[] templates = templateLibraryDirectory.listFiles(TEMPLATE_FILE_FILTER);

            Arrays.stream(templates).forEach(file -> {
                final TemplateFileButton button = createTemplateFileButton(file);

                if(button != null) {
                    this.templates.getChildren().add(button);
                }
            });
        }
    }

    public void dispose() {
        this.templates.getChildren().stream()
                .filter(node -> node instanceof TemplateFileButton)
                .map(node -> (TemplateFileButton) node)
                .forEach(button -> {
                    try {
                        button.removeExtractedContent();
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Error while trying to clean resources", e);
                    }
                });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.populatePluginsView();
    }
}
