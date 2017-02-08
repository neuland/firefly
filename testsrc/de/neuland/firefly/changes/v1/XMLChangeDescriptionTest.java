package de.neuland.firefly.changes.v1;

import de.neuland.firefly.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class XMLChangeDescriptionTest {
    @Test
    public void shouldParseChangeList() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription.xml";
        // when
        XMLChangeDescription changeDescription = TestUtils.loadXML(XMLChangeDescription.class, xmlFile);
        // then
        assertEquals(4, changeDescription.getChanges().size());
        assertEquals(XMLBeanShell.class, changeDescription.getChanges().get(0).getClass());
        assertEquals(XMLGroovy.class, changeDescription.getChanges().get(1).getClass());
        assertEquals(XMLImpEx.class, changeDescription.getChanges().get(2).getClass());
        assertEquals(XMLSql.class, changeDescription.getChanges().get(3).getClass());
    }

    @Test
    public void shouldParsePreconditions() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription-details.xml";
        // when
        XMLChangeDescription changeDescription = TestUtils.loadXML(XMLChangeDescription.class, xmlFile);
        // then
        assertEquals(1, changeDescription.getPreconditions().size());
    }

    @Test
    public void shouldOverwriteToString() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription.xml";
        // when
        XMLChangeDescription changeDescription = TestUtils.loadXML(XMLChangeDescription.class, xmlFile);
        // then
        assertTrue(changeDescription.toString().contains("changes"));
    }
}
