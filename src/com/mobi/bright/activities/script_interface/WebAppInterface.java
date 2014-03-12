package com.mobi.bright.activities.script_interface;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
import com.mobi.bright.activities.callback.PositionCallback;
import com.mobi.bright.util.LogUtil;

import java.util.Date;

public class WebAppInterface {

  private static final String TAG = LogUtil.createTag(WebAppInterface.class);
  Context mContext;
  Integer pageId;
  Long start;
  private WebView webView;
  private Handler handler = new Handler();

  /**
   * Instantiate the interface and set the context
   */
  public WebAppInterface(WebView w, Context c) {
    mContext = c;
    webView = w;
  }

  @JavascriptInterface
  public void load(int pageId) {
    this.pageId = pageId;
    start = new Date().getTime();

    Log.d(TAG, "start=" + start);
  }

  @JavascriptInterface
  public void unload() {

    long end = new Date().getTime();
    Log.d(TAG, "end=" + end);
    long total = end - start;

    Log.d(TAG, "total=" + total + " -- " + (total % 1000));

  }

  @JavascriptInterface
  public void sendBackPosition(double currentPercentage, double maxPercentage) {



    Log.d(TAG, "currentPercentage=" + currentPercentage);
    Log.d(TAG, "maxPercentage=" + maxPercentage);

    if (mContext instanceof PositionCallback) {
     ((PositionCallback) mContext).reportPosition(currentPercentage, maxPercentage);
    }


    //.. do something with the data
  }

  public void getPagePosition() {
    webView.loadUrl("javascript:getPercentageAndPosition()");
  }

  /**
   * Show a toast from the web page
   */
  @JavascriptInterface
  public void showToast(String toast) {
    Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
  }
}