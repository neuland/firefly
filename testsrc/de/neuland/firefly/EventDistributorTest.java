package de.neuland.firefly;

import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.neuland.firefly.extensionfinder.FireflyExtension;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class EventDistributorTest {
    private static final String TENANT_ID = "myTestTenant";

    @Mock private ClusterService clusterService;
    @Mock private TenantService tenantService;

    private EventDistributor listener = new EventDistributor(clusterService, tenantService) {
        @Override protected void onEvent(AbstractEvent event) {
            // NO OP
        }
    };
    @Mock private FireflyExtension extension;
    @Mock private AbstractEvent event;

    @Test
    public void shouldNotFindRandomListenerAsRegistered() throws Exception {
        // given
        listener.registerListener(TENANT_ID, Mockito.mock(FireflyExtension.class));
        // when
        boolean isRegistered = listener.isListenerRegistered(extension);
        // then
        assertFalse(isRegistered);
    }

    @Test
    public void shouldRegisterMultipleListeners() throws Exception {
        // given
        FireflyExtension extension1 = Mockito.mock(FireflyExtension.class);
        FireflyExtension extension2 = Mockito.mock(FireflyExtension.class);
        // when
        listener.registerListener(TENANT_ID, extension2);
        listener.registerListener(TENANT_ID, extension1);
        // then
        assertTrue(listener.isListenerRegistered(extension1));
        assertTrue(listener.isListenerRegistered(extension2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowListenerRegistrationWithoutTenant() throws Exception {
        // when
        listener.registerListener(null, extension);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullListeners() throws Exception {
        // when
        listener.registerListener(TENANT_ID, null);
    }
}
