package com.twasyl.slideshowfx.concurrent;

import com.twasyl.slideshowfx.engine.presentation.PresentationEngine;
import com.twasyl.slideshowfx.engine.presentation.Presentations;
import com.twasyl.slideshowfx.hosting.connector.IHostingConnector;
import com.twasyl.slideshowfx.hosting.connector.exceptions.HostingConnectorException;
import com.twasyl.slideshowfx.hosting.connector.io.RemoteFile;
import com.twasyl.slideshowfx.utils.DialogHelper;
import com.twasyl.slideshowfx.utils.concurrent.SlideshowFXTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ButtonType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This tasks uploads a SlideshowFX presentation to a given service. It takes a {@link com.twasyl.slideshowfx.engine.presentation.PresentationEngine}
 * that will be uploaded to a service represented by a {@link com.twasyl.slideshowfx.hosting.connector.IHostingConnector}. The
 * presentation is uploaded in the given {@code #destination} or at the root if it is {@code null}.
 * The user will also be asked to overwrite the presentation if already exists.
 * Nothing will be done if the user is not authenticated or if the user doesn't want to overwrite an existing file.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0
 */
public class UploadPresentationTask extends SlideshowFXTask<Void> {
    private static final Logger LOGGER = Logger.getLogger(SavePresentationTask.class.getName());

    private PresentationEngine engine;
    private IHostingConnector hostingConnector;
    private RemoteFile destination;

    public UploadPresentationTask(PresentationEngine engine, IHostingConnector hostingConnector, RemoteFile destination) {
        ((SimpleStringProperty) this.titleProperty()).set(String.format("Uploading presentation to %1$s: %2$s", hostingConnector.getName(), engine.getArchive().getName()));
        this.engine = engine;
        this.hostingConnector = hostingConnector;
        this.destination = destination;
    }

    @Override
    protected Void call() throws Exception {
        if(this.engine == null) throw new NullPointerException("The presentation is null");
        if(this.engine.getArchive() == null) throw new NullPointerException("The presentation's archive is null");
        if(!this.hostingConnector.isAuthenticated()) throw new HostingConnectorException(HostingConnectorException.NOT_AUTHENTICATED);

        boolean overwrite = false;
        boolean fileExist = this.hostingConnector.fileExists(Presentations.getCurrentDisplayedPresentation(), destination);

        if(fileExist) {
            final String message = String.format("The '%1$s' presentation already exist in '%2$s'.%nDo you want to overwrite it?",
                    engine.getArchive().getName(), destination.toString());

            final ButtonType response = DialogHelper.showConfirmationAlert("Overwrite presentation", message);

            overwrite = response != null && response == ButtonType.YES;
        }

        if(fileExist && !overwrite) {
            this.cancel();
        } else {
            this.hostingConnector.upload(this.engine, this.destination, overwrite);
        }

        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        this.updateMessage("Presentation uploaded to " + this.hostingConnector.getName());
        this.updateProgress(0, 0);
    }

    @Override
    protected void running() {
        super.running();
        this.updateMessage("Uploading presentation to " + this.hostingConnector.getName());
        this.updateProgress(-1, 0);
    }

    @Override
    protected void failed() {
        super.failed();
        this.updateMessage("Error while uploading the presentation to " + this.hostingConnector.getName());
        this.updateProgress(0, 0);
        LOGGER.log(Level.SEVERE, "Can not upload the presentation", this.getException());
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        this.updateMessage("Cancelled presentation upload");
        this.updateProgress(0, 0);
    }
}
