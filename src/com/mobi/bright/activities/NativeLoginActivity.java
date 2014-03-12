package com.mobi.bright.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.mobi.analytics.commom.util.GsonUtil;
import com.mobi.bright.activities.services.payloads.MobiLoginCustomer;
import com.mobi.bright.util.ActivityUtil;
import com.mobi.bright.util.AndroidUtil;

import java.util.List;


public class NativeLoginActivity extends NativeAuthActivity {


    TextView email;
    TextView password;

    final String WS_URI = AndroidUtil.BASE_WSURI + "login-activity";



// -------------------------- OTHER METHODS --------------------------

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        email = (TextView) findViewById(R.id.login_email);
        password = (TextView) findViewById(R.id.login_password);


        new Thread(new WebSocketConnector()).run();
    }

    private synchronized void send(MobiLoginCustomer mobiLoginCustomer) {
        if (socketReady == null) {
            try {
                wait(5000);
            } catch (InterruptedException ignore) {
            }
        }

        if (socketReady == null || !socketReady) {
            ActivityUtil.alert(getActivity(),
                               "Cannot connect to cloud services",
                               null);
        } else {
            webSocketConnection.sendTextMessage(GsonUtil.createInstance()
                                                        .toJson(mobiLoginCustomer));
        }
    }


    public void submitLogin(View view) {
        MobiLoginCustomer mobiLoginCustomer = new MobiLoginCustomer();


        mobiLoginCustomer.setEmail(email.getText()
                                        .toString());

        mobiLoginCustomer.setPassword(password.getText()
                                              .toString());


        List<String> errors = mobiLoginCustomer.checkFields();

        if (errors.size() > 0) {
            ActivityUtil.alert(getActivity(),
                               errors);
        } else {

            Toast.makeText(this,
                           "Sending data",
                           Toast.LENGTH_SHORT)
                 .show();

            send(mobiLoginCustomer);
        }
    }


    @Override Activity getActivity() {
        return this;
    }

    @Override String getWsUri() {
        return WS_URI;
    }
}