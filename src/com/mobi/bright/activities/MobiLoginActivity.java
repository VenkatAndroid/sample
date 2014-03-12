package com.mobi.bright.activities;

import android.os.Bundle;
import com.mobi.bright.util.AndroidUtil;

public class MobiLoginActivity extends MobiAuthActivity {

  @Override
  String createUrl() {
    return  AndroidUtil.WEB_SERVER_HTTP_URL +  "login.mobi?id=" + token;
  }


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}