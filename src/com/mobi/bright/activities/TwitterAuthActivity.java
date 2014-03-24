package com.mobi.bright.activities;

import android.os.Bundle;
import android.util.Log;

import com.mobi.bright.util.AndroidUtil;
import com.mobi.bright.util.LogUtil;

import de.tavendo.autobahn.WebSocketException;


public class TwitterAuthActivity extends OauthActivity {
// ------------------------------ FIELDS ------------------------------

  private static final String TAG = LogUtil.createTag(FacebookAuthActivity.class);
  WebSocketConnector socketConnector = null;
  String token;


// -------------------------- OTHER METHODS --------------------------

  @Override
  String createUrl() {
    return "https://api.twitter.com/oauth/authenticate?oauth_token=" + token;
    // File Templates.
  }

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    socketConnector = new WebSocketConnector();


    new Thread(socketConnector).start();
  }


// -------------------------- INNER CLASSES --------------------------

  class WebSocketConnector implements Runnable {
    final String wsuri = AndroidUtil.BASE_WSURI + "twitter-login";
    final String TOKEN_KEY = "TOKEN-";

    @Override
    public void run() {

      Log.d(TAG, wsuri);

      try {
        webSocketConnection.connect(wsuri, new OauthWebSocketHandler() {


          @Override
          public void onTextMessage(String payload) {
            Log.d(TAG, "Got Message payload= " + payload);

            if (payload.startsWith(TOKEN_KEY)) {
              token = payload.substring(TOKEN_KEY.length());

              Log.d(TAG, "TOKEN=" + token);

              loadWebView();
            } else {
              storeCustomer(payload);
            }


          }


        });
      }
      catch (WebSocketException e) {
        socketReady = false;
        e.printStackTrace();
        Log.d(TAG, "Exception " + e.getMessage());

        AndroidUtil.showAlertDialog(activity,
                                    "Error Connecting to cloud services, Reason: " +
                                    e.getMessage());
      }


    }
  }
}