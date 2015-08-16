/*
 * Copyright 2015 Thierry Wasylczenko
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

package com.twasyl.slideshowfx.beans.properties;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

/**
 * This bindings converts the status of a given {@link Task} to the name of a glyph used by FontAwesomeFX. The name
 * returned belongs to the {@link FontAwesomeIcon} enum.
 *
 * @author Thierry Wasylczenko
 * @version 1.0
 * @since SlideshowFX 1.0.0
 */
public class TaskStatusGlyphNameBinding extends StringBinding {

    private final ObjectProperty<Task> task = new SimpleObjectProperty<>();

    public TaskStatusGlyphNameBinding(final Task task) {
        if(task == null) throw new NullPointerException("The task can not be null");

        this.task.set(task);

        super.bind(this.task.get().stateProperty());
    }

    @Override
    protected String computeValue() {

        switch(this.task.get().getState()) {
            case SCHEDULED:
            case READY:
            case RUNNING:
                return FontAwesomeIcon.SPINNER.name();
            case CANCELLED:
            case FAILED:
                return FontAwesomeIcon.EXCLAMATION_CIRCLE.name();
            case SUCCEEDED:
                return FontAwesomeIcon.CHECK_CIRCLE.name();
        }

        return null;
    }

    @Override
    public void dispose() {
        super.unbind(this.task.get().stateProperty());
    }
}