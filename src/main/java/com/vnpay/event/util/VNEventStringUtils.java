package com.vnpay.event.util;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by SonCD on 21/05/2021
 */
public class VNEventStringUtils extends StringUtils {

    private static final String CHARSET_NAME = "UTF-8";

    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            if (object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String maskString(String strText, int start, int end, String maskChar)
            throws Exception {

        if (strText == null || strText.equals("")) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }

        if (end > strText.length()) {
            end = strText.length();
        }

        if (start > end) {
            throw new Exception("VNEvent: End index cannot be greater than start index");
        }

        int maskLength = end - start;

        if (maskLength == 0) {
            return strText;
        }

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }
        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }
}
