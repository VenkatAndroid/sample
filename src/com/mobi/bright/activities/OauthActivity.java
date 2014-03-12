package com.mobi.bright.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import com.mobi.analytics.commom.json.MobiAnalyticsFromServer;
import com.mobi.analytics.commom.util.GsonUtil;
import com.mobi.bright.activities.services.WebSocket;
import com.mobi.bright.minerva.dao.CustomerDao;
import com.mobi.bright.util.ActivityUtil;
import com.mobi.bright.util.AnalyticsUtil;
import com.mobi.bright.util.AndroidUtil;
import com.mobi.bright.util.LogUtil;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;


public abstract class OauthActivity extends Activity {

    private static final String TAG = LogUtil.createTag(OauthActivity.class);
    Activity activity;
    WebSocketConnection webSocketConnection = null;
    Boolean socketReady = null;

    abstract String createUrl();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.web_view);

        webSocketConnection = new WebSocketConnection();
    }

    public void closeWebSocket() {
        if (webSocketConnection != null) {
            webSocketConnection.disconnect();
        }
        webSocketConnection = null;

        socketReady = null;

        System.gc();
    }

    void loadWebView() {
        WebView webView = (WebView) findViewById(R.id.web_view);

        webView.getSettings()
               .setJavaScriptEnabled(true); // enable javascript

        webView.setWebViewClient(new MyWebViewClient());

        String url = createUrl();

        Log.d(TAG,
              url);

        webView.loadUrl(url);

        webView.requestFocus();
    }



     /**
    void storeCustomerSHITE_REMOVE_OLD(final String payload) {


        //todo fix when have set up FB
        OAuthUserData oAuthUserData =
                GsonUtil.createInstance()
                        .fromJson(payload,
                                  OAuthUserData.class);

        if (oAuthUserData.getErrorReason() != null) {
            AndroidUtil.showAlertDialog(activity,
                                        oAuthUserData.getErrorReason());
        } else {
            if (oAuthUserData.getMobiId() > 0) {
               // MobiSharedStorage mobiSharedStorage = new MobiSharedStorage(this);
                // mobiSharedStorage.setLoginDetails(payload);


            } else {
                AndroidUtil.showAlertDialog(activity,
                                            "No user Id defined");
            }
        }

        if (!oAuthUserData.isHaveAvatar()) {
            closeWebSocket();
            ActivityUtil.doWelcomeIntent(this);
        }

    }
      **/

    void storeCustomer(final String payload) {

        MobiAnalyticsFromServer incoming;

        try {
            incoming = GsonUtil.createInstance()
                               .fromJson(payload,
                                         MobiAnalyticsFromServer.class);

            if (incoming.isError()) {
                AndroidUtil.showAlertDialog(activity,
                                            incoming.getErrorMessage());
                return;
            }
        } catch (Throwable t) {
            Log.e(TAG,
                  "Cant parse data to store incoming data payload=" + payload,
                  t);
            return;
        }

        if (incoming.getJson() == null) {
            AndroidUtil.showAlertDialog(activity,
                                        "Payload json is null");
            Log.e(TAG,
                  "Payload json is null");
            return;
        }


        try {
           CustomerDao customer = AnalyticsUtil.setCustomerToSqlLite(incoming);
            WebSocket.setCustomer(customer);

            if (!customer.isHaveAvatar()) {
                closeWebSocket();
               ActivityUtil.doWelcomeIntent(this);
            }


        } catch (Throwable t) {
            Log.e(TAG,
                  "Cant parse customer data json=" + payload,
                  t);

            //todo handle error   tell user


            return;
        }



    }


    class OauthWebSocketHandler extends WebSocketHandler {

        @Override
        public void onOpen() {
            socketReady = true;

            Toast.makeText(activity,
                           "Connected to cloud services.",
                           Toast.LENGTH_SHORT)
                 .show();
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            try {
                AndroidUtil.saveAvatarImage(activity,
                                            payload);
            } catch (Exception e) {
                e.printStackTrace();
            }

            closeWebSocket();
            ActivityUtil.doWelcomeIntent(OauthActivity.this);
        }

        @Override
        public void onClose(int code,
                            String reason) {
            socketReady = false;
            Log.d(TAG,
                  "Connection lost. " + "reason=" + reason + " code=" + code);
        }
    }


}
