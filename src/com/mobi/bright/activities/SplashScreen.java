package com.mobi.bright.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;
import com.mobi.bright.minerva.dao.CustomerDao;
import com.mobi.bright.minerva.data.DataHolder;
import com.mobi.bright.util.AnalyticsUtil;
import com.mobi.bright.util.LogUtil;

import java.io.IOException;

public class SplashScreen extends Activity {

    private static final String TAG = LogUtil.createTag(SplashScreen.class);
    //how long until we go to the next activity
    protected int _splashTime = 5000;
    private Thread splashTread;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash);

        DataHolder.init(this,
                        getDatabaseName());

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        WebSocketTask task = new WebSocketTask();
        task.execute();


        // thread for displaying the SplashScreen
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {

                        //wait 5 sec
                        wait(_splashTime);
                    }

                } catch (InterruptedException e) {
                } finally {


                    CustomerDao customer = new CustomerDao();


                    //start a new activity
                    Intent i = new Intent();

                    if (customer.getFromDb()) {
                        i.setClass(SplashScreen.this,
                                   CategoryActivity.class);
                    } else {
                        i.setClass(SplashScreen.this,
                                   SignUpActivity.class);
                    }


                    startActivity(i);

                    finish();

                }
            }
        };

        splashTread.start();
    }

    //Function that will handle the touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized (splashTread) {
                splashTread.notifyAll();
            }
        }
        return true;
    }

    protected String getDatabaseName() {

        final String defaultDatabase = "TEST_ME_minerva.db";
        String database = null;

        try {
            String[] fileNames = getAssets().list("");

            for (String fileName : fileNames) {

                if (fileName.endsWith(".db")) {
                    database = fileName;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        if (database == null) {
            toast("No database found");
        } else {

            if (database.equals(defaultDatabase)) {
                toast("Using default database");
            }
        }


        return database;
    }

    public void toast(String string) {
        Toast.makeText(this,
                       string,
                       Toast.LENGTH_LONG)
             .show();
    }


    private class WebSocketTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            AnalyticsUtil.startWebSocket(SplashScreen.this);
            return null;
        }
    }

}