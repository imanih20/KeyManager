package com.mohyeddin.passwordmanager.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectionHelper {
    public static boolean isOnline(final Activity activity){
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.Q) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null)
                return false;
            else
                return (info.isConnected());
        }else {
            if (manager != null) {
                NetworkCapabilities capabilities=manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (capabilities==null)return false;
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return true;
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    return true;
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            }
            return false;
        }
    }
    public static void openWifiSettingsScreen(Context context){
        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    public static void openDataUsageScreen(Context context){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(	"com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity"));
        context.startActivity(intent);
    }
}
