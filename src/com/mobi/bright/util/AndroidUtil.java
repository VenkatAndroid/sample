package com.mobi.bright.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.mobi.analytics.commom.enums.MobiNetworkTypes;

import java.io.FileOutputStream;
import java.io.IOException;


public final class AndroidUtil {
// ------------------------------ FIELDS ------------------------------

    /**
     * Url and Port of Server
     */
    final public static String WEB_SERVER_ROOT = "10.1.0.236:8080"; //"54.205.253.32:8080";  //"10.0.2.2:8080";
    /**
     * Web apps name
     */
    final public static String WEB_APP = "/mobi-analytics/";
    /**
     * The full URL of the web server
     */
    final public static String WEB_SERVER_HTTP_URL = "http://" + WEB_SERVER_ROOT + WEB_APP;
    /**
     * The base URL of the web socket services
     */
    final public static String BASE_WSURI = "ws://" + WEB_SERVER_ROOT + WEB_APP + "websocket/";
    /**
     * The name we want to call customers avatars image
     */
    public static final String FILENAME = "avatar.jpg";


    private static final String TAG = LogUtil.createTag(AndroidUtil.class);

// -------------------------- STATIC METHODS --------------------------

    public static void showAlertDialog(final Activity activity,
                                       final String message) {
        new AlertDialog.Builder(activity).setTitle("Error")
                                         .setMessage(message)
                                         .setNeutralButton("Close",
                                                           null)
                                         .show();
    }

    public static boolean isNetworkAvailable(final Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null) {
            Network network = new Network(activity);
            return network.getConnectivityStatus();
        } else {
            Log.d(TAG,
                  "activeNetworkInfo.getTypeName()=" + activeNetworkInfo.getTypeName());

            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    public static void saveAvatarImage(final Activity activity,
                                       final byte[] bytes) throws
    IOException {
        Log.d(TAG,
              "saving avatar file payload.length=" + bytes.length);
        FileOutputStream fos = activity.openFileOutput(FILENAME,
                                                       Context.MODE_PRIVATE);
        fos.write(bytes);
        fos.close();

        Log.d(TAG,
              "SAVED avatar file payload.length=" + bytes.length);
    }

    public static MobiNetworkTypes getNetworkType(final Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return MobiNetworkTypes.None;
        }

        int type = activeNetworkInfo.getType();


        if (type == ConnectivityManager.TYPE_BLUETOOTH) {
            return MobiNetworkTypes.BlueTooth;
        }

        if (type == ConnectivityManager.TYPE_ETHERNET) {
            return MobiNetworkTypes.Ethernet;
        }

        if (type == ConnectivityManager.TYPE_WIFI) {
            return MobiNetworkTypes.Wifi;
        }

        if (type == ConnectivityManager.TYPE_WIMAX) {
            return MobiNetworkTypes.WiMax;
        }

        if (activeNetworkInfo.getTypeName()
                             .equalsIgnoreCase("MOBILE")) {
            return getNetworkClass(activity);
        }

        Log.e(TAG,
              "Cant find activeNetwork  type  activeNetworkInfo.getTypeName()=" +
              activeNetworkInfo.getTypeName());

        return MobiNetworkTypes.Unknown;
    }

    static MobiNetworkTypes getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return MobiNetworkTypes.Mobile_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return MobiNetworkTypes.Mobile_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return MobiNetworkTypes.Mobile_4G;
            default:
                return MobiNetworkTypes.Unknown;
        }
    }

// -------------------------- INNER CLASSES --------------------------

    /**
     * 2.3 Hack
     */

    public static class Network {
        private Context context;
        private ConnectivityManager connManager;

        public Network(Context ctx) {
            this.context = ctx;
        }

        public boolean getConnectivityStatus() {
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = connManager.getActiveNetworkInfo();
            if (info != null) {
                return info.isConnected(); // WIFI connected
            } else {
                return false; // no info object implies no connectivity
            }
        }
    }
}
