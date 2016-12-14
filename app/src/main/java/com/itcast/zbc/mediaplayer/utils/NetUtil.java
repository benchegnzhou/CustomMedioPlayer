package com.itcast.zbc.mediaplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.itcast.zbc.mediaplayer.appcation.MyApplication;
import com.itcast.zbc.mediaplayer.network.netmodel.AppName;
import com.itcast.zbc.mediaplayer.network.netmodel.Device;
import com.itcast.zbc.mediaplayer.network.netmodel.Header;
import com.itcast.zbc.mediaplayer.network.netmodel.NetUser;

/**
 * 网络类型判断
 *
 * @author Administrator
 */
public class NetUtil {

    /**
     * 当前无网络
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * WIFI网络开启
     */
    public static final int NETWORK_TYPE_WIFI = 1;
    /**
     * mobile网络开启：2G、2.5G、3G、3.5G等
     */
    public static final int NETWORK_TYPE_MOBILE = 2;
    /**
     * wifi和mobile网络都已开启
     */
    public static final int NETWORK_TYPE_ALL = 3;

    /**
     * 当前网络为3G网络
     */
    public static final int NETWORK_TYPE_MOBILE_3G = 10;
    /**
     * 当前网络为4G网络
     */
    public static final int NETWORK_TYPE_MOBILE_4G = 11;
    /**
     * 当前网络为2G或者其他网络
     */
    public static final int NETWORK_TYPE_MOBILE_OTHER = 12;


    /**
     * 获取当然有效的网络类型，该函数只区分WIFI和MOBILE。详细区分
     * wifi、2g、3g、4g请查看函数：<BR>
     *
     * @param context
     * @return int 网络类型
     * @see #getNetConnectSubType(Context)
     * NetUtils.getNetConnectType()<BR>
     */
    public static int getNetConnectType(Context context) {
        int res = 0;
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (((wifi != null) && wifi.isAvailable() && wifi.isConnectedOrConnecting())
                && ((mobile != null) && mobile.isAvailable() && mobile.isConnectedOrConnecting())) {
            res = NETWORK_TYPE_ALL;
        } else if ((wifi != null) && wifi.isAvailable() && wifi.isConnectedOrConnecting()) {
            res = NETWORK_TYPE_WIFI;
        } else if ((mobile != null) && mobile.isAvailable() && mobile.isConnectedOrConnecting()) {
            res = NETWORK_TYPE_MOBILE;
        } else {
            res = NETWORK_TYPE_UNKNOWN;
        }
        return res;
    }


    /**
     * 获取当前有效网络类型，能够详细区分WIFI、2G、3G等网络类型。如果想只区分
     * WIFI和MOBILE，请查看函数：
     *
     * @param context
     * @return
     * @see #getNetConnectType(Context)
     * NetUtils.getNetConnectSubType()<BR>
     */
    public static int getNetConnectSubType(Context context) {
        int type = NETWORK_TYPE_UNKNOWN;
        int subtype = NETWORK_TYPE_UNKNOWN;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if ((activeNetInfo != null) && activeNetInfo.isConnectedOrConnecting()) {
            type = activeNetInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                type = NETWORK_TYPE_WIFI;
                subtype = type;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                switch (activeNetInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:// ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:// ~ 14-64 kbps IS95A or IS95B
                    case TelephonyManager.NETWORK_TYPE_EDGE:// ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
                        subtype = NETWORK_TYPE_MOBILE_OTHER;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 kbps
//				case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
//				case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
//				case TelephonyManager.NETWORK_TYPE_HSUPA:// ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:// ~ 400-7000 kbps
                        subtype = NETWORK_TYPE_MOBILE_3G;
                        break;
                    // NOT AVAILABLE YET IN API LEVEL 7
                    // case Connectivity.NETWORK_TYPE_EHRPD:// ~ 1-2 Mbps
                    // case Connectivity.NETWORK_TYPE_EVDO_B:// ~ 5 Mbps
                    // case Connectivity.NETWORK_TYPE_HSPAP:// ~ 10-20 Mbps
                    // case Connectivity.NETWORK_TYPE_LTE:// ~ 10+ Mbps
                    //	 subtype = NETWORK_TYPE_MOBILE_4G;
                    //break;
                    // Unknown
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        subtype = NETWORK_TYPE_UNKNOWN;
                        break;
                }
            }
        }
        return subtype;
    }

    /**
     * 判断终端网络是否有效
     *
     * @return boolean TRUE:代表网络有效
     */
    public static boolean isNetConnected() {
        return getNetConnectType(MyApplication.getInstance()) != NETWORK_TYPE_UNKNOWN;
    }


    /**
     * 判断网络情况
     *
     * @return
     */
    public static boolean checkNetWork(Context context) {
        // 重点判断wap方式

        // 操作步骤：
        // 判断联网的渠道：WLAN VS APN
        boolean isWIFI = isWIFIConnection(context);
        boolean isAPN = isAPNConnection(context);

        // 分支：如果isWIFI、isAPN均为false
        if (!isWIFI && !isAPN) {
            LogUtil.log("网络异常");
            return false;
        }

        // 如果是APN：区分NET OR WAP（代理信息的设置）
//		if (isAPN) {
//			readAPN(context);// 读联系人
//		}
        LogUtil.log("网络正常");
        return true;
    }

    /**
     * 读取APN配置信息
     *
     * @param context
     */
//	private static void readAPN(Context context) {
        /*ContentResolver contentResolver = context.getContentResolver();
        // uri:被选中的通信APN
		Cursor cursor = contentResolver.query(PREFERRED_APN_URI, null, null, null, null);// 只能返回一个结果，当前出与链接的APN信息
		if(cursor!=null&&cursor.moveToFirst())
		{
//			GlobalParams.PROXY_IP=cursor.getString(cursor.getColumnIndex("proxy"));
//			GlobalParams.PROXY_PORT=cursor.getInt(cursor.getColumnIndex("port"));
		}
		*/

//	}

    /**
     * 判断WIFI的链接状态
     *
     * @param context
     * @return
     */
    private static boolean isAPNConnection(Context context) {
        // 获取到系统服务——关于链接的管理
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取到WIFI的链接描述信息
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 判断WIFI的链接状态
     *
     * @param context
     * @return
     */
    private static boolean isWIFIConnection(Context context) {
        // 获取到系统服务——关于链接的管理
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取到WIFI的链接描述信息
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 拼装请求头
     *
     * @return
     */
    public static Header getRequestHeader() {
        Header header = new Header();
        AppName app = new AppName();
        Device device = new Device();
        NetUser user = new NetUser();

        app.setAppName("虾米音乐播放器");
        app.setChannel("1");   //渠道
        app.setMobileType("Android");
        app.setPackageName(MyApplication.getInstance().getPackageName());
        app.setVersion("" + DeviceUtil.getVersionNumber());

        device.setClientId(DeviceUtil.getID());
        device.setScreenSize(DeviceUtil.getDeviceScreenSize());
        device.setDenstiy("1");
        device.setFactory(DeviceUtil.getModel());
        device.setIMEI(DeviceUtil.getImei());
        device.setModel(DeviceUtil.getModel());
        device.setMac(DeviceUtil.getMac());
        device.setPlatform("Android");
        device.setFactory(DeviceUtil.getFACTURER());

        user.setToken("");//登录成功添加

        header.setApp(app);
        header.setDevice(device);
//        header.setUser(user);
        return header;
    }

/*
请求头的信息
    {
        "Header": {
        "App": {
            "AppName": "亿人帮",
                    "Channel": "1",
                    "MobileType": "Android",
                    "PackageName": "com.dy.yirenyibang",
                    "Version": "32"
        },
        "Device": {
            "ClientId": "HuaweiH30-U10",
                    "Denstiy": "1",
                    "Factory": "HUAWEI",
                    "IMEI": "359209025857727",
                    "Mac": "20:08:ed:08:23:42",
                    "Model": "HUAWEI H30-U10",
                    "Platform": "Android",
                    "ScreenSize": "720*1280"
        }
    },
        "Body": {
        "bid": "35dbc8905f9911e6ea925fa5c4af109c"
    }
    }
*/


    /**
     * 拼装整体
     *
     * @return
     */
    public static String getQequestData(JsonObject body) {
        JsonObject object = new JsonObject();
        object.add("Header", new Gson().toJsonTree(getRequestHeader()));
        object.add("Body", body);
        return object.toString();
    }
}
