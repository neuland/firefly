package de.neuland.firefly;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.jalo.HMCManager;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.util.localization.TypeLocalization;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("tenant")
public class HybrisAdapter {
    @Autowired EventService eventService;

    public void updateSystem() throws Exception {
        Initialization.updateSystem();
        eventService.publishEvent(new SystemUpdatedEvent(getTenantId()));
    }

    public void clearHmcConfiguration() {
        HMCManager.getInstance().clearHMCStructure();
        TypeLocalization.getInstance().localizeTypes();
        eventService.publishEvent(new HmcResetEvent(getTenantId()));
    }

    public String getTenantId() {
        return Registry.getCurrentTenant().getTenantID();
    }

    /**
     * This event is triggered after a system update is done.
     */
    public static class SystemUpdatedEvent extends TenantEvent {

        SystemUpdatedEvent(String tenantId) {
            super(tenantId);
        }
    }

    /**
     * This event is triggered after a hMC reset is done.
     */
    public static class HmcResetEvent extends TenantEvent {

        HmcResetEvent(String tenantId) {
            super(tenantId);
        }
    }

    public static class TenantEvent extends AbstractEvent {

        protected String tenantId;
        TenantEvent(String tenantId) {
            this.tenantId = tenantId;
            // This event can only be triggered from der HybrisAdapter.
        }

        public String getTenantId() {
            return tenantId;
        }

        @Override public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }
}
