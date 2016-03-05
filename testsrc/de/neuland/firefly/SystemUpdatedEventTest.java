package de.neuland.firefly;

import de.hybris.platform.core.PK;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class SystemUpdatedEventTest {
    private static final String TENANT_ID = "myTenantId";

    @Test
    public void shouldShowTenantId() throws Exception {
        // when
        HybrisAdapter.SystemUpdatedEvent event = new HybrisAdapter.SystemUpdatedEvent(TENANT_ID, PK.NULL_PK);
        // then
        assertTrue(event.toString().contains(TENANT_ID));
    }
}
