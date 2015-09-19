package me.zsj.smile.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by zsj on 2015/7/19 0019.
 */
public class SnackUtils {

    public static void show(View view, String message) {

        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void show(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
    }

    public static void showAction(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }
}
