package com.mobi.bright.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobi.bright.minerva.dao.CustomerDao;
import com.mobi.bright.util.AnalyticsUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;


public class LoginDetailsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_details);


        AnalyticsUtil.startWebSocket(this);


        TextView textView = (TextView) findViewById(R.id.user_name);
        CustomerDao customer = new CustomerDao();
        customer.getFromDb();


        if (customer.getFromDb()) {
            textView.setText(customer.getName());
            Log.e("User123","user12");
        } else {
            textView.setText("Anonymous");
        }


        try {
            FileInputStream is = openFileInput("avatar.jpg");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int read;

            while ((read = is.read()) != -1) {
                bos.write(read);
            }

            byte[] blob = bos.toByteArray();

            Bitmap bmp = BitmapFactory.decodeByteArray(blob,
                                                       0,
                                                       blob.length);
            ImageView image = (ImageView) findViewById(R.id.user_icon);
            image.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File
            // Templates.
        }


    }

    public void startReading(View v) {

        Intent i = new Intent(this,
                              CategoryActivity.class);
        startActivity(i);

    }


}