package de.neuland.firefly;

import de.neuland.firefly.utils.XMLUtil;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class TestUtils {
    public static void unwrapException(RuntimeException e) throws Throwable {
        if (e.getCause() != null) {
            throw e.getCause();
        } else {
            throw e;
        }
    }

    public static <T> T loadXML(Class<T> type, String xmlFile) {
        T result = XMLUtil.loadXML(type, getFileFromClasspath(xmlFile));
        assertNotNull(result);
        return result;
    }

    public static File getFileFromClasspath(String filename) {
        URL currentClassPathFolder = TestUtils.class.getClassLoader().getResource("");
        File result = new File(currentClassPathFolder.getFile(), filename);
        assertTrue(result.exists());
        return result;
    }
}
