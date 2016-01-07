package de.neuland.firefly.web;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;


public class ApplicationStartupEvent extends AbstractEvent {
    @Override public String toString() {
        return getClass().getSimpleName();
    }
}
