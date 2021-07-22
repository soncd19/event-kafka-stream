package com.vnpay.event.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpay.event.entity.VNEventData;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by SonCD on 09/04/2021
 */
public class VNEventDataDeserializer implements Deserializer<VNEventData> {

    private static final Logger logger = LoggerFactory.getLogger(VNEventDataDeserializer.class);
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public VNEventData deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        VNEventData vnEventData;
        try {
            String data = new String(bytes);
            ObjectMapper objectMapper = new ObjectMapper();
            vnEventData = objectMapper.readValue(data, VNEventData.class);
        } catch (Exception e) {
            logger.error("VNEvent: VNEventDataDeserializer not deserializer: "+ e);
            return null;
        }
        return vnEventData;
    }

    @Override
    public void close() {

    }
}
