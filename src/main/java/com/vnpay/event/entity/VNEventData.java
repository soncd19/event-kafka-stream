package com.vnpay.event.entity;

/**
 * Created by SonCD on 14/04/2021
 */
public class VNEventData {
    private String key;
    private String imei;
    private String nonce;
    private String timestamp;
    private String data;
    private VNEventLicense license;

    public VNEventData() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public VNEventLicense getLicense() {
        return license;
    }

    public void setLicense(VNEventLicense license) {
        this.license = license;
    }

    @Override
    public String toString() {
        return "VNEventData{" +
                "key='" + key + '\'' +
                ", imei='" + imei + '\'' +
                ", nonce='" + nonce + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data='" + data + '\'' +
                ", license='" + license + '\'' +
                "}";
    }

}
