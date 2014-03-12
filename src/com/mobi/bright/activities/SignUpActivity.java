package com.mobi.bright.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.mobi.bright.util.AnalyticsUtil;
import com.mobi.bright.util.AndroidUtil;


public class SignUpActivity extends Activity {
// -------------------------- OTHER METHODS --------------------------

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
    }

    public void registerMobifusion(View v) {

        if (AndroidUtil.isNetworkAvailable(this)) {
            Intent i = new Intent(this,
                                  NativeRegisterActivity.class);
            startActivity(i);
        } else {
            AndroidUtil.showAlertDialog(this,
                                        "No network is available");
        }

    }

    public void signInFacebook(View v) {

        if (AndroidUtil.isNetworkAvailable(this)) {
            Intent i = new Intent(this,
                                  FacebookAuthActivity.class);
            startActivity(i);
        } else {
            AndroidUtil.showAlertDialog(this,
                                        "No network is available");
        }
    }

    public void signInMobifusion(View v) {
        if (AndroidUtil.isNetworkAvailable(this)) {
            Intent i = new Intent(this,
                                 NativeLoginActivity.class);
            startActivity(i);
        } else {
            AndroidUtil.showAlertDialog(this,
                                        "No network is available");
        }
    }

    public void signInTwitter(View v) {

        if (AndroidUtil.isNetworkAvailable(this)) {
            Intent i = new Intent(this,
                                  TwitterAuthActivity.class);
            startActivity(i);
        } else {
            AndroidUtil.showAlertDialog(this,
                                        "No network is available");
        }
    }

    public void skipSignUp(View v) {

        if (AndroidUtil.isNetworkAvailable(this)) {
            WebSocketTask task = new WebSocketTask();
            task.execute();
        }

        //todo look for more than one page, then go to PagesActivity
        Intent i = new Intent(this,
                              CategoryActivity.class);
        startActivity(i);
    }


    private class WebSocketTask extends AsyncTask<Void, Void, Void> {

        @Override protected Void doInBackground(Void... params) {
            AnalyticsUtil analyticsUtil = new AnalyticsUtil();
            analyticsUtil.startWebSocketForAnonymousUserCreation();
            return null;
        }
    }

}