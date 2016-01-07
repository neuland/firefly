package de.neuland.firefly.extensionfinder;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.util.WeakHashSet;

import java.util.HashMap;
import java.util.Set;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;


public abstract class EventDistributor<T extends AbstractEvent> extends AbstractEventListener<T> {
    private HashMap<String, Set<FireflyExtension>> registeredExtensions = new HashMap<>();

    public void registerFireflyExtension(String tenantId, FireflyExtension fireflyExtension) {
        hasText(tenantId);
        notNull(fireflyExtension);
        final Set<FireflyExtension> extensions;
        if (registeredExtensions.containsKey(tenantId)) {
            extensions = registeredExtensions.get(tenantId);
        } else {
            extensions = new WeakHashSet();
            registeredExtensions.put(tenantId, extensions);
        }
        extensions.add(fireflyExtension);
    }

    boolean isFireflyExtensionRegistered(FireflyExtension fireflyExtension) {
        for (Set<FireflyExtension> extensions : registeredExtensions.values()) {
            if (extensions.contains(fireflyExtension)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasListenersForTenant(String tenantId) {
        return registeredExtensions.containsKey(tenantId);
    }

    protected Set<FireflyExtension> getListenersForTenant(String tenantId) {
        return registeredExtensions.get(tenantId);
    }
}
