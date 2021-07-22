package com.vnpay.event.app;

import com.vnpay.event.register.VNEventRegisterKafkaConnector;
import com.vnpay.event.stream.VNEventKafkaStreamer;
import com.vnpay.event.process.VNEventStreamProcess;
import com.vnpay.event.util.VNEventBuildProperties;
import com.vnpay.event.util.VNEventBuildResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by SonCD on 31/03/2021
 */
public class VNEventStreamApp {

    private static final Logger logger = LoggerFactory.getLogger(VNEventStreamApp.class);

    public static void main(String[] args) throws Exception {
        logger.info("VNEvent: Starting Stream Data App");

        Properties properties = VNEventBuildProperties.getInstance().getProperties();
        logger.info("VNEvent: read properties file success");
        VNEventRegisterKafkaConnector registerKafkaConnector = new VNEventRegisterKafkaConnector();
        registerKafkaConnector.register();

        logger.info("VNEvent: register kafka connector success");

        List<VNEventStreamProcess> vnEventStreamProcesses = VNEventBuildResourceConfig.parserXMLStream();

        VNEventKafkaStreamer vnEventKafkaStreamer = new VNEventKafkaStreamer(properties);
        vnEventKafkaStreamer.setVnEventStreamProcesses(vnEventStreamProcesses);

        vnEventKafkaStreamer.running();
    }
}
