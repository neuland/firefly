package de.neuland.firefly.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
    public static String generateMD5(String baseValue) {
        return generateMD5(baseValue, "UTF-8", "MD5");
    }

    static String generateMD5(String baseValue, String encoding, String algorithm) {
        try {
            return new BigInteger(1, MessageDigest.getInstance(algorithm).digest(baseValue.getBytes(encoding))).toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
