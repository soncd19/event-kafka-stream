package com.vnpay.event.decrypt;

/**
 * @author quangtt
 * @modify by SonCD
 */


import java.nio.charset.Charset;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * https://www.owasp.org/index.php/Using_the_Java_Cryptographic_Extensions
 */
public class VNEventAES256Service {

    public static String decrypt(String strSecretKey, byte[] textByte, byte[] iv) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] encodedKey = strSecretKey.getBytes();
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        Cipher aesCipherForDecryption = Cipher.getInstance("AES/CBC/PKCS7Padding");
        aesCipherForDecryption.init(2, secretKey, new IvParameterSpec(iv));
        byte[] byteCipherText = aesCipherForDecryption.doFinal(textByte);
        return new String(byteCipherText, Charset.defaultCharset());
    }
}