package com.twasyl.slideshowfx.controls.notification;

import com.twasyl.slideshowfx.beans.properties.TaskStatusIconBinding;
import com.twasyl.slideshowfx.beans.properties.TaskStatusIconColorBinding;
import com.twasyl.slideshowfx.icons.FontAwesome;
import com.twasyl.slideshowfx.icons.Icon;
import com.twasyl.slideshowfx.utils.DialogHelper;
import com.twasyl.slideshowfx.utils.beans.binding.LocalTimeBinding;
import com.twasyl.slideshowfx.utils.concurrent.SlideshowFXTask;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a notification to be present in the {@link NotificationCenter}. It displays the title of the {@link SlideshowFXTask}
 * that is associated to the notification, as well as an icon representing the status of the task and a button for deleting
 * the notification in the notification center.
 *
 * @author Thierry Wasylczenko
 * @version 1.1
 * @since SlideshowFX 1.0
 */
public class Notification extends MenuItem {
    private static final Logger LOGGER = Logger.getLogger(Notification.class.getName());
    private final ReadOnlyObjectProperty<SlideshowFXTask> task = new SimpleObjectProperty<>();

    public Notification(final SlideshowFXTask task) {
        ((SimpleObjectProperty) this.task).set(task);

        final Text taskTitle = this.getTaskTitle();
        final Text statusChangeTime = this.getStatusChangeTimeText();

        final TextFlow statusFlow = new TextFlow(taskTitle, new Text("\n"), statusChangeTime);
        statusFlow.setMaxWidth(250);

        final FontAwesome statusIcon = this.getStatusIcon();

        final Button deleteButton = this.getDeleteButton();

        final HBox content = new HBox(5);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(statusIcon, statusFlow, deleteButton);

        this.setGraphic(content);

        this.setOnAction(event -> {
            if (this.task.get().getState() == Worker.State.FAILED) {

                final StringBuilder builder = new StringBuilder(this.task.get().getException().getMessage())
                        .append("\n");

                try (final StringWriter stringWriter = new StringWriter();
                     final PrintWriter writer = new PrintWriter(stringWriter)) {

                    this.task.get().getException().printStackTrace(writer);
                    writer.flush();

                    builder.append(stringWriter.toString());
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Can not parse error stacktrace", ex);
                }

                final TextArea errorMessage = new TextArea(builder.toString());
                errorMessage.setPrefColumnCount(50);
                errorMessage.setPrefRowCount(20);
                errorMessage.setWrapText(true);
                errorMessage.setEditable(false);
                errorMessage.setBorder(Border.EMPTY);
                DialogHelper.showError(this.task.get().getTitle(), errorMessage);
            }
        });
    }

    /**
     * Initialize the {@link javafx.scene.Node} that displays the {@link Task#titleProperty() title} of the task.
     *
     * @return The {@link javafx.scene.Node} that displays the title of the task.
     */
    private Text getTaskTitle() {
        final Text taskTitle = new Text();
        taskTitle.getStyleClass().addAll("text", "notification", "title");
        taskTitle.textProperty().bind(this.task.get().titleProperty());

        return taskTitle;
    }

    /**
     * Initialize the {@link javafx.scene.Node} that displays the time when the status of the task has changed.
     *
     * @return The {@link javafx.scene.Node} that displays the change time of the status of the task.
     */
    private Text getStatusChangeTimeText() {
        final Text statusChangeTime = new Text();
        statusChangeTime.getStyleClass().addAll("text", "notification", "time");
        statusChangeTime.textProperty().bind(new LocalTimeBinding(this.task.get().statusChangedTimeProperty()));

        return statusChangeTime;
    }

    /**
     * Initialize the {@link javafx.scene.Node} that will contain the icon indicating the status (RUNNING, SUCCEEDED, FAILED, ...) of the
     * notification.
     *
     * @return The {@link javafx.scene.Node} indicating the status of the notification.
     */
    private FontAwesome getStatusIcon() {
        final FontAwesome statusIcon = new FontAwesome();

        final RotateTransition rotation = new RotateTransition(Duration.seconds(1), statusIcon);
        rotation.setByAngle(360);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setInterpolator(Interpolator.LINEAR);

        statusIcon.iconProperty().addListener((glyphValue, oldGlyph, newGlyph) -> {
            if (Icon.SPINNER.equals(newGlyph)) rotation.playFromStart();
            else {
                rotation.stop();
                statusIcon.setRotate(0);
            }
        });
        statusIcon.iconProperty().bind(new TaskStatusIconBinding(this.task.get()));
        statusIcon.colorProperty().bind(new TaskStatusIconColorBinding(this.task.get()));
        statusIcon.setSize("20");

        return statusIcon;
    }

    /**
     * Initialize the {@link Button} that will allow to remove the notification from the {@link NotificationCenter}.
     *
     * @return The button for deleting the notification from the {@link NotificationCenter}.
     */
    private Button getDeleteButton() {
        final FontAwesome deleteIcon = new FontAwesome(Icon.TIMES);
        deleteIcon.setSize("10");

        final Button deleteButton = new Button();
        deleteButton.getStyleClass().add("notification");
        deleteButton.setGraphic(deleteIcon);
        deleteButton.setOnAction(event -> Notification.this.getParentPopup().getItems().remove(Notification.this));

        return deleteButton;
    }

    /**
     * Get the task associated to this notification.
     *
     * @return The property containing the task associated to this notification.
     */
    public ReadOnlyObjectProperty<SlideshowFXTask> taskProperty() {
        return task;
    }

    /**
     * Get the task associated to this notification.
     *
     * @return The task associated to this notification.
     */
    public Task getTask() {
        return task.get();
    }
}
