package de.neuland.firefly;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class SystemUpdatedEventTest {
    private static final String TENANT_ID = "myTenantId";

    @Test
    public void shouldShowTenantId() throws Exception {
        // when
        HybrisAdapter.SystemUpdatedEvent event = new HybrisAdapter.SystemUpdatedEvent(TENANT_ID);
        // then
        assertTrue(event.toString().contains(TENANT_ID));
    }
}
