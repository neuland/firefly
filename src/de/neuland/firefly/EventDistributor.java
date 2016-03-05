package de.neuland.firefly;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.util.WeakHashSet;

import java.util.HashMap;
import java.util.Set;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;


public abstract class EventDistributor<T extends AbstractEvent, L> extends AbstractEventListener<T> {
    private HashMap<String, Set<L>> registeredListener = new HashMap<>();

    public void registerListener(String tenantId, L listener) {
        hasText(tenantId);
        notNull(listener);
        final Set<L> listeners;
        if (registeredListener.containsKey(tenantId)) {
            listeners = registeredListener.get(tenantId);
        } else {
            listeners = new WeakHashSet();
            registeredListener.put(tenantId, listeners);
        }
        listeners.add(listener);
    }

    public boolean isListenerRegistered(L listener) {
        for (Set<L> listeners : registeredListener.values()) {
            if (listeners.contains(listener)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasListenersForTenant(String tenantId) {
        return registeredListener.containsKey(tenantId);
    }

    protected Set<L> getListenersForTenant(String tenantId) {
        return registeredListener.get(tenantId);
    }
}
