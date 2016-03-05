package de.neuland.firefly.extensionfinder;

import de.hybris.platform.core.PK;
import de.neuland.firefly.HybrisAdapter;
import de.neuland.firefly.model.FireflyMigrationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FireflySystemTest {
    public static final PK MIGRATION_PK = PK.fromLong(4711);
    @Mock private FireflyExtension extensionAlice;
    @Mock private FireflyExtension extensionBob;
    @Mock private FireflyMigrationModel migration;
    @Mock private HybrisAdapter hybrisAdapter;
    private FireflySystem fireflySystem;

    @Before
    public void setUp() throws Exception {
        given(migration.getPk()).willReturn(MIGRATION_PK);
        given(extensionAlice.getName()).willReturn("Alice");
        given(extensionBob.getName()).willReturn("Bob");
        fireflySystem = new FireflySystem(hybrisAdapter, Arrays.asList(extensionAlice, extensionBob));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullValueForExtensions() throws Exception {
        // when
        new FireflySystem(hybrisAdapter, null);
    }

    @Test
    public void shouldNotTriggerUpdate() throws Exception {
        // given
        given(extensionAlice.isUpdateRequired()).willReturn(false);
        given(extensionBob.isUpdateRequired()).willReturn(false);
        // when
        fireflySystem.update(migration);
        // then
        verify(hybrisAdapter, never()).updateSystem(MIGRATION_PK);
    }

    @Test
    public void shouldNotTriggerHmcReset() throws Exception {
        // given
        given(extensionAlice.isHmcResetRequired()).willReturn(false);
        given(extensionBob.isHmcResetRequired()).willReturn(false);
        // when
        fireflySystem.update(migration);
        // then
        verify(hybrisAdapter, never()).clearHmcConfiguration(MIGRATION_PK);
    }

    @Test
    public void shouldPerformUpdate() throws Exception {
        // given
        given(extensionAlice.isUpdateRequired()).willReturn(true);
        // when
        fireflySystem.update(migration);
        // then
        verify(hybrisAdapter).updateSystem(MIGRATION_PK);
    }

    @Test
    public void shouldPerformHmcReset() throws Exception {
        // given
        given(extensionAlice.isHmcResetRequired()).willReturn(true);
        // when
        fireflySystem.update(migration);
        // then
        verify(hybrisAdapter).clearHmcConfiguration(MIGRATION_PK);
    }

    @Test
    public void shouldRequireUpdate() throws Exception {
        // given
        given(extensionAlice.isUpdateRequired()).willReturn(true);
        // when
        boolean updateRequired = fireflySystem.isUpdateRequired();
        // then
        assertTrue(updateRequired);
    }

    @Test
    public void shouldRequireHmcReset() throws Exception {
        // given
        given(extensionAlice.isHmcResetRequired()).willReturn(true);
        // when
        boolean resetRequired = fireflySystem.isHmcResetRequired();
        // then
        assertTrue(resetRequired);
    }

    @Test
    public void shouldNotRequireUpdate() throws Exception {
        // given
        given(extensionAlice.isUpdateRequired()).willReturn(false);
        given(extensionBob.isUpdateRequired()).willReturn(false);
        // when
        boolean updateRequired = fireflySystem.isUpdateRequired();
        // then
        assertFalse(updateRequired);
    }

    @Test
    public void shouldNotRequireHmcReset() throws Exception {
        // given
        given(extensionAlice.isHmcResetRequired()).willReturn(false);
        given(extensionBob.isHmcResetRequired()).willReturn(false);
        // when
        boolean resetRequired = fireflySystem.isHmcResetRequired();
        // then
        assertFalse(resetRequired);
    }

    @Test
    public void shouldNotPerformHmcResetAfterUpdate() throws Exception {
        // given
        given(extensionAlice.isUpdateRequired()).willReturn(true);
        given(extensionBob.isHmcResetRequired()).willReturn(true);
        doAnswer(new Answer() {
            // only after update, the hMC reset is not required for Bob
            @Override public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                reset(extensionBob);
                given(extensionBob.isHmcResetRequired()).willReturn(false);
                return null;
            }
        }).when(hybrisAdapter).updateSystem(MIGRATION_PK);
        // when
        fireflySystem.update(migration);
        // then
        verify(hybrisAdapter, never()).clearHmcConfiguration(any(PK.class));
    }

    @Test
    public void shouldGetExtensionPathsForAllExtensions() throws Exception {
        // when
        Map<String, File> result = fireflySystem.getExtensionPaths();
        // then
        assertEquals(2, result.size());
    }

    @Test
    public void shouldGetExtensionPaths() throws Exception {
        // given
        given(extensionAlice.getRootPath()).willReturn(new File("alice"));
        // when
        Map<String, File> result = fireflySystem.getExtensionPaths();
        // then
        assertTrue(result.values().contains(new File("alice")));
    }
}
