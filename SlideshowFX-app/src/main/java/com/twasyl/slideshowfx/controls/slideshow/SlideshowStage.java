package com.twasyl.slideshowfx.controls.slideshow;

import com.twasyl.slideshowfx.engine.presentation.configuration.Slide;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * The stage is defined when the presentation enters in slideshow mode. It is necessary to create the stage with a {@link Context}
 * to properly configure the stage.
 * The stage will take care of the creation of the necessary screens (slideshow and information screens) as well as
 * defining to which event screen will respond (key event and so on).
 *
 * @author Thierry Wasylczenko
 * @version 1.0.0
 * @since SlideshowFX 1.0
 */
public class SlideshowStage {
    private static final Logger LOGGER = Logger.getLogger(SlideshowStage.class.getName());
    private static final String DO_NOT_CONSIDER_EVENT_TEXT = "do_not_consider";
    private Context context;

    private Runnable onCloseAction = null;
    private Stage slideshowStage, informationStage;
    private SlideshowPane slideshowPane;
    private InformationPane informationPane;

    /**
     * Creates a stage according the configuration stored within the {@code context}.
     *
     * @param context The context defining the configuration of the stage.
     */
    public SlideshowStage(final Context context) {
        this.context = context;

        this.initializeSlideshowStage();
        this.initializeInformationStage();
        this.initializeKeyEvents();
    }

    /**
     * Initializes the stage that will host the presentation itself. This stage will always be created and displayed.
     * If the computer of the user has more than one screen, the stage will be displayed on the first secondary screen
     * found, which should be a video projector for instance. If only one screen is present, the stage will be displayed
     * on it.
     */
    private final void initializeSlideshowStage() {
        this.slideshowPane = new SlideshowPane(this.context);

        final Scene scene = new Scene(this.slideshowPane);

        this.slideshowStage = new Stage(StageStyle.UNDECORATED);
        this.slideshowStage.setScene(scene);

        /*
        Getting the number of screens in order to place the stage for the presentation on the right one. If there is
        more than one screen, then the slideshow is displayed on the first secondary screen in order to put the
        information stage on the primary one. If there is only one screen then the slideshow will be displayed on it.
         */
        final int numberOfScreens = Screen.getScreens().size();
        Screen screenToDisplayOn = null;

        if (numberOfScreens == 1) {
            screenToDisplayOn = Screen.getPrimary();
        } else {
            final Screen primary = Screen.getPrimary();
            int index = 0;

            do {
                if (!Screen.getScreens().get(index).equals(primary)) {
                    screenToDisplayOn = Screen.getScreens().get(index);
                }
                index++;
            } while (screenToDisplayOn == null && index < numberOfScreens);
        }

        if (screenToDisplayOn != null) {
            this.slideshowStage.setX(screenToDisplayOn.getBounds().getMinX());
            this.slideshowStage.setY(screenToDisplayOn.getBounds().getMinY());
            this.slideshowStage.setWidth(screenToDisplayOn.getBounds().getWidth());
            this.slideshowStage.setHeight(screenToDisplayOn.getBounds().getHeight());
        }

        this.slideshowStage.setAlwaysOnTop(true);
    }

    /**
     * Initializes the stage that will host the information (time elapsed since the beginning of the presentation,
     * current slide, next slide) of the presentation. This stage will only be created if more than one screen is present
     * on the user computer. The information screen will be displayed on the primary screen.
     */
    private final void initializeInformationStage() {
        /*
        The information screen is only shown if there are more than one screen detected. The information screen is
        displayed on the primary screen.
         */
        final int numberOfScreens = Screen.getScreens().size();

        if (numberOfScreens > 1) {
            final Screen screenToDisplayOn = Screen.getPrimary();

            this.informationPane = new InformationPane(this.context);

            final Scene scene = new Scene(this.informationPane);

            this.informationStage = new Stage(StageStyle.UNDECORATED);
            this.informationStage.setX(screenToDisplayOn.getBounds().getMinX());
            this.informationStage.setY(screenToDisplayOn.getBounds().getMinY());
            this.informationStage.setWidth(screenToDisplayOn.getBounds().getWidth());
            this.informationStage.setHeight(screenToDisplayOn.getBounds().getHeight());
            this.informationStage.setScene(scene);
            this.informationStage.setAlwaysOnTop(true);

            this.informationStage.setOnCloseRequest(event -> this.informationPane.stop());
            this.informationStage.setOnShowing(event -> this.informationPane.start());
        }
    }

    /**
     * Set the management of {@link KeyEvent} fired within the screens and browsers displayed. This allows to define the
     * communication of events between the {@link #slideshowPane} and the {@link #informationPane} meaning that when a
     * key is pressed in one or other of these screens, a {@link KeyEvent} is fired to the other one in order display
     * the correct slide in each {@link com.twasyl.slideshowfx.controls.PresentationBrowser}.
     */
    private final void initializeKeyEvents() {
        EventHandler<KeyEvent> handler = event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) {
                if (this.onCloseAction != null) this.onCloseAction.run();

                this.slideshowStage.close();
                if (this.informationStage != null) this.informationStage.close();
            } else if (this.informationPane != null && !DO_NOT_CONSIDER_EVENT_TEXT.equals(event.getText())) {
                final KeyEvent copiedEvent = this.copyEventWithNewText(event, DO_NOT_CONSIDER_EVENT_TEXT);

                final boolean sendToInformation = event.getSource() == this.slideshowPane.getBrowser().getInternalBrowser()
                        || event.getSource() == this.informationPane.getScene();
                final boolean sendToPresentation = event.getSource() == this.informationPane.getScene();

                if (sendToInformation) {
                    this.informationPane.getCurrentSlideBrowser().setInteractionAllowed(true);
                    this.informationPane.getCurrentSlideBrowser().getInternalBrowser().fireEvent(copiedEvent);
                    this.informationPane.getCurrentSlideBrowser().setInteractionAllowed(false);

                    final String currentSlideId = this.slideshowPane.getBrowser().getCurrentSlideId();
                    final Slide firstSlide = this.context.getPresentation().getConfiguration().getFirstSlide();
                    final Slide lastSlide = this.context.getPresentation().getConfiguration().getLastSlide();

                    final boolean onFirstSlide = Objects.equals(currentSlideId, firstSlide.getId());
                    final boolean onLastSlide = Objects.equals(currentSlideId, lastSlide.getId());

                    if (!onFirstSlide && !onLastSlide) {
                        this.informationPane.getNextSlideBrowser().setInteractionAllowed(true);
                        this.informationPane.getNextSlideBrowser().getInternalBrowser().fireEvent(copiedEvent);
                        this.informationPane.getNextSlideBrowser().setInteractionAllowed(false);
                    }
                }

                if (sendToPresentation) this.slideshowPane.getBrowser().getInternalBrowser().fireEvent(copiedEvent);
            }
        };

        this.slideshowPane.getBrowser().getInternalBrowser().addEventHandler(KeyEvent.KEY_PRESSED, handler);
        if (this.informationPane != null)
            this.informationPane.getScene().addEventHandler(KeyEvent.KEY_PRESSED, handler);
    }

    /**
     * Copy a given {@code event} and set its text with a given {@code newText}. All other parameters of the original
     * event are kept.
     *
     * @param event   The event to copy.
     * @param newText The new text of the event.
     * @return A copy of the original event with a new text.
     */
    private KeyEvent copyEventWithNewText(final KeyEvent event, final String newText) {
        final KeyEvent copy = new KeyEvent(event.getSource(), event.getTarget(), event.getEventType(),
                event.getCharacter(), newText, event.getCode(), event.isShiftDown(), event.isControlDown(),
                event.isAltDown(), event.isMetaDown());

        return copy;
    }

    /**
     * Displays the slideshow and the information screen if it was previously created.
     */
    public void show() {
        if (this.slideshowStage != null) {
            this.slideshowPane.getBrowser().loadPresentationAndDo(this.context.getPresentation(), () -> {
                this.slideshowPane.getBrowser().slide(this.context.getStartAtSlideId());

                if (this.informationPane != null) {
                    this.informationPane.getCurrentSlideBrowser().loadPresentationAndDo(this.context.getPresentation(), () -> {
                        this.informationPane.getCurrentSlideBrowser().slide(this.context.getStartAtSlideId());

                        this.informationPane.getNextSlideBrowser().loadPresentationAndDo(this.context.getPresentation(), () -> {
                            final Slide nextSlide;

                            if (this.context.getStartAtSlideId() == null) {
                                final Slide currentSlide = this.context.getPresentation().getConfiguration().getSlideById(this.slideshowPane.getBrowser().getCurrentSlideId());
                                nextSlide = this.context.getPresentation().getConfiguration().getSlideAfter(currentSlide.getSlideNumber());
                            } else {
                                final Slide startingSlide = this.context.getPresentation().getConfiguration().getSlideById(this.context.getStartAtSlideId());
                                nextSlide = this.context.getPresentation().getConfiguration().getSlideAfter(startingSlide.getSlideNumber());
                            }

                            if (nextSlide != null) {
                                this.informationPane.getNextSlideBrowser().slide(nextSlide.getId());
                            }
                        });
                    });
                }
            });
            this.slideshowStage.show();
        }

        if (this.informationStage != null) this.informationStage.show();
    }

    /**
     * Defines the process that is executed when the stage is closed. The action is given as a {@link Runnable} object
     * but a new thread will not be created for running it. The type is just for having an interface which can describe
     * a process. The {@link Runnable#run()} method is called directly.
     *
     * @param action The action to perform when the stage is closed.
     */
    public void onClose(Runnable action) {
        this.onCloseAction = action;
    }

    /**
     * Get the ID of the displayed slide.
     *
     * @return The ID of the current slide or {@code null} if no slide is considered displayed.
     */
    public String getDisplayedSlideId() {
        if (this.slideshowStage != null) {
            return this.slideshowPane.getBrowser().getCurrentSlideId();
        }
        return null;
    }
}
