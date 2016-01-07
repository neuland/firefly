package de.neuland.firefly.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;


public class XMLUtilTest {
    @Test
    public void shouldRemoveComments() throws Exception {
        // given
        File xmlFile = createXmlFile("<!--comment a--><!--comment b--><items/>");
        // when
        String xml = XMLUtil.loadAndNormalizeXML(xmlFile);
        // then
        assertEquals("<items/>", xml);
    }

    @Test
    public void shouldCloseEmptyTags() throws Exception {
        // given
        File xmlFile = createXmlFile("<items></items>");
        // when
        String xml = XMLUtil.loadAndNormalizeXML(xmlFile);
        // then
        assertEquals("<items/>", xml);
    }

    @Test
    public void shouldRemoveWhiteSpaces() throws Exception {
        // given
        File xmlFile = createXmlFile("<items> <itemtypes/></items>");
        // when
        String xml = XMLUtil.loadAndNormalizeXML(xmlFile);
        // then
        assertEquals("<items><itemtypes/></items>", xml);
    }

    @Test
    public void shouldRemoveLineBreaks() throws Exception {
        // given
        File xmlFile = createXmlFile("<items>\n<itemtypes/>\n\r</items>");
        // when
        String xml = XMLUtil.loadAndNormalizeXML(xmlFile);
        // then
        assertEquals("<items><itemtypes/></items>", xml);
    }

    private File createXmlFile(String content) throws IOException {
        File xmlFile = File.createTempFile("XMLUtilTest", "xml");
        PrintWriter out = new PrintWriter(xmlFile);
        out.append(content);
        out.flush();
        out.close();
        return xmlFile;
    }
}
