package de.neuland.firefly.changes;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ChangeExecutedEventListenerTest {
    private static final String TENANT_ID = "myTestTenant";
    private static final PK MIGRATION = PK.NULL_PK;
    private ChangeExecutedEventListener listener;
    @Mock private Change change;
    @Mock private ChangeExecutedEvent changeExecutedEvent;
    @Mock private ClusterService clusterService;
    @Mock private TenantService tenantService;
    @Mock private ApplicationContext applicationContext;

    @Before
    public void setUp() throws Exception {
        listener = new ChangeExecutedEventListener(
            clusterService,
            tenantService,
            applicationContext
        );
        given(changeExecutedEvent.getTenantId()).willReturn(TENANT_ID);
        given(changeExecutedEvent.getMigration()).willReturn(MIGRATION);
        given(changeExecutedEvent.getChange()).willReturn(change);
    }

    @Test
    public void shouldCallAllListenersForTenant() throws Exception {
        // given
        listener.registerListener(TENANT_ID, change);
        // when
        listener.onEvent(changeExecutedEvent);
        // then
        verify(change).onExecution(MIGRATION);
    }

    @Test
    public void shouldNotCallListenersForOtherChanges() throws Exception {
        // given
        listener.registerListener(TENANT_ID, change);
        Change otherChange = mock(Change.class);
        given(changeExecutedEvent.getChange()).willReturn(otherChange);
        // when
        listener.onEvent(changeExecutedEvent);
        // then
        verify(change, never()).onExecution(MIGRATION);
    }

    @Test
    public void shouldNotCallListenersForOtherTenants() throws Exception {
        // given
        listener.registerListener("otherTenantId", change);
        // when
        listener.onEvent(changeExecutedEvent);
        // then
        verify(change, never()).onExecution(MIGRATION);
    }

}
