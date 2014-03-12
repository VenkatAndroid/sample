package com.mobi.bright.activities;

import android.os.Bundle;
import android.util.Log;
import com.mobi.bright.util.AndroidUtil;
import com.mobi.bright.util.LogUtil;
import de.tavendo.autobahn.WebSocketException;


public class FacebookAuthActivity extends OauthActivity {
// ------------------------------ FIELDS ------------------------------

    public static final String FB_APP_ID = "602943776423099";
    private static final String TAG = LogUtil.createTag(FacebookAuthActivity.class);
    private static final String CALLBACK_URL = AndroidUtil.WEB_SERVER_HTTP_URL + "facebook.mobi";
    String token = null;
    WebSocketConnector socketConnector = null;

// -------------------------- OTHER METHODS --------------------------

    public String createUrl() {
        StringBuilder builder = new StringBuilder("https://www.facebook.com/dialog/oauth?");

        builder.append("client_id=")
               .append(FB_APP_ID)
               .append('&');
        builder.append("redirect_uri=")
               .append(CALLBACK_URL)
               .append('&');
        builder.append("scope=")
               .append("user_birthday,email")
               .append('&');
        builder.append("state=")
               .append(token);

        return builder.toString();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketConnector = new WebSocketConnector();

        new Thread(socketConnector).start();
    }

// -------------------------- INNER CLASSES --------------------------

    class WebSocketConnector implements Runnable {
        final String wsuri = AndroidUtil.BASE_WSURI + "facebook-login";

        final String TOKEN_KEY = "TOKEN=";

        @Override
        public void run() {
            try {
                webSocketConnection.connect(wsuri,
                                            new OauthWebSocketHandler() {
                                                @Override
                                                public void onTextMessage(String payload) {
                                                    Log.d(TAG,
                                                          "Got Message payload= " + payload);

                                                    if (payload.startsWith("ERROR-")) {
                                                        AndroidUtil.showAlertDialog(activity,
                                                                                    payload);
                                                        //todo look for error
                                                    } else if (payload.startsWith(TOKEN_KEY)) {
                                                        token = payload.substring(TOKEN_KEY.length());
                                                        Log.d(TAG,
                                                              "token = " + token);

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