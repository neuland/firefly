package de.neuland.firefly.utils;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static de.neuland.firefly.TestUtils.unwrapException;
import static org.junit.Assert.assertEquals;


public class MD5UtilTest {
    @Test
    public void shouldGenerateForString() throws Exception {
        // when
        String hash = MD5Util.generateMD5("<items><itemtypes/></items>");
        // then
        assertEquals("5b4422df25563a6e8cd98a6a1327aaa4", hash);
    }

    @Test(expected = UnsupportedEncodingException.class)
    public void shouldHandleUnsupportedEncodingException() throws Throwable {
        try {
            // when
            MD5Util.generateMD5("someValue", "myEncoding", "MD5");
        } catch (RuntimeException e) {
            // then
            unwrapException(e);
        }
    }

    @Test(expected = NoSuchAlgorithmException.class)
    public void shouldHandleNoSuchAlgorithmException() throws Throwable {
        try {
            // when
            MD5Util.generateMD5("someValue", "UTF-8", "myAlgorithm");
        } catch (RuntimeException e) {
            // then
            unwrapException(e);
        }
    }
}
