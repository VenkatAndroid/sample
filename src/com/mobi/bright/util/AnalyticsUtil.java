package com.mobi.bright.util;


import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import com.mobi.analytics.commom.json.MobiAnalyticsFromServer;
import com.mobi.analytics.commom.util.GsonUtil;
import com.mobi.bright.activities.services.WebSocket;
import com.mobi.bright.minerva.dao.CustomerDao;

public final class AnalyticsUtil {

    private static final String TAG = LogUtil.createTag(AnalyticsUtil.class);

    public static void startWebSocket(final Activity activity) {


        CustomerDao customer = new CustomerDao();

        if (!customer.getFromDb()) {
            Log.d(TAG,
                  "No temp customer found, so no web socket created");
            return;
        }

        WebSocket.setCustomer(customer);

        WebSocket webSocket = WebSocket.getInstance();
        webSocket.setNetworkType(AndroidUtil.getNetworkType(activity));
        webSocket.setLocation(getLocation(activity));
        webSocket.startMobiSession();

    }


    public synchronized void startWebSocketForAnonymousUserCreation() {

        // creates object to be sent once connected
        WebSocket.createTempUserEventToBeSent();

        // will start connection
        WebSocket.getInstance();
    }


    static Location getLocation(final Activity activity) {
        // Get the location manager
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) activity.getSystemService(
                Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria,
                                                          false);
        return locationManager.getLastKnownLocation(provider);

    }


    public static CustomerDao setCustomerToSqlLite(final MobiAnalyticsFromServer incoming) {

        Log.d(TAG,
              "setCustomerToSqlLite=" + incoming.getJson());

        CustomerDao jsonCustomer = GsonUtil.createInstance()
                                        .fromJson(incoming.getJson(),
                                                  CustomerDao.class);

        CustomerDao dbCustomer = new CustomerDao();

        if (dbCustomer.getFromDb()) { // will return true if exists in db
            jsonCustomer.updateDb();
        } else { // need to insert does not exist
            jsonCustomer.insertDb();
        }


        return jsonCustomer;
    }
}
