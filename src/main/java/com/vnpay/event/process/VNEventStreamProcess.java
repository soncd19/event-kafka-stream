package com.vnpay.event.process;

import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;

/**
 * Created by SonCD on 01/04/2021
 */
public abstract class VNEventStreamProcess {
    protected final Properties properties;
    private KStreamBuilder kStreamBuilder;

    public VNEventStreamProcess(Properties properties) {
        this.properties = properties;
    }

    public void setKStreamBuilder(KStreamBuilder kStreamBuilder) {
        this.kStreamBuilder = kStreamBuilder;
    }

    public void run() {
        process(kStreamBuilder);
    }

    public abstract void process(KStreamBuilder kStreamBuilder);
}
