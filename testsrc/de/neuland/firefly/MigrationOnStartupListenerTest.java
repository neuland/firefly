package de.neuland.firefly;

import de.neuland.firefly.web.ApplicationStartupEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class MigrationOnStartupListenerTest {
    @Mock FireflyService fireflyService;

    @Test
    public void shouldStartAutomaticMigration() throws Exception {
        // given
        MigrationOnStartupListener listener = new MigrationOnStartupListener(fireflyService, true);
        // when
        listener.onEvent(new ApplicationStartupEvent());
        // then
        verify(fireflyService).migrate();
    }

    @Test
    public void shouldRunSimulation() throws Exception {
        // given
        MigrationOnStartupListener listener = new MigrationOnStartupListener(fireflyService, false);
        // when
        listener.onEvent(new ApplicationStartupEvent());
        // then
        verify(fireflyService).simulate();
    }
}
