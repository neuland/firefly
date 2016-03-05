package de.neuland.firefly;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import org.apache.commons.lang.builder.ToStringBuilder;


public abstract class TenantEvent extends AbstractEvent {
    protected String tenantId;

    protected TenantEvent(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    @Override public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
