package io.im.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Description:
 * Author: jfz
 * Date: 2021-04-13 14:55
 */
@SuppressLint("MissingPermission")
public final class ChatNetworkUtil {
    //没有网络连接
    public static final String NETWORN_NONE = "NONE";
    //wifi连接
    public static final String NETWORN_WIFI = "WIFI";
    //手机网络数据连接类型
    public static final String NETWORN_2G = "2G";
    public static final String NETWORN_3G = "3G";
    public static final String NETWORN_4G = "4G";
    public static final String NETWORN_5G = "5G";
    public static final String NETWORN_MOBILE = "MOBILE";

    /**
     * 获取当前网络连接类型
     *
     * @param context ctx
     * @return type
     */
    public static String getNetworkState(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager)
            return NETWORN_NONE;

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORN_NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        case TelephonyManager.NETWORK_TYPE_NR:
                            return NETWORN_5G;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
        }
        return NETWORN_NONE;
    }

    /**
     * 获取联网的Manager
     *
     * @param context
     * @return
     */
    private static ConnectivityManager getConnectivityManager(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager == null) {
            return null;
        }
        return mConnectivityManager;
    }

    /**
     * 获取手机联网的类型
     *
     * @param context
     */
    public static String getConnectionType(Context context) {
        boolean connection = isConnection(context);
        if (connection) {
            ConnectivityManager manager = getConnectivityManager(context);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            String typeName = networkInfo.getTypeName();
            Log.i("ConnectionVerdict", typeName);
            return typeName;
        } else {
            return null;
        }
    }

    /**
     * 判断WiFi开关是否打开
     *
     * @return
     */
    public boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        NetworkInfo info = mgrConn.getActiveNetworkInfo();
        return ((info != null && info.getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断当前使用的网络是否WiFi
     *
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        if (manager != null) {
            NetworkInfo networkINfo = manager.getActiveNetworkInfo();
            if (networkINfo != null
                    && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前链接的网络是否是手机流量网络
     *
     * @return
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        NetworkInfo networkINfo = manager.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 获取手机是否链接网络
     *
     * @param context
     * @return
     */
    public static boolean isConnection(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable;
        if (networkInfo != null) {
            isAvailable = networkInfo.isAvailable();
        } else {
            isAvailable = false;
        }
        Log.i("ConnectionVerdict", isAvailable + "");
        return isAvailable;
    }

    /**
     * 判断设备 是否使用代理上网
     */
    public static boolean isWifiProxy(Context context) {
        boolean isWifi = isWifi(context);
        if (isWifi) {
            final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            String proxyAddress;
            int proxyPort;
            if (IS_ICS_OR_LATER) {
                proxyAddress = System.getProperty("http.proxyHost");
                String portStr = System.getProperty("http.proxyPort");
                proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            } else {
                proxyAddress = android.net.Proxy.getHost(context);
                proxyPort = android.net.Proxy.getPort(context);
            }
            return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
        }
        return false;
    }

    /**
     * 检测是否正在使用VPN，如果在使用返回true,反之返回flase
     */
    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    Log.d("vpn-----", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

}