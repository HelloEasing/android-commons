package com.easing.commons.android.manager;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.easing.commons.android.app.CommonApplication;
import com.easing.commons.android.value.network.WifiConnectionInfo;

import java.io.IOException;
import java.net.URL;

import lombok.SneakyThrows;

public class Network {

    public static final String NET_TYPE_NONE = "无网络";
    public static final String NET_TYPE_WIFI = "无线网络";
    public static final String NET_TYPE_DATA_2G = "数据网络/2G";
    public static final String NET_TYPE_DATA_3G = "数据网络/3G";
    public static final String NET_TYPE_DATA_4G = "数据网络/4G";
    public static final String NET_TYPE_UNKNOWN = "未知网络";

    //是否可以连接互联网
    public static boolean pingInternet() {
        try {
            String host = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + host);
            int status = p.waitFor();
            return status == 0;
        } catch (Exception e) {
            return false;
        }
    }

    //是否可以连接互联网
    public static boolean hasInternet() {
        return Network.hasInternet(CommonApplication.ctx);
    }

    //是否可以连接互联网
    public static boolean hasInternet(Context ctx) {
        if (SystemUtil.getApiVersion() < 23)
            return hasInternetByLowApi(ctx);

        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
        if (capabilities == null)
            return false;
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    //是否开启GPS服务
    public static boolean hasGPS(Context ctx) {
        LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return true;
        return false;
    }

    //是否有网络，不一定可以连接到互联网
    public static boolean hasNetwork(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network network = manager.getActiveNetwork();
        return network != null;
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network network = manager.getActiveNetwork();
        if (network == null)
            return NET_TYPE_NONE;

        NetworkInfo info = manager.getActiveNetworkInfo();
        String type = info.getTypeName().toUpperCase();
        String subtype = info.getSubtypeName().toUpperCase();

        if (type.equals("WIFI"))
            return NET_TYPE_WIFI;
        if (subtype.contains("EDGE"))
            return NET_TYPE_DATA_2G;
        if (subtype.contains("CDMA"))
            return NET_TYPE_DATA_3G;
        if (subtype.contains("LTE"))
            return NET_TYPE_DATA_4G;

        return NET_TYPE_UNKNOWN;
    }

    public static boolean hasInternetByJava() {
        try {
            new URL("https://www.baidu.com").openStream();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean hasInternetByLowApi(Context ctx) {
        ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable())
            return false;
        return true;
    }

    @SneakyThrows
    //获取本机的WIFI-IP
    public static String getWifiIp(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = manager.getConnectionInfo().getIpAddress();
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    @SneakyThrows
    //获取无线连接信息
    public static WifiConnectionInfo getWifiConnectionInfo(Context ctx) {
        WifiConnectionInfo info = new WifiConnectionInfo();
        WifiManager wifiManager = ((WifiManager) ctx.getSystemService(Context.WIFI_SERVICE));
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if (dhcp == null) return null;
        info.serverAddress = intToIp(dhcp.serverAddress);
        info.ipAddress = intToIp(dhcp.ipAddress);
        info.defaultGateway = intToIp(dhcp.gateway);
        info.subnetMask = intToIp(dhcp.netmask);
        info.dns = intToIp(dhcp.dns1);
        if (!info.serverAddress.equals("0.0.0.0")) {
            if (info.ipAddress.equals("0.0.0.0")) info.ipAddress = info.serverAddress;
            if (info.defaultGateway.equals("0.0.0.0")) info.defaultGateway = info.serverAddress;
            if (info.subnetMask.equals("0.0.0.0")) info.subnetMask = "255.255.255.0";
            if (info.dns.equals("0.0.0.0")) info.dns = info.serverAddress;
        }
        return info;
    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

}

