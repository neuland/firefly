package de.neuland.firefly.extensionfinder;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;
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
    private EventDistributor listener = new EventDistributor() {
        @Override protected void onEvent(AbstractEvent event) {
            // NO OP
        }

        @Override public void onApplicationEvent(ApplicationEvent applicationEvent) {
            // NO OP
        }
    };
    @Mock private FireflyExtension extension;
    @Mock private AbstractEvent event;

    @Test
    public void shouldNotFindRandomListenerAsRegistered() throws Exception {
        // given
        listener.registerFireflyExtension(TENANT_ID, Mockito.mock(FireflyExtension.class));
        // when
        boolean isRegistered = listener.isFireflyExtensionRegistered(extension);
        // then
        assertFalse(isRegistered);
    }

    @Test
    public void shouldRegisterMultipleListeners() throws Exception {
        // given
        FireflyExtension extension1 = Mockito.mock(FireflyExtension.class);
        FireflyExtension extension2 = Mockito.mock(FireflyExtension.class);
        // when
        listener.registerFireflyExtension(TENANT_ID, extension2);
        listener.registerFireflyExtension(TENANT_ID, extension1);
        // then
        assertTrue(listener.isFireflyExtensionRegistered(extension1));
        assertTrue(listener.isFireflyExtensionRegistered(extension2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowListenerRegistrationWithoutTenant() throws Exception {
        // when
        listener.registerFireflyExtension(null, extension);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullListeners() throws Exception {
        // when
        listener.registerFireflyExtension(TENANT_ID, null);
    }
}
