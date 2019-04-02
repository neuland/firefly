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
public class HmcResetEventListenerTest {
    private static final String TENANT_ID = "myTestTenant";
    private HmcResetEventListener listener;
    @Mock private FireflyExtension extension;
    @Mock private HybrisAdapter.HmcResetEvent hmcResetEvent;
    @Mock private ClusterService clusterService;
    @Mock private TenantService tenantService;

    @Before
    public void setUp() throws Exception {
        listener = new HmcResetEventListener(clusterService, tenantService);
        given(hmcResetEvent.getTenantId()).willReturn(TENANT_ID);
    }

    @Test
    public void shouldCallAllListenersForTenant() throws Exception {
        // given
        listener.registerListener(TENANT_ID, extension);
        // when
        listener.onEvent(hmcResetEvent);
        // then
        verify(extension).onHmcReset(any(PK.class));
    }

    @Test
    public void shouldNotCallListenersForOtherTenants() throws Exception {
        // given
        listener.registerListener("otherTenantId", extension);
        // when
        listener.onEvent(hmcResetEvent);
        // then
        verify(extension, never()).onHmcReset(any(PK.class));
    }
}
