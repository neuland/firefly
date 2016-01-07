package de.neuland.firefly.changes.v1;

import de.neuland.firefly.TestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class XMLChangeTest {
    public static final String FILE = "junit.java";

    @Test
    public void shouldParseChangeAuthor() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription-details.xml";
        // when
        XMLChange change = loadXML(xmlFile, 0);
        // then
        assertEquals("jUnit", change.getAuthor());
    }

    @Test
    public void shouldParseChangeId() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription-details.xml";
        // when
        XMLChange change = loadXML(xmlFile, 0);
        // then
        assertEquals("1", change.getId());
    }

    @Test
    public void shouldParseChangeFileReference() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription-details.xml";
        //
        XMLChange change = loadXML(xmlFile, 0);
        // then
        assertEquals(FILE, change.getFile());
    }

    @Test
    public void shouldParseChangeContent() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription-details.xml";
        // when
        XMLChange change = loadXML(xmlFile, 1);
        // then
        assertEquals("print(ctx.getBeanDefinitionNames());", change.getChangeContent());
    }

    @Test
    public void shouldOverwriteToString() throws Exception {
        // given
        String xmlFile = "resources/changes/v1/changeDescription-details.xml";
        // when
        XMLChange change = loadXML(xmlFile, 0);
        // then
        assertTrue(change.toString().contains(FILE));
    }

    private XMLChange loadXML(String xmlFile, int changeIndex) {
        XMLChangeDescription changeDescription = TestUtils.loadXML(XMLChangeDescription.class, xmlFile);
        assertTrue(changeDescription.getChanges().size() > changeIndex);
        return changeDescription.getChanges().get(changeIndex);
    }
}
