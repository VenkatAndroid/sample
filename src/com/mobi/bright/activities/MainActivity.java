package com.mobi.bright.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


public class MainActivity extends FragmentActivity {

  private static final String TAG_DATA_LOADER = "dataLoader";
  private static final String TAG_SPLASH_SCREEN = "splashScreen";

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final FragmentManager fm = getSupportFragmentManager();





  }
}
