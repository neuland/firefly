package de.neuland.firefly.changes;

import de.hybris.platform.core.PK;
import de.neuland.firefly.TenantEvent;

/**
 * This event is triggered after a change is executed.
 */
public class ChangeExecutedEvent extends TenantEvent {
    private Change change;
    private PK migration;
    ChangeExecutedEvent(String tenantId, Change change, PK migration) {
        super(tenantId);
        this.change = change;
        this.migration = migration;
    }

    public Change getChange() {
        return change;
    }

    public PK getMigration() {
        return migration;
    }
}
