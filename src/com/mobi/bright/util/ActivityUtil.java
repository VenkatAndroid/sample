package com.mobi.bright.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.mobi.bright.activities.LoginDetailsActivity;
import com.mobi.bright.activities.R;

import java.util.List;


public final class ActivityUtil {

    public static void doWelcomeIntent(final Activity activity) {
        final Intent intent = new Intent(activity,
                                         LoginDetailsActivity.class);
        activity.startActivity(intent);

        activity.finish();
    }


    public static void alert(final Activity activity,
                             final String error,
                             final String titleIn) {

        String title;

        if (titleIn == null) {
            title = activity.getString(R.string.error);
        } else {
            title = titleIn;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(error);
        alertDialog.setButton("OK",
                              new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog,
                                                      int which) {
                                      //MT
                                  }
                              });

        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.show();
    }

    public static void alert(final Activity activity,
                             final List<String> errors) {
        String message = "";

        for (String error : errors) {
            message += error + "\n";
        }

        String title = activity.getString(R.string.error) + (errors.size() == 1 ? "" : "s");

        ActivityUtil.alert(activity,
                           message,
                           title);
    }


}
