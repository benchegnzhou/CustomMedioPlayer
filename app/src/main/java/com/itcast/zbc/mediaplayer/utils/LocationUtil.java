package com.itcast.zbc.mediaplayer.utils;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;


/**
 * 定位
 *
 * @author dyyy
 */
public class LocationUtil {

    private LocationManagerProxy mLocationManagerProxy;
    /**
     * 地址的详细信息
     */
    private String address;
    /**
     * 省份**
     */
    private String province;
    /**
     * 市*
     */
    private String city;
    /**
     * 县*
     */
    private String county;

    private AMapLocationListener aMapLocationListener;
    private static LocationUtil instance = null;

    public static LocationUtil getInstance() {
        if (instance == null) {
            synchronized (LocationUtil.class) {
                if (instance == null) {
                    instance = new LocationUtil();
                }
            }
        }
        return instance;
    }

    private LocationUtil() {

    }

    /**
     * 开启定位，并设置 省、市 县
     *
     * @param context
     * @param tv_province
     * @param tv_city
     * @param tv_county
     * @return
     */
    public String init(Context context, final TextView tv_province, final TextView tv_city, final TextView tv_county) {
        // 初始化定位，只采用网络定位
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        mLocationManagerProxy.setGpsEnable(false);
        aMapLocationListener = new AMapLocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onLocationChanged(AMapLocation amapLocation) {

                if (amapLocation != null && amapLocation.getAMapException()
                        .getErrorCode() == 0) {
                    // 定位成功回调信息，设置相关消息
                    address = amapLocation.getAddress();
                    province = amapLocation.getProvince();
                    city = amapLocation.getCity();

                    if (!province.equals(city)) {
                        tv_province.setText(province);
                        tv_city.setText(city);
                    } else {
                        tv_province.setText(city);
                        tv_city.setText(amapLocation.getDistrict());
                    }

                } else {
                    LogUtil.log("Location ERR:" + amapLocation.getAMapException().getErrorCode());
                }
            }
        };

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用destroy()方法
        // 其中如果间隔时间为-1，则定位只定一次,
        // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 100, aMapLocationListener);

        return address;
    }


    /**
     * 开启定位，获取省市
     *
     * @param context
     * @return
     */
    public String initProvinceCity(Context context, final EditText et_site) {
        // 初始化定位，只采用网络定位
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        mLocationManagerProxy.setGpsEnable(false);
        aMapLocationListener = new AMapLocationListener() {

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onLocationChanged(AMapLocation amapLocation) {

                if (amapLocation != null && amapLocation.getAMapException()
                        .getErrorCode() == 0) {
                    // 定位成功回调信息，设置相关消息
                    address = amapLocation.getAddress();
                    province = amapLocation.getProvince();
                    city = amapLocation.getCity();

                    if (province.equals(city)) {
                        et_site.setText(province);
                    } else {
                        et_site.setText(province + city);
                    }

                } else {
                    LogUtil.log("Location ERR:" + amapLocation.getAMapException().getErrorCode());
                }
            }
        };

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用destroy()方法
        // 其中如果间隔时间为-1，则定位只定一次,
        // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
//        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 100, aMapLocationListener);
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 2 * 1000, 100, aMapLocationListener);


        return province;
    }

    /**
     * 关闭GPS定位
     */
    public void stop() {

        if (mLocationManagerProxy != null) {
            // 移除定位请求
            mLocationManagerProxy.removeUpdates(aMapLocationListener);
            // 销毁定位
            mLocationManagerProxy.destroy();

        }
    }
}
