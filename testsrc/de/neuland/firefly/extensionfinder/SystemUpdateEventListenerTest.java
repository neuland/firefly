package de.neuland.firefly.extensionfinder;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.neuland.firefly.HybrisAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SystemUpdateEventListenerTest {
    private static final String TENANT_ID = "myTestTenant";
    private static final PK MIGRATION = PK.NULL_PK;
    private SystemUpdateEventListener listener;
    @Mock private FireflyExtension extension;
    @Mock private HybrisAdapter.SystemUpdatedEvent systemUpdatedEvent;
    @Mock private ClusterService clusterService;
    @Mock private TenantService tenantService;

    @Before
    public void setUp() throws Exception {
        listener = new SystemUpdateEventListener(clusterService, tenantService);
        given(systemUpdatedEvent.getTenantId()).willReturn(TENANT_ID);
        given(systemUpdatedEvent.getMigration()).willReturn(MIGRATION);
    }

    @Test
    public void shouldCallAllListenersForTenant() throws Exception {
        // given
        listener.registerListener(TENANT_ID, extension);
        // when
        listener.onEvent(systemUpdatedEvent);
        // then
        verify(extension).onUpdate(MIGRATION);
    }

    @Test
    public void shouldNotCallListenersForOtherTenants() throws Exception {
        // given
        listener.registerListener("otherTenantId", extension);
        // when
        listener.onEvent(systemUpdatedEvent);
        // then
        verify(extension, never()).onUpdate(any(PK.class));
    }
}
