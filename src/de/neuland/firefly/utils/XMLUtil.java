package de.neuland.firefly.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;


public class XMLUtil {
    public static String loadAndNormalizeXML(File xmlFile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        removeRecursively(doc);
        doc.normalizeDocument();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter normalizedXml = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(normalizedXml));
        return normalizedXml.toString();
    }

    public static <T> T loadXML(Class<T> type, File sourceFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            return (T) context.createUnmarshaller().unmarshal(sourceFile);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    static void removeRecursively(Node node) {
        if (node == null) {
            return;
        }
        if (isIrrelevantNode(node)) {
            removeRecursively(node.getNextSibling());
            node.getParentNode().removeChild(node);
        } else {
            NodeList childNotes = node.getChildNodes();
            for (int i = 0; i < childNotes.getLength(); i++) {
                removeRecursively(childNotes.item(i));
            }
        }
    }

    private static boolean isIrrelevantNode(Node node) {
        if (COMMENT_NODE == node.getNodeType()) {
            return true;
        }
        return TEXT_NODE == node.getNodeType() && isBlank(node.getTextContent());
    }
}
