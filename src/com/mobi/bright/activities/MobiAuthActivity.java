package com.mobi.bright.activities;


import android.os.Bundle;
import android.util.Log;
import com.mobi.bright.util.AndroidUtil;
import com.mobi.bright.util.LogUtil;
import de.tavendo.autobahn.WebSocketException;

public class MobiAuthActivity extends OauthActivity {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = LogUtil.createTag(MobiAuthActivity.class);
    String token;
    WebSocketConnector socketConnector = null;

// -------------------------- OTHER METHODS --------------------------

    @Override String createUrl() {
        return AndroidUtil.WEB_SERVER_HTTP_URL + "register.mobi?id=" + token;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socketConnector = new WebSocketConnector();

        new Thread(socketConnector).start();
    }

// -------------------------- INNER CLASSES --------------------------

    class WebSocketConnector implements Runnable {
        final String wsuri = AndroidUtil.BASE_WSURI + "mobi-login";
        final String TOKEN_KEY = "TOKEN-";

        @Override
        public void run() {
            try {
                webSocketConnection.connect(wsuri,
                                            new OauthActivity.OauthWebSocketHandler() {
                                                @Override
                                                public void onTextMessage(String payload) {
                                                    Log.d(TAG,
                                                          "Got Message payload= " + payload);

                                                    if (payload.startsWith(TOKEN_KEY)) {
                                                        token = payload.substring(TOKEN_KEY.length());

                                                        Log.d(TAG,
                                                              "TOKEN=" + token);

                                                        loadWebView();
                                                    } else {

                                                        storeCustomer(payload);

                                                    }
                                                }
                                            });
            } catch (WebSocketException e) {
                socketReady = false;
                e.printStackTrace();
                Log.d(TAG,
                      "Exception " + e.getMessage());

                AndroidUtil.showAlertDialog(activity,
                                            "Error Connecting to cloud services, Reason: " +
                                            e.getMessage());
            }
        }
    }
}
