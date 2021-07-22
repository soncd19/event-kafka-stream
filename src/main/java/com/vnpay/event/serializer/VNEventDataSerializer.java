package com.vnpay.event.serializer;

import com.vnpay.event.entity.VNEventData;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by SonCD on 09/04/2021
 */
public class VNEventDataSerializer implements Serializer<VNEventData> {

    public VNEventDataSerializer() {

    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, VNEventData data) {
        if (data == null)
            return null;

        try {
            return data.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializationException("VNEvent: Error serializing VNEventData", e);
        }
    }

    @Override
    public void close() {

    }
}
