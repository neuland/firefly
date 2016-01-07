package de.neuland.firefly.changes.v1;

import de.neuland.firefly.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class XMLChangeListTest {
    @Test
    public void shouldParseXmlFile() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeList.xml";
        // when
        XMLChangeList changeList = TestUtils.loadXML(XMLChangeList.class, xmlFile);
        // then
        assertNotNull(changeList);
        assertEquals(2, changeList.getChangeReferences().size());
        assertEquals("change-1.xml", changeList.getChangeReferences().get(0).getFile());
        assertEquals("change-2.xml", changeList.getChangeReferences().get(1).getFile());
    }

    @Test
    public void shouldOverwriteToString() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeList.xml";
        // when
        XMLChangeList changeList = TestUtils.loadXML(XMLChangeList.class, xmlFile);
        // then
        assertTrue(changeList.toString().contains("changeReferences"));
    }
}
