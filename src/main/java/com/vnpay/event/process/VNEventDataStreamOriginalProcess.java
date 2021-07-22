package com.vnpay.event.process;

import com.vnpay.event.constant.VNEventKeyConstant;
import com.vnpay.event.serializer.JsonObjectDeserializer;
import com.vnpay.event.serializer.JsonObjectSerializer;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by SonCD on 21/05/2021
 */
public class VNEventDataStreamOriginalProcess extends VNEventStreamProcess {
    private static final Logger logger = LoggerFactory.getLogger(VNEventDataStreamOriginalProcess.class);

    public VNEventDataStreamOriginalProcess(Properties properties) {
        super(properties);
    }

    @Override
    public void process(KStreamBuilder kStreamBuilder) {
        try {
            StringSerializer stringSerializer = new StringSerializer();
            StringDeserializer stringDeserializer = new StringDeserializer();

            Deserializer<JSONObject> jsonDeserializer = new JsonObjectDeserializer();
            Serializer<JSONObject> jsonSerializer = new JsonObjectSerializer();
            Serde<JSONObject> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

            Serde<String> stringSerde = Serdes.serdeFrom(stringSerializer, stringDeserializer);
            Serde<String> transactionSerde = Serdes.serdeFrom(stringSerializer, stringDeserializer);

            String inputTopic = properties.getProperty(VNEventKeyConstant.TOPIC_IN);
            String outputTopic = properties.getProperty(VNEventKeyConstant.TOPIC_OUT);

            KStream<String, JSONObject> transactionKStream = kStreamBuilder.stream(stringSerde, jsonSerde, inputTopic);
            KStream<String, JSONObject> userInfo = transactionKStream
                    .filter((key, value) -> value != null
                            && VNEventUserInfoProcess.filterEvent(value))
                    .map(((key, value) -> {
                        try {
                            return VNEventUserInfoProcess.process(key, value);
                        } catch (Exception e) {
                            logger.error("VNEvent process phone error: " + e);
                        }
                        return new KeyValue<>(key, null);
                    })).filter((k, v) -> v != null);

            userInfo.to(stringSerde, jsonSerde, outputTopic);
        } catch (Exception e) {
            logger.error("VNEvent VNEventDataStreamOriginalProcess stream data error: " + e);
        }
    }
}
