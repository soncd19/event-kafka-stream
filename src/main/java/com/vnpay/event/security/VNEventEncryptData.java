package com.vnpay.event.security;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * Created by SonCD on 21/05/2021
 */
public class VNEventEncryptData {

    public static String encrypt(String data, PublicKey pk) throws BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        return new String(Base64.encode(cipher.doFinal(data.getBytes())));
    }
}
