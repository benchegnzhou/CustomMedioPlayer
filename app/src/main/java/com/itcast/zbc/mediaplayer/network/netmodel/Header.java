package com.itcast.zbc.mediaplayer.network.netmodel;

/**
 * Created by LIAN on 15/7/5.
 * 请求头
 */
public class Header {

    private AppName App;
    private Device Device;
    private NetUser User;

    public AppName getApp() {
        return App;
    }

    public void setApp(AppName app) {
        this.App = app;
    }

    public Device getDevice() {
        return Device;
    }

    public void setDevice(Device device) {
        this.Device = device;
    }

    public NetUser getUser() {
        return User;
    }

    public void setUser(NetUser user) {
        this.User = user;
    }
}
