package com.mobi.bright.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.mobi.bright.activities.services.payloads.MobiRegisterCustomer;
import com.mobi.bright.util.ActivityUtil;
import com.mobi.bright.util.AndroidUtil;

import java.util.List;


public class NativeRegisterActivity extends NativeAuthActivity {
// ------------------------------ FIELDS ------------------------------

    TextView name;
    TextView email;
    TextView password;
    TextView passwordConfirm;
    CheckBox certifyAge;

    final static String WS_URI = AndroidUtil.BASE_WSURI + "register-activity";

// -------------------------- OTHER METHODS --------------------------

    @Override Activity getActivity() {
        return this;
    }

    @Override String getWsUri() {
        return WS_URI;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        name = (TextView) findViewById(R.id.register_name);
        email = (TextView) findViewById(R.id.register_email);
        password = (TextView) findViewById(R.id.register_password);
        passwordConfirm = (TextView) findViewById(R.id.register_password_confirm);

        certifyAge = (CheckBox) findViewById(R.id.register_certify_age);

        new Thread(new WebSocketConnector()).run();
    }


    public void submitRegister(View view) {
        MobiRegisterCustomer mobiRegisterCustomer = new MobiRegisterCustomer();

        mobiRegisterCustomer.setName(name.getText()
                                         .toString());
        mobiRegisterCustomer.setEmail(email.getText()
                                           .toString());

        mobiRegisterCustomer.setPassword(password.getText()
                                                 .toString());

        mobiRegisterCustomer.setConfirmPassword(passwordConfirm.getText()
                                                               .toString());

        mobiRegisterCustomer.setAgeCheck(certifyAge.isChecked());

        List<String> errors = mobiRegisterCustomer.checkFields();

        if (errors.size() > 0) {
            ActivityUtil.alert(this,
                               errors);
        } else {
            Toast.makeText(this,
                           "Sending data",
                           Toast.LENGTH_SHORT)
                 .show();

           send(mobiRegisterCustomer);
        }
    }
}