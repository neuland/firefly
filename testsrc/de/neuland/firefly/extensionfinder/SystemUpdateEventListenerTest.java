package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.HybrisAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SystemUpdateEventListenerTest {
    private static final String TENANT_ID = "myTestTenant";
    private SystemUpdateEventListener listener;
    @Mock private FireflyExtension extension;
    @Mock private HybrisAdapter.SystemUpdatedEvent systemUpdatedEvent;

    @Before
    public void setUp() throws Exception {
        listener = new SystemUpdateEventListener();
        given(systemUpdatedEvent.getTenantId()).willReturn(TENANT_ID);
    }

    @Test
    public void shouldCallAllListenersForTenant() throws Exception {
        // given
        listener.registerFireflyExtension(TENANT_ID, extension);
        // when
        listener.onEvent(systemUpdatedEvent);
        // then
        verify(extension).onUpdate();
    }

    @Test
    public void shouldNotCallListenersForOtherTenants() throws Exception {
        // given
        listener.registerFireflyExtension("otherTenantId", extension);
        // when
        listener.onEvent(systemUpdatedEvent);
        // then
        verify(extension, never()).onUpdate();
    }
}
