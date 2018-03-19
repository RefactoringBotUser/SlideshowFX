package com.twasyl.slideshowfx.services;

import com.twasyl.slideshowfx.concurrent.SavePresentationTask;
import com.twasyl.slideshowfx.engine.presentation.PresentationEngine;
import com.twasyl.slideshowfx.global.configuration.GlobalConfiguration;
import com.twasyl.slideshowfx.utils.PlatformHelper;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An implementation of a {@link ScheduledService} that saves regularly the {@link PresentationEngine presentation}
 * registered to it.
 * @author Thierry Wasylczenko
 * @version 1.0.0
 * @since SlideshowFX 1.0
 */
public class AutoSavingService extends ScheduledService<Void> {

    private static final Set<AutoSavingService> runningServices = new HashSet<>();

    private final PresentationEngine presentation;

    /**
     * Creates a service for auto saving a presentation. The {@link ScheduledService#delay delay} and
     * {@link ScheduledService#period period} are automatically set according the
     * {@link GlobalConfiguration#getAutoSavingInterval()} if it is not {@code null}.
     * @param presentation The presentation to auto save.
     */
    public AutoSavingService(PresentationEngine presentation) {
        this.setRestartOnFailure(true);
        this.presentation = presentation;

        final Long interval = GlobalConfiguration.getAutoSavingInterval();
        if(interval != null) this.setDelayAndPeriod(interval);

        runningServices.add(this);
    }

    @Override
    protected Task<Void> createTask() {
        final SavePresentationTask task = new SavePresentationTask(this.presentation);
        return task;
    }

    @Override
    protected void executeTask(Task<Void> task) {
        super.executeTask(task);
    }

    /**
     * Set the delay and period to this service.
     * @param delayInMinutes The delay in minutes.
     */
    protected void setDelayAndPeriod(final long delayInMinutes) {
        this.setDelay(Duration.minutes(delayInMinutes));
        this.setPeriod(Duration.minutes(delayInMinutes));
    }

    /**
     * Resume all services that have been cancelled.
     */
    public static void resumeAll() {
        runningServices.forEach(service -> PlatformHelper.run(() -> service.restart()));
    }

    /**
     * Cancel all services that are registered.
     */
    public static void cancelAll() {
        final Iterator<AutoSavingService> iterator = runningServices.iterator();

        while(iterator.hasNext()) {
            iterator.next().cancel();
            iterator.remove();
        }
    }

    /**
     * Cancel the service for a given presentation.
     * @param presentation The presentation to cancel the auto saving service for.
     */
    public static void cancelFor(final PresentationEngine presentation) {
        if(presentation != null) {
            final long id = presentation.getConfiguration().getId();
            runningServices.stream()
                    .filter(service -> service.presentation.getConfiguration().getId() == id)
                    .findFirst()
                    .ifPresent(service -> {
                        service.cancel();
                        runningServices.remove(service);
                    });
        }
    };

    /**
     * Change the delay and the period for all {@link AutoSavingService services}. Each service is then restarted.
     * @param delayInMinutes The new delay in minutes.
     */
    public static void setDelayForAllServices(final long delayInMinutes) {
        runningServices.forEach(service -> {
            service.setDelayAndPeriod(delayInMinutes);
            service.restart();
        });
    }
}