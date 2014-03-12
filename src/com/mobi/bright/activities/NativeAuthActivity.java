package com.mobi.bright.activities;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.mobi.analytics.commom.json.MobiAnalyticsFromServer;
import com.mobi.analytics.commom.util.GsonUtil;
import com.mobi.bright.activities.services.WebSocket;
import com.mobi.bright.minerva.dao.CustomerDao;
import com.mobi.bright.util.ActivityUtil;
import com.mobi.bright.util.AnalyticsUtil;
import com.mobi.bright.util.LogUtil;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import java.io.Serializable;


public abstract class NativeAuthActivity extends Activity {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = LogUtil.createTag(NativeAuthActivity.class);

    WebSocketConnection webSocketConnection = null;

    Boolean socketReady = null;

// -------------------------- OTHER METHODS --------------------------

    abstract Activity getActivity();

    abstract String getWsUri();


    protected synchronized void send(Serializable mobiRegisterCustomer) {
        if (socketReady == null) {
            try {
                wait(5000);
            } catch (InterruptedException ignore) {
            }
        }

        if (socketReady == null || !socketReady) {
            ActivityUtil.alert(this,
                               "Cannot connect to cloud services",
                               null);
        } else {
            webSocketConnection.sendTextMessage(GsonUtil.createInstance()
                                                        .toJson(mobiRegisterCustomer));
        }
    }

// -------------------------- INNER CLASSES --------------------------

    class WebSocketConnector implements Runnable {
        @Override public void run() {
            try {
                Log.d(TAG,
                      "Connecting to " + getWsUri());

                webSocketConnection = new WebSocketConnection();


                webSocketConnection.connect(getWsUri(),
                                            new RegisterWebSocketHandler());

                Toast.makeText(getActivity(),
                               "Connected to cloud services.",
                               Toast.LENGTH_SHORT)
                     .show();
            } catch (WebSocketException e) {
                Toast.makeText(getActivity(),
                               "Could not connect to cloud services.",
                               Toast.LENGTH_LONG)
                     .show();

                socketReady = false;


                Log.e(TAG,
                      "ERROR Connecting to " + getWsUri(),
                      e);
            }
        }
    }

    final class RegisterWebSocketHandler extends WebSocketHandler {
        @Override
        public void onTextMessage(String payload) {
            MobiAnalyticsFromServer mobiAnalyticsIncoming = GsonUtil.createInstance()
                                                                    .fromJson(payload,
                                                                              MobiAnalyticsFromServer.class);


            if (mobiAnalyticsIncoming.isError()) {
                ActivityUtil.alert(getActivity(),
                                   mobiAnalyticsIncoming.getErrorMessage(),
                                   null);
            } else {
                CustomerDao customer = AnalyticsUtil.setCustomerToSqlLite(mobiAnalyticsIncoming);

                WebSocket.setCustomer(customer);
                WebSocket.getInstance();


                Toast.makeText(getActivity(),
                               "Successfully registered",
                               Toast.LENGTH_SHORT)
                     .show();

                webSocketConnection.disconnect();
                ActivityUtil.doWelcomeIntent(getActivity());
            }
        }

        @Override
        public void onOpen() {
            socketReady = true;

            synchronized (getActivity()) {
                getActivity().notify();
            }

            Toast.makeText(getActivity(),
                           "Connected to cloud services.",
                           Toast.LENGTH_SHORT)
                 .show();
        }
    }
}
