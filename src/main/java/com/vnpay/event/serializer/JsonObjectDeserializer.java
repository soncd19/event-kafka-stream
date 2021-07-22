package com.vnpay.event.serializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by SonCD on 09/04/2021
 */
public class JsonObjectDeserializer implements Deserializer<JSONObject> {

    private static final Logger logger = Logger.getLogger(JsonObjectDeserializer.class);
    private String encoding = "UTF8";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null)
            encodingValue = configs.get("deserializer.encoding");
        if (encodingValue instanceof String)
            encoding = (String) encodingValue;
    }

    @Override
    public JSONObject deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        JSONObject jsonObject;
        try {
            String data = new String(bytes, encoding);
            jsonObject = new JSONObject(data);
        } catch (Exception e) {
            logger.error("VNPapOTT: JsonObjectDeserializer not deserializer: " + e);
            return null;
        }
        return jsonObject;
    }

    @Override
    public void close() {

    }
}
