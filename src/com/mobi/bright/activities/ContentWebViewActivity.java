package com.mobi.bright.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.mobi.bright.activities.callback.PositionCallback;
import com.mobi.bright.activities.script_interface.WebAppInterface;
import com.mobi.bright.activities.services.WebSocket;
import com.mobi.bright.data.DataStorage;
import com.mobi.bright.minerva.dao.PageHtml;
import com.mobi.bright.minerva.dao.PageInstance;
import com.mobi.bright.util.LogUtil;

import java.util.Date;


public class ContentWebViewActivity extends Activity implements PositionCallback {
// ------------------------------ FIELDS ------------------------------


    public static final String START_FROM_SCRATCH_TAG = "start_rb_from_scratch";
    final protected static String JAVASCRIPT =
            "<script>var PAGE_ID=__PAGE_ID__; var SCROLL_PERCENTAGE=__SCROLL_PERCENTAGE__;</script>";
    //"<script src=\"js/content_page" +
    //".js\"></script>";
    final protected static String CSS_FONT_SIZE = "    <style type=\"text/css\">\n" +
                                                  "      body {\n" +
                                                  "         font-size:~SIZE~" + "%;\n" +
                                                  "       }\n" +
                                                  "    </style>\n";
    final protected static String CSS_STATIC =
            "<link href=\"css/minrva.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "<link href=\"css/toolbar.css\" rel=\"stylesheet\" type=\"text/css\"/>\n";
    private static final String TAG = LogUtil.createTag(ContentWebViewActivity.class);
    protected DataStorage dataStorage;
    WebView webView;
    PageInstance pageInstance;
    long start;
    long total = 0;
    WebAppInterface webAppInterface;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface PositionCallback ---------------------

    @Override
    public void reportPosition(final double currentPercentage,
                               final double maxPercentage) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG,
                      "currentPercentage=" + currentPercentage);
                Log.d(TAG,
                      "maxPercentage=" + maxPercentage);


                Date now = new Date();

                int maxPercentageInt = (int) maxPercentage;

                if (maxPercentage > 95) {
                    maxPercentageInt = 100;

                    if (pageInstance.getDateRead() == null) {
                        pageInstance.setDateRead(now.getTime());
                    }
                }

                if (pageInstance.getMaxPercentage() == null ||
                    maxPercentageInt > pageInstance.getMaxPercentage()) {
                    pageInstance.setMaxPercentage(maxPercentageInt);
                }

                pageInstance.setCurrentPercentage((int) currentPercentage);
                pageInstance.setLastUpdate(now.getTime());

                pageInstance.update();


                Log.d(TAG,
                      "pageInstance.getMaxPercentage()=" + pageInstance.getMaxPercentage());


                WebSocket webSocket = WebSocket.getInstance();

                if (webSocket != null) {
                    webSocket.createPagePositionEvent(pageInstance,
                                                      (int) currentPercentage,
                                                      pageInstance.getMaxPercentage());
                }

            }
        });

    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void onBackPressed() {
        long end = new Date().getTime();

        super.onBackPressed();

        finish();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataStorage = new DataStorage(this);

        setContentView(R.layout.web_view);

        Bundle extras = getIntent().getExtras();

        pageInstance = (PageInstance) extras.getSerializable(PageInstance.class.getName());

        // we can change with percentages
        pageInstance = pageInstance.getPageFromDbObject(pageInstance.getId());


        PageHtml pageHtml = new PageHtml();
        String html = pageHtml.getHtmlFromDb(pageInstance.getId());


        webView = (WebView) findViewById(R.id.web_view);
        init();


        webView.loadDataWithBaseURL("file:///android_asset/",
                                    initHtml(html),
                                    "text/html",
                                    "UTF-8",
                                    null);

        start = new Date().getTime();
    }

    void init() {
        //webView.setBackgroundColor(0x00000000);
        webView.setInitialScale(100);
        WebSettings webSettings = webView.getSettings();


        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(true);
        //webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);


        webSettings.setAllowFileAccess(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(false);
        // webSettings.setAppCacheMaxSize(5 * 1024 * 1024);

        webSettings.setDatabaseEnabled(true);

        webAppInterface = new WebAppInterface(webView,
                                              this);

        webView.addJavascriptInterface(webAppInterface,
                                       "Android");

        // webView.clearCache(true);


        webView.setWebChromeClient(new WebChromeClient() {
            public void onRequestFocus(WebView view) {
                Log.d(TAG,
                      "onRequestFocus");
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG,
                      cm.message() + " -- From line "
                      + cm.lineNumber() + " of "
                      + cm.sourceId());
                return true;
            }

            public boolean onJsAlert(WebView view,
                                     String url,
                                     String message,
                                     JsResult result) {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setMessage(message)
                        .setPositiveButton("OK",
                                           new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog,
                                                                   int which) {
                                                   //do nothing
                                               }
                                           })
                        .create();
                dialog.show();
                result.confirm();
                return true;
            }
        });


        if (android.os.Build.VERSION.SDK_INT > 14) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE,
                                 null);
        }
    }

    protected String initHtml(String html) {
       // Log.d(TAG,
       //       "\n" +
       //       "\nPRE html= " + html);




        String css = getCssFontSize(computeSizeHack());

        // Will contain the quizzes css if a quiz exists on the page
        String quizCss = "";
        String quizJavascript = "";


        String cssStatic = CSS_STATIC;

        // Depectated
        //if (html.contains(MOBI_QUIZ_TAG)) {
        //    quizCss = "<link href=\"quiz/quiz.css\" rel=\"stylesheet\" type=\"text/css\"/>";
        //    quizJavascript = "<script type=\"text/javascript\" src=\"quiz/quiz.js\"></script>";
        //    cssStatic = "";
        // }


        html = html.replace("</head>",
                            "\n" +
                            //META_VIEWPORT + "\n" +
                            // cssStatic + "\n" +
                            // css + "\n" +
                            // quizCss + "\n" +
                            // quizJavascript + "\n" +
                            getJavaScript() + "\n" +
                            "</head>\n");


        //Log.d(TAG,
        //      "\n\nPOST html= " + html);


        return html;
    }

    protected String getCssFontSize(int hack) {
        float size = dataStorage.getSize();

        int sizeInt = (int) (size * hack);


        return CSS_FONT_SIZE.replace("~SIZE~",
                                     "" + sizeInt);
    }

    protected int computeSizeHack() {
        Display display = getWindowManager().getDefaultDisplay();


        /* To support 2.3 devices */
        @SuppressWarnings("deprecation")
        final int width = display.getWidth();

        int toReturn = 100;

        if (width >= 1200) {
            toReturn = 120;
        } else if (width >= 800) {
            toReturn = 100;
        } else if (width >= 500) {
            toReturn = 80;
        } else if (width >= 400) {
            toReturn = 70;
        } else if (width >= 200) {
            toReturn = 50;
        }


        return toReturn;
    }

    protected String getJavaScript() {
        String script = JAVASCRIPT.replace("__PAGE_ID__",
                                           "" + pageInstance.getId());
        script = script.replace("__SCROLL_PERCENTAGE__",
                                pageInstance.getCurrentPercentage() == null ? "0" :
                                "" + pageInstance.getCurrentPercentage());
        return script;
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        Log.d(TAG,
              "pause");

        total += (new Date().getTime()) - start;

        Log.d(TAG,
              "total=" + total);

        webAppInterface.getPagePosition();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.d(TAG,
              "resume");
        start = new Date().getTime();
    }

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
        Log.d(TAG,
              "stop");


        int seconds = (int) (total / 1000);
        Log.d(TAG,
              "seconds=" + seconds);

        if (seconds > 5) {
            total = 0;
            WebSocket webSocket = WebSocket.getInstance();
            if (webSocket != null) {
                webSocket.createPageViewEvent(pageInstance,
                                              seconds);
            }
        }
    }
}