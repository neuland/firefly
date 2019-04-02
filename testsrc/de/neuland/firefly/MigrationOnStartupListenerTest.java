package de.neuland.firefly;

import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.neuland.firefly.web.ApplicationStartupEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class MigrationOnStartupListenerTest {
    @Mock private FireflyService fireflyService;

    @Test
    public void shouldStartAutomaticMigration() {
        // given
        MigrationOnStartupListener listener = new MigrationOnStartupListener(
            fireflyService,
            true);
        // when
        listener.onApplicationEvent(new ApplicationStartupEvent());
        // then
        verify(fireflyService).migrate();
    }

    @Test
    public void shouldRunSimulation() {
        // given
        MigrationOnStartupListener listener = new MigrationOnStartupListener(
            fireflyService,
            false
        );
        // when
        listener.onApplicationEvent(new ApplicationStartupEvent());
        // then
        verify(fireflyService).simulate();
    }
}
