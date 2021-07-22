package com.vnpay.event.process;

import com.vnpay.event.constant.VNEventKeyConstant;
import com.vnpay.event.decrypt.VNEventDecrypt;
import com.vnpay.event.entity.VNEventData;
import com.vnpay.event.serializer.JsonObjectDeserializer;
import com.vnpay.event.serializer.JsonObjectSerializer;
import com.vnpay.event.serializer.VNEventDataDeserializer;
import com.vnpay.event.serializer.VNEventDataSerializer;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by SonCD on 01/04/2021
 */
public class VNEventDataStreamDecryptProcess extends VNEventStreamProcess {

    private static final Logger logger = LoggerFactory.getLogger(VNEventDataStreamDecryptProcess.class);

    public VNEventDataStreamDecryptProcess(Properties properties) {
        super(properties);
    }

    @Override
    public void process(KStreamBuilder kStreamBuilder) {
        try {
            StringSerializer stringSerializer = new StringSerializer();
            StringDeserializer stringDeserializer = new StringDeserializer();
            Serde<String> stringSerde = Serdes.serdeFrom(stringSerializer, stringDeserializer);

            Deserializer<VNEventData> vnEventDataDeserializer = new VNEventDataDeserializer();
            Serializer<VNEventData> vnEventDataSerializer = new VNEventDataSerializer();
            Serde<VNEventData> vnEventDataSerde = Serdes.serdeFrom(vnEventDataSerializer, vnEventDataDeserializer);


            Deserializer<JSONObject> jsonDeserializer = new JsonObjectDeserializer();
            Serializer<JSONObject> jsonSerializer = new JsonObjectSerializer();
            Serde<JSONObject> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

            String inputTopic = properties.getProperty(VNEventKeyConstant.TOPIC_IN);
            String outputTopic = properties.getProperty(VNEventKeyConstant.TOPIC_OUT);
            KStream<String, VNEventData> transactionKStream = kStreamBuilder.stream(stringSerde, vnEventDataSerde, inputTopic);
            KStream<String, JSONObject> userInfo = transactionKStream.map((key, value) ->
            {
                try {
                    String data = deCryptMessage(value);
                    JSONObject jsonData = new JSONObject(data);
                    return new KeyValue<>(key, jsonData);
                } catch (Exception e) {
                    logger.error("VNEvent: KafkaStream data error: " + e.getMessage());
                }
                return new KeyValue<>(key, null);
            }).filter((key, value) -> value != null && VNEventUserInfoProcess.filterEvent(value))
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
            logger.error("VNEvent process Data encrypt error: " + e);
        }
    }

    public String deCryptMessage(VNEventData vnEventData) {
        try {
            String dataDecrypt = VNEventDecrypt.decryptData(vnEventData);
            logger.info("VNEvent: Message decrypt: " + dataDecrypt);
            return dataDecrypt;
        } catch (Exception e) {
            logger.error("VNEvent: Decrypt Message error by: " + e);
            return null;
        }
    }
}
