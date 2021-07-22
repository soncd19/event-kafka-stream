package com.vnpay.event.decrypt;

import com.vnpay.event.entity.VNEventData;
import org.bouncycastle.util.encoders.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by SonCD on 14/04/2021
 */
public class VNEventDecrypt {

    public static String decryptData(VNEventData vnEventData) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, Exception {
        byte[] encryptByteKey = Base64.decode(vnEventData.getKey());
        byte[] IV = Arrays.copyOfRange(encryptByteKey, 0, 16);
        byte[] dataByteK = Arrays.copyOfRange(encryptByteKey, 16, encryptByteKey.length);
        String dynamicKey = VNEventAES256Service.decrypt(vnEventData.getLicense().getAesKey(), dataByteK, IV);
        byte[] allEncryptByte = Base64.decode(vnEventData.getData());
        IV = Arrays.copyOfRange(allEncryptByte, 0, 16);
        byte[] dataByte = Arrays.copyOfRange(allEncryptByte, 16, allEncryptByte.length);
        return VNEventAES256Service.decrypt(dynamicKey, dataByte, IV);
    }
}
