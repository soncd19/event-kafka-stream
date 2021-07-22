package com.vnpay.event.security;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by SonCD on 21/05/2021
 */
public class VNEventHashKey {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SECRET_KEY = "b20fbb8de534415f9e48fe0302c08d2b";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String calculateHMAC(String data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        return new String(Base64.encode(mac.doFinal(data.getBytes())));
    }

    public static long bitwise(String data) {
        long hashcode = data.hashCode();
        return hashcode << 23 | hashcode >> 23 | 1024 << 10;
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
        String phone = "0987604546";
        System.out.println(VNEventHashKey.calculateHMAC(phone));
    }

}
