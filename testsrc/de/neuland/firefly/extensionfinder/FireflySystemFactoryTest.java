package de.neuland.firefly.extensionfinder;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;


@IntegrationTest
public class FireflySystemFactoryTest extends ServicelayerTransactionalTest {
    @Resource FireflySystemFactory fireflySystemFactory;
    @Resource SystemUpdateEventListener systemUpdateEventListener;
    @Resource HmcResetEventListener hmcResetEventListener;

    @Test
    public void shouldAddExtensionsToSystem() throws Exception {
        // when
        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        // then
        assertNotNull(fireflySystem);
        assertFalse(fireflySystem.getExtensions().isEmpty());
    }

    @Test
    public void shouldFindFireflyExtension() throws Exception {
        // when
        FireflyExtension fireflyExtension = findByName(fireflySystemFactory.createExtensions(), "firefly");
        // then
        assertNotNull(fireflyExtension);
        assertEquals("firefly", fireflyExtension.getRootPath().getName());
    }

    @Test
    public void shouldRegisterFireflyForUpdateEvent() throws Exception {
        // when
        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        // then
        assertTrue(systemUpdateEventListener.isListenerRegistered(fireflySystem.getExtensions().get(0)));
    }

    @Test
    public void shouldRegisterFireflyForResetEvent() throws Exception {
        // when
        FireflySystem fireflySystem = fireflySystemFactory.createFireflySystem();
        // then
        assertTrue(hmcResetEventListener.isListenerRegistered(fireflySystem.getExtensions().get(0)));
    }

    private FireflyExtension findByName(List<FireflyExtension> extensions, String name) {
        for (FireflyExtension extension : extensions) {
            if (name.equals(extension.getName())) {
                return extension;
            }
        }
        return null;
    }
}
