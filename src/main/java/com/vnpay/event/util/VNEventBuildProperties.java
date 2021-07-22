package com.vnpay.event.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by SonCD on 31/03/2021
 */
public class VNEventBuildProperties {

    private static final Logger logger = LoggerFactory.getLogger(VNEventBuildProperties.class);
    private static VNEventBuildProperties instance;
    private final Properties properties = new Properties();

    private VNEventBuildProperties() {
        buildProperties();

    }

    public static VNEventBuildProperties getInstance() {
        if (instance == null) {
            instance = new VNEventBuildProperties();
        }
        return instance;
    }

    private void buildProperties() {
        try {
            String configFile = System.getProperty("user.dir") + "/config/event.properties";
            logger.info("VNEvent: properties file: " + configFile);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
            properties.load(isr);
            isr.close();
        } catch (Exception e) {

        }
    }

    public Properties getProperties() {
        return properties;
    }
}
