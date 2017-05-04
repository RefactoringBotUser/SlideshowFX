package com.twasyl.slideshowfx.app;

import com.twasyl.slideshowfx.controllers.SlideshowFXController;
import com.twasyl.slideshowfx.engine.presentation.PresentationEngine;
import com.twasyl.slideshowfx.engine.template.TemplateEngine;
import com.twasyl.slideshowfx.global.configuration.GlobalConfiguration;
import com.twasyl.slideshowfx.hosting.connector.IHostingConnector;
import com.twasyl.slideshowfx.osgi.OSGiManager;
import com.twasyl.slideshowfx.server.SlideshowFXServer;
import com.twasyl.slideshowfx.utils.DialogHelper;
import com.twasyl.slideshowfx.utils.io.DeleteFileVisitor;
import com.twasyl.slideshowfx.utils.time.DateTimeUtils;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX application class to launch SlideshowFX.
 *
 * @author Thierry Wasylczenko
 * @version 1.1
 * @since SlideshowFX 1.0
 */
public class SlideshowFX extends Application {

    private static final Logger LOGGER = Logger.getLogger(SlideshowFX.class.getName());
    private static final String PRESENTATION_ARGUMENT_PREFIX = "presentation";
    private static final String TEMPLATE_ARGUMENT_PREFIX = "template";

    private static final ReadOnlyObjectProperty<Stage> stage = new SimpleObjectProperty<>();
    private static final ReadOnlyObjectProperty<Scene> presentationBuilderScene = new SimpleObjectProperty<>();

    private final ReadOnlyObjectProperty<SlideshowFXController> mainController = new SimpleObjectProperty<>();
    private Set<File> filesToOpen;

    @Override
    public void init() throws Exception {
        Font.loadFont(SlideshowFX.class.getResource("/com/twasyl/slideshowfx/fonts/Inconsolata-Regular.ttf").toExternalForm(), 12);

        // Initialize the configuration
        GlobalConfiguration.createApplicationDirectory();
        GlobalConfiguration.createTemplateLibraryDirectory();

        if (GlobalConfiguration.createConfigurationFile()) {
            GlobalConfiguration.fillConfigurationWithDefaultValue();
        } else {
            GlobalConfiguration.fillConfigurationWithDefaultValue();
        }

        if (GlobalConfiguration.createLoggingConfigurationFile()) {
            GlobalConfiguration.fillLoggingConfigurationFileWithDefaultValue();
        }

        // Start the MarkupManager
        LOGGER.info("Starting Felix");
        OSGiManager.getInstance().startAndDeploy();
        // Retrieve the files to open at startup
        final Map<String, String> params = getParameters().getNamed();
        if (params != null && !params.isEmpty()) {
            this.filesToOpen = new HashSet<>();

            // Only files that exist and can be read and opened are added to the list of files to open
            params.forEach((paramName, paramValue) -> {
                if (paramName != null && (paramName.startsWith(PRESENTATION_ARGUMENT_PREFIX) ||
                        paramName.startsWith(TEMPLATE_ARGUMENT_PREFIX))) {

                    final File file = new File(paramValue);

                    if (file.exists() && file.canRead() && file.canWrite() && !this.filesToOpen.contains(file)) {
                        this.filesToOpen.add(file);
                    }
                }
            });
        }

        // Try to load parameters that are passed dynamically with just a value
        final List<String> unnamedParams = getParameters().getUnnamed();
        if (unnamedParams != null && !unnamedParams.isEmpty()) {
            unnamedParams.forEach(param -> {
                final File file = new File(param);

                if ((file.getName().endsWith(TemplateEngine.DEFAULT_ARCHIVE_EXTENSION) ||
                        file.getName().endsWith(PresentationEngine.DEFAULT_ARCHIVE_EXTENSION))
                        && file.exists() && file.canRead() && file.canWrite() && !this.filesToOpen.contains(file)) {
                    this.filesToOpen.add(file);
                }
            });
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        ((SimpleObjectProperty<Stage>) SlideshowFX.stage).set(stage);

        try {
            final FXMLLoader loader = new FXMLLoader();
            final Parent root = loader.load(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/fxml/SlideshowFX.fxml"));
            ((SimpleObjectProperty<SlideshowFXController>) this.mainController).set(loader.getController());

            final Scene scene = new Scene(root);
            ((SimpleObjectProperty<Scene>) presentationBuilderScene).set(scene);

            stage.setTitle("SlideshowFX");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.getIcons().addAll(
                    new Image(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/images/appicons/16.png")),
                    new Image(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/images/appicons/32.png")),
                    new Image(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/images/appicons/64.png")),
                    new Image(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/images/appicons/128.png")),
                    new Image(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/images/appicons/256.png")),
                    new Image(SlideshowFX.class.getResourceAsStream("/com/twasyl/slideshowfx/images/appicons/512.png")));
            stage.show();

            if (this.filesToOpen != null && !this.filesToOpen.isEmpty()) {
                this.filesToOpen.forEach(file -> {
                    try {
                        this.mainController.get().openTemplateOrPresentation(file);
                    } catch (IllegalAccessException | FileNotFoundException e) {
                        LOGGER.log(Level.SEVERE, "Can not open file at startup", e);
                    }
                });
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Can not start the application", ex);
            DialogHelper.showError("Error", "Can not start the application");
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        if (this.mainController.get() != null) {
            this.mainController.get().closeAllPresentations(true);

            deleteTemporaryFiles();
            stopInternalServer();
        }

        stopOSGIManager();
    }

    /**
     * Deletes temporary files older than the configuration parameter {@link GlobalConfiguration#TEMPORARY_FILES_MAX_AGE_PARAMETER}.
     */
    private void deleteTemporaryFiles() {
        if (GlobalConfiguration.canDeleteTemporaryFiles()) {
            LOGGER.info("Cleaning temporary files");
            final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));

            Arrays.stream(tempDirectory.listFiles())
                    .filter(file -> file.getName().startsWith("sfx-"))
                    .filter(DateTimeUtils.getFilterForFilesOlderThanGivenDays(GlobalConfiguration.getTemporaryFilesMaxAge()))
                    .forEach(file -> {
                        try {
                            Files.walkFileTree(file.toPath(), new DeleteFileVisitor());
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE,
                                    String.format("Can not delete temporary file %1$s", file.getAbsolutePath()),
                                    e);
                        }
                    });
        }
    }

    /**
     * Stop the internal server if it is running.
     */
    private void stopInternalServer() {
        if (SlideshowFXServer.getSingleton() != null) {
            LOGGER.info("Closing the internal server");
            SlideshowFXServer.getSingleton().stop();
        }
    }

    /**
     * Stops the OSGI manager. If some {@link IHostingConnector} are registered, disconnect from them.
     */
    private void stopOSGIManager() {
        this.stopHostingConnectors();
        LOGGER.info("Stopping the OSGi manager");
        OSGiManager.getInstance().stop();
    }

    /**
     * Disconnect all hosting connectors if they are running.
     */
    private void stopHostingConnectors() {
        final List<IHostingConnector> connectors = OSGiManager.getInstance().getInstalledServices(IHostingConnector.class);

        if (!connectors.isEmpty()) {
            LOGGER.info("Disconnecting from all hosting connectors");
            connectors.forEach(hostingConnector -> hostingConnector.disconnect());
        }
    }

    public static ReadOnlyObjectProperty<Stage> stageProperty() {
        return stage;
    }

    public static Stage getStage() {
        return stageProperty().get();
    }

    public static void main(String[] args) {
        SlideshowFX.launch(args);
    }
}