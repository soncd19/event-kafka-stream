package com.vnpay.event.register;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by SonCD on 24/05/2021
 */
public class ClosableCallback implements FutureCallback<HttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ClosableCallback.class);
    private final CloseableHttpAsyncClient closeableHttpAsyncClient;

    public ClosableCallback(CloseableHttpAsyncClient closeableHttpAsyncClient) {
        this.closeableHttpAsyncClient = closeableHttpAsyncClient;
    }

    @Override
    public void completed(HttpResponse httpResponse) {
        close();
    }

    @Override
    public void failed(Exception e) {
        close();
    }

    @Override
    public void cancelled() {
        close();
    }

    private void close() {
        try {
            closeableHttpAsyncClient.close();
            logger.info("VNEvent: close http async client success");
        } catch (Exception e) {
            logger.error("VNEvent: cannot close http async client: " + e);
        }
    }
}
