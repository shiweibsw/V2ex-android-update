package com.bsw.v2ex.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bsw.v2ex.Application;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class MessageUtils {
    public static void showErrorMessage(Context cxt, String errorString) {
        Activity activity = (Activity) cxt;
        if (activity == null)
            Toast.makeText(Application.getInstance(), errorString, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(cxt, errorString, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context cxt, String msg) {
        if(cxt == null)
            cxt = Application.getInstance();
        Toast toast = Toast.makeText(cxt, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
