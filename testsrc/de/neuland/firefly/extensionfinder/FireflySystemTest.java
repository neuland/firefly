package de.neuland.firefly.extensionfinder;

import de.neuland.firefly.HybrisAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FireflySystemTest {
    @Mock private FireflyExtension extensionAlice;
    @Mock private FireflyExtension extensionBob;
    @Mock private HybrisAdapter hybrisAdapter;
    private FireflySystem fireflySystem;

    @Before
    public void setUp() throws Exception {
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
        fireflySystem.update();
        // then
        verify(hybrisAdapter, never()).updateSystem();
    }

    @Test
    public void shouldNotTriggerHmcReset() throws Exception {
        // given
        given(extensionAlice.isHmcResetRequired()).willReturn(false);
        given(extensionBob.isHmcResetRequired()).willReturn(false);
        // when
        fireflySystem.update();
        // then
        verify(hybrisAdapter, never()).clearHmcConfiguration();
    }

    @Test
    public void shouldPerformUpdate() throws Exception {
        // given
        given(extensionAlice.isUpdateRequired()).willReturn(true);
        // when
        fireflySystem.update();
        // then
        verify(hybrisAdapter).updateSystem();
    }

    @Test
    public void shouldPerformHmcReset() throws Exception {
        // given
        given(extensionAlice.isHmcResetRequired()).willReturn(true);
        // when
        fireflySystem.update();
        // then
        verify(hybrisAdapter).clearHmcConfiguration();
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
        }).when(hybrisAdapter).updateSystem();
        // when
        fireflySystem.update();
        // then
        verify(hybrisAdapter, never()).clearHmcConfiguration();
    }
}
