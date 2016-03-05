package de.neuland.firefly.changes;

import de.neuland.firefly.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URL;

import static de.neuland.firefly.TestUtils.getFileFromClasspath;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class ChangeFactoryTest {
    private ChangeFactory changeFactory = new ChangeFactory();
    private File changeList = getFileFromClasspath("resources/changes/v1/changeList.xml");
    private File changeFile = getFileFromClasspath("resources/changes/v1/changeDescription.xml");

    @Test
    public void shouldIdentifyChangeListFile() throws Exception {
        // when
        boolean result = changeFactory.isChangeList(changeList);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldIdentifyChangeFile() throws Exception {
        // when
        boolean result = changeFactory.isChangeList(changeFile);
        // then
        assertFalse(result);
    }
}
