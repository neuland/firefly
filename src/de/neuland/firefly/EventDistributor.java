package de.neuland.firefly;

import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.util.WeakHashSet;

import java.util.HashMap;
import java.util.Set;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;


public abstract class EventDistributor<T extends AbstractEvent, L> extends AbstractEventListener<T> {
    private HashMap<String, Set<L>> registeredListener = new HashMap<>();

    public EventDistributor(ClusterService clusterService,
                            TenantService tenantService) {
        setClusterService(clusterService);
        setTenantService(tenantService);
    }

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

    public void unregisterListener(L listener) {
        for (Set<L> listeners : registeredListener.values()) {
            listeners.remove(listener);
        }
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

    // overridden to neutralize @Required annotation of method id super class
    @Override
    public void setClusterService(ClusterService clusterService) {
        super.setClusterService(clusterService);
    }

    // overridden to neutralize @Required annotation of method id super class
    @Override
    public void setTenantService(TenantService tenantService) {
        super.setTenantService(tenantService);
    }
}
