package de.neuland.firefly.changes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ChangeFileFilterTest {
    private ChangeFileFilter filter = new ChangeFileFilter();
    @Mock private File file;

    @Before
    public void setUp() throws Exception {
        when(file.canRead()).thenReturn(true);
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn("something.xml");
    }

    @Test
    public void shouldIgnoreCase() throws Exception {
        // given
        when(file.getName()).thenReturn("something.xML");
        // when
        boolean result = filter.accept(file);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldFilterDirectory() throws Exception {
        // given
        when(file.isFile()).thenReturn(false);
        // when
        boolean result = filter.accept(file);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldFilterRestrictedFiles() throws Exception {
        // given
        when(file.canRead()).thenReturn(false);
        // when
        boolean result = filter.accept(file);
        // then
        assertFalse(result);
    }
}
