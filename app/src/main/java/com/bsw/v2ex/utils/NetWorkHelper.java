package com.bsw.v2ex.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class NetWorkHelper {

    /**
     * 判断是不是wifi网络状态
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        return "2".equals(getNetType(context)[0]);
    }

    /**
     * 判断是不是2/3G网络状态
     *
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        return "1".equals(getNetType(context)[0]);
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        if ("1".equals(getNetType(context)[0]) || "2".equals(getNetType(context)[0])) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前网络状态 返回2代表wifi,1代表2G/3G
     *
     * @param context
     * @return
     */
    public static String[] getNetType(Context context) {
        String[] types = {"Unknown", "Unknown"};
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.checkPermission("android.permission.ACCESS_NETWORK_STATE", context.getPackageName()) != 0) {
            types[0] = "Unknown";
            return types;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            types[0] = "Unknown";
            return types;
        }
        NetworkInfo networkInfo1 = connectivityManager.getNetworkInfo(1);
        if (networkInfo1 != null && networkInfo1.getState() == NetworkInfo.State.CONNECTED) {
            types[0] = "2";
            return types;
        }

        NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(0);
        if (networkInfo2 != null && networkInfo2.getState() == NetworkInfo.State.CONNECTED) {
            types[0] = "1";
            types[1] = networkInfo2.getSubtypeName();
            return types;
        }
        return types;

    }


}
