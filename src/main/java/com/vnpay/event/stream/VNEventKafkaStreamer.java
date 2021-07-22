package com.vnpay.event.stream;

import com.vnpay.event.constant.VNEventKeyConstant;
import com.vnpay.event.process.VNEventStreamProcess;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Created by SonCD on 01/04/2021
 */
public class VNEventKafkaStreamer {
    private static final Logger logger = LoggerFactory.getLogger(VNEventKafkaStreamer.class);
    private final Properties properties;
    private List<VNEventStreamProcess> vnEventStreamProcesses = new ArrayList<>();


    public VNEventKafkaStreamer(Properties properties) {
        this.properties = properties;
    }

    public void running() {
        final CountDownLatch latch = new CountDownLatch(1);
        Properties kafkaProps = buildKafkaStreamProperties();
        KStreamBuilder kStreamBuilder = new KStreamBuilder();

        vnEventStreamProcesses.forEach(vnEventStreamProcess -> {
            vnEventStreamProcess.setKStreamBuilder(kStreamBuilder);
            vnEventStreamProcess.run();
        });
        KafkaStreams kafkaStreams = new KafkaStreams(kStreamBuilder, kafkaProps);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                kafkaStreams.close();
                latch.countDown();
            }
        });

        try {
            kafkaStreams.start();
            latch.await();
        } catch (Throwable e) {
            logger.error("VNEvent: Run thread Stream app Throwable: " + e);
            System.exit(1);
        }
    }

    private Properties buildKafkaStreamProperties() {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, properties.getProperty(VNEventKeyConstant.APPLICATION_ID));
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty(VNEventKeyConstant.BOOTSTRAP_SERVER));
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    public void setVnEventStreamProcesses(List<VNEventStreamProcess> vnEventStreamProcesses) {
        this.vnEventStreamProcesses = vnEventStreamProcesses;
    }
}
