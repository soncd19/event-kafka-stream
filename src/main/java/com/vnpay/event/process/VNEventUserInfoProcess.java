package com.vnpay.event.process;

import com.vnpay.event.constant.VNEventKeyConstant;
import com.vnpay.event.redis.VNEventJedisUtil;
import com.vnpay.event.security.VNEventSecurity;
import com.vnpay.event.util.VNEventBuildResourceConfig;
import com.vnpay.event.util.VNEventStringUtils;
import org.apache.kafka.streams.KeyValue;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by SonCD on 24/05/2021
 */
public class VNEventUserInfoProcess {
    private static final Logger logger = LoggerFactory.getLogger(VNEventUserInfoProcess.class);
    private static VNEventJedisUtil vnEventJedisUtil;

    static {
        try {
            vnEventJedisUtil = VNEventBuildResourceConfig.parserXMLJedis();
        } catch (Exception e) {
            logger.error("VNEventUserInfoProcess: cannot create jedis pool: " + e);
        }
    }

    public static KeyValue<String, JSONObject> process(String key, JSONObject value) throws Exception {
        JSONObject jsonHeader = value.getJSONObject(VNEventKeyConstant.DATA)
                .getJSONObject(VNEventKeyConstant.HEADER);
        String phone = jsonHeader.getString(VNEventKeyConstant.PHONE);
        String bankCode = String.valueOf(jsonHeader.get(VNEventKeyConstant.BANK_CODE));
        String userId = VNEventSecurity.hashKey(phone);
        if (userId == null) {
            return new KeyValue<>(key, null);
        }
        String phoneEncrypt = VNEventSecurity.encryptKey(phone);
        JSONObject jsonUserInfo = new JSONObject();
        if (vnEventJedisUtil.exists(userId)) {
            List<String> bankCodes = vnEventJedisUtil.getList(userId);
            if (bankCodes != null && bankCodes.contains(bankCode)) {
                return new KeyValue<>(key, null);
            }
        }
        phone = VNEventStringUtils.maskString(phone, 3, 7, "*");
        jsonUserInfo.put("phone", phone);
        jsonUserInfo.put("bankCode", bankCode);
        jsonUserInfo.put("userId", userId);
        jsonUserInfo.put("phoneEncrypt", phoneEncrypt);
        jsonUserInfo.put("createDate", LocalDateTime.now().toString());
        vnEventJedisUtil.listAdd(userId, bankCode);

        return new KeyValue<>(key, jsonUserInfo);
    }

    public static boolean filterEvent(JSONObject jsonObject) {
        try {
            String event = jsonObject.getJSONObject(VNEventKeyConstant.DATA)
                    .getJSONArray(VNEventKeyConstant.EVENT)
                    .getJSONObject(0)
                    .getString(VNEventKeyConstant.EVENT);
            return event.toLowerCase().startsWith("login");
        } catch (Exception e) {
            return false;
        }
    }

}
