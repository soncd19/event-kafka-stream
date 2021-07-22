package com.vnpay.event.serializer;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by SonCD on 09/04/2021
 */
public class JsonObjectSerializer implements Serializer<JSONObject> {
    private String encoding = "UTF8";

    public JsonObjectSerializer() {

    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null)
            encodingValue = configs.get("serializer.encoding");
        if (encodingValue instanceof String)
            encoding = (String) encodingValue;
    }

    @Override
    public byte[] serialize(String topic, JSONObject data) {
        if (data == null)
            return null;

        try {
            return data.toString().getBytes(encoding);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {

    }
}
