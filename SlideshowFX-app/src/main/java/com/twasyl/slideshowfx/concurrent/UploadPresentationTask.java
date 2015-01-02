/*
 * Copyright 2014 Thierry Wasylczenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twasyl.slideshowfx.concurrent;

import com.twasyl.slideshowfx.controls.Dialog;
import com.twasyl.slideshowfx.dao.PresentationDAO;
import com.twasyl.slideshowfx.engine.presentation.PresentationEngine;
import com.twasyl.slideshowfx.uploader.IUploader;
import com.twasyl.slideshowfx.uploader.io.RemoteFile;
import javafx.concurrent.Task;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This tasks uploads a SlideshowFX presentation to a given service. It takes a {@link com.twasyl.slideshowfx.engine.presentation.PresentationEngine}
 * that will be uploaded to a service represented by a {@link com.twasyl.slideshowfx.uploader.IUploader}. The
 * presentation is uploaded in the given {@code #destination} or at the root if it is {@code null}.
 * The user will also be asked to overwrite the presentation if already exists.
 * Nothing will be done if the user is not authenticated or if the user doesn't want to overwrite an existing file.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since 1.0
 */
public class UploadPresentationTask extends Task<Void> {
    private static final Logger LOGGER = Logger.getLogger(SavePresentationTask.class.getName());

    private PresentationEngine engine;
    private IUploader uploader;
    private RemoteFile destination;

    public UploadPresentationTask(PresentationEngine engine, IUploader uploader, RemoteFile destination) {
        this.engine = engine;
        this.uploader = uploader;
        this.destination = destination;
    }

    @Override
    protected Void call() throws Exception {

        // Ensure the presentation has already been saved
        if(this.engine != null && this.engine.getArchive() != null
                && this.uploader.isAuthenticated()) {

            boolean overwrite = false;
            boolean fileExist = this.uploader.fileExists(PresentationDAO.getInstance().getCurrentPresentation(), destination);

            if(fileExist) {
                final String message = String.format("The '%1$s' presentation already exist in '%2$s'.\n Do you want to overwrite it?",
                        engine.getArchive().getName(), destination.toString());

                final Dialog.Response answer = Dialog.showConfirmDialog(null, "Overwrite presentation", message);

                overwrite = answer == Dialog.Response.YES;
            }

            if(fileExist && !overwrite) {
                this.cancelled();
            } else {
                try {
                    this.uploader.upload(this.engine, this.destination, overwrite);
                    this.succeeded();
                } catch (FileNotFoundException e) {
                    this.setException(e);
                    this.failed();
                }
            }
        } else this.failed();

        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        this.updateMessage("Presentation uploaded to " + this.uploader.getName());
        this.updateProgress(0, 0);
    }

    @Override
    protected void running() {
        super.running();
        this.updateMessage("Uploading presentation to " + this.uploader.getName());
        this.updateProgress(-1, 0);
    }

    @Override
    protected void failed() {
        super.failed();
        this.updateMessage("Error while uploading the presentation to " + this.uploader.getName());
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