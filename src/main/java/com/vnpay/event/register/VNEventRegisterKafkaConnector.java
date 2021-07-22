package com.vnpay.event.register;

import com.vnpay.event.constant.VNEventKeyConstant;
import com.vnpay.event.util.VNEventBuildProperties;
import com.vnpay.event.util.VNEventFileToStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHeader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by SonCD on 24/05/2021
 */
public class VNEventRegisterKafkaConnector {

    private static final Logger logger = LoggerFactory.getLogger(VNEventRegisterKafkaConnector.class);

    public void register() throws GeneralSecurityException, ExecutionException, InterruptedException, IOException {
        String messagePath = VNEventBuildProperties.getInstance().getProperties().getProperty(VNEventKeyConstant.JSON_DATA_KAFKA_CONNECTOR);
        String message = VNEventFileToStringUtils.fileToString(messagePath);
        JSONArray jsonArrayRegister = new JSONArray(message);
        for (int i = 0; i < jsonArrayRegister.length(); i++) {
            JSONObject jsonObject = jsonArrayRegister.getJSONObject(i);
            String endpoint = VNEventBuildProperties.getInstance().getProperties().getProperty(VNEventKeyConstant.KAFKA_CONNECTOR_URL);
            String connectName = jsonObject.getString("name");
            String newEndpoint = endpoint.replace("connect.name", connectName);
            HttpResponse httpResponse = sendAsync(jsonObject.toString(), newEndpoint).get();
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode != 200 && responseCode != 201) {
                logger.warn("VNEvent: cannot register with kafka connector topics: " + jsonObject.getString("topics"));
            }
        }
    }

    private Future<HttpResponse> sendAsync(String message, String endpoint) throws GeneralSecurityException {
        try {
            HttpPut httpPost = preparePost(message, endpoint);
            logger.info("VNEvent: sending register kafka connector");
            CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.createSystem();
            closeableHttpAsyncClient.start();

            return closeableHttpAsyncClient.execute(httpPost, new ClosableCallback(closeableHttpAsyncClient));
        } catch (Exception e) {
            logger.error("VNEvent: Send register kafka connector error = " + e);
            throw new GeneralSecurityException(e);
        }
    }

    private HttpPut preparePost(String message, String endpoint) throws Exception {
        try {
            HttpPut httpPost = new HttpPut(endpoint);
            httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(new StringEntity(message));
            return httpPost;
        } catch (Exception e) {
            throw new Exception(e);
        }

    }
}
