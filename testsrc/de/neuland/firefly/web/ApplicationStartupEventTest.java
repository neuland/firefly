package de.neuland.firefly.web;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class ApplicationStartupEventTest {
    @Test
    public void shouldOverwriteToString() throws Exception {
        // when
        String toString = new ApplicationStartupEvent().toString();
        // then
        assertTrue(toString.contains(ApplicationStartupEvent.class.getSimpleName()));
    }
}
