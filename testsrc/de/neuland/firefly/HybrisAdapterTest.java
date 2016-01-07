package de.neuland.firefly;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


@IntegrationTest
public class HybrisAdapterTest extends ServicelayerTransactionalTest {
    @Resource HybrisAdapter hybrisAdapter;
    @Resource(name = "HybrisAdapterTest.SystemUpdatedEventListener") SystemUpdatedEventListener systemUpdatedEventListener;
    @Resource(name = "HybrisAdapterTest.HmcResetEventListener") HmcResetEventListener hmcResetEventListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        systemUpdatedEventListener.resetEventLog();
        hmcResetEventListener.resetEventLog();
    }

    @Test
    public void shouldTriggerUpdateEvent() throws Exception {
        // when
        hybrisAdapter.updateSystem();
        // then
        assertEquals(1, systemUpdatedEventListener.getEventLog().size());
    }

    @Test
    public void shouldTriggerHmcResetEvent() throws Exception {
        // when
        hybrisAdapter.clearHmcConfiguration();
        // then
        assertEquals(1, hmcResetEventListener.getEventLog().size());
    }

    @Component("HybrisAdapterTest.SystemUpdatedEventListener")
    public static class SystemUpdatedEventListener extends TenantEventListener<HybrisAdapter.SystemUpdatedEvent> {
    }

    @Component("HybrisAdapterTest.HmcResetEventListener")
    public static class HmcResetEventListener extends TenantEventListener<HybrisAdapter.HmcResetEvent> {
    }

    public static abstract class TenantEventListener<T extends HybrisAdapter.TenantEvent> extends AbstractEventListener<T> {
        private ThreadLocal<List<T>> eventLog = new ThreadLocal<>();

        @Override protected void onEvent(T event) {
            List<T> eventLog = this.eventLog.get();
            if (eventLog == null) {
                eventLog = new ArrayList<>();
            }
            eventLog.add(event);
            this.eventLog.set(eventLog);
        }

        void resetEventLog() {
            eventLog.set(null);
        }

        List<T> getEventLog() {
            if (eventLog.get() == null) {
                return Collections.emptyList();
            } else {
                return eventLog.get();
            }
        }
    }
}
