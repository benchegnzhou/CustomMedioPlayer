package com.itcast.zbc.mediaplayer.network.netmodel;

/**
 * Created by LIAN on 15/7/5.
 * 设备型号
 */
public class Device {
    private String Platform;
    private String Model;
    private String Factory;
    private String ScreenSize;
    private String Denstiy;
    private String IMEI;
    private String Mac;
    private String ClientId;

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getFactory() {
        return Factory;
    }

    public void setFactory(String factory) {
        Factory = factory;
    }

    public String getScreenSize() {
        return ScreenSize;
    }

    public void setScreenSize(String screenSize) {
        ScreenSize = screenSize;
    }

    public String getDenstiy() {
        return Denstiy;
    }

    public void setDenstiy(String denstiy) {
        Denstiy = denstiy;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }
}
