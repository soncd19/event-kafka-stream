package com.vnpay.event.security;

import com.vnpay.event.util.VNEventBuildProperties;
import com.vnpay.event.util.VNEventFileToStringUtils;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by SonCD on 21/05/2021
 */
public class VNEventSecurity {

    private static String pubKey;

    static {

        try {
            String pubKeyPath = VNEventBuildProperties.getInstance().getProperties().getProperty("public.key.path");
            pubKey = VNEventFileToStringUtils.fileToString(pubKeyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String hashKey(String phone) throws Exception {
        if (phone == null || phone.length() < 10) {
            return null;
        }
        return VNEventHashKey.calculateHMAC(phone);
    }

    public static String encryptKey(String phone) throws Exception {
        byte[] arrPk = Base64.decode(pubKey);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(arrPk));
        return VNEventEncryptData.encrypt(phone, pubKey);
    }
}
