package de.neuland.firefly.changes;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;


@IntegrationTest
public class ChangeFactoryIntegrationTest extends ServicelayerTransactionalTest {
    @Resource ChangeFactory changeFactory;

    @Test
    public void shouldAddChange() throws Exception {
        // when
        ChangeList changeList = changeFactory.createChangeList();
        // then
        assertTrue(changeList.contains("firefly/changes/jUnit.xml", "jUnit", "v3-1"));
    }

    @Test
    public void shouldLoadChangeContentFromFile() throws Exception {
        // when
        ChangeList changeList = changeFactory.createChangeList();
        // then
        assertTrue(changeList.contains("firefly/changes/unitTest/noOp.xml", "jUnit", "v3-2"));
    }
}
