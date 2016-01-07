package de.neuland.firefly;

import de.neuland.firefly.utils.XMLUtil;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertNotNull;


public class TestUtils {
    public static void unwrapException(RuntimeException e) throws Throwable {
        if (e.getCause() != null) {
            throw e.getCause();
        } else {
            throw e;
        }
    }

    public static <T> T loadXML(Class<T> type, String xmlFile) {
        URL currentClassPathFolder = TestUtils.class.getClassLoader().getResource("");
        T result = XMLUtil.loadXML(type, new File(currentClassPathFolder.getFile(), xmlFile));
        assertNotNull(result);
        return result;
    }
}
