package me.zsj.smile.utils;

import android.support.design.widget.FloatingActionButton;

/**
 * Created by zsj on 2015/10/30 0030.
 */
public class FABAnimation {

    private static boolean mIsFABAppear;

    public static void fabAnimation(FloatingActionButton fab, int dy) {

        if (dy > 0) {
            fabDisAppear(fab);
        }else if (dy < 0){
            fabAppear(fab);
        }
    }

    private static void fabAppear(FloatingActionButton fab) {
        if (!mIsFABAppear) {
            fab.animate().scaleX(1).scaleY(1).setDuration(400).start();
            mIsFABAppear = true;
        }
    }

    private static void fabDisAppear(FloatingActionButton fab) {
        if (mIsFABAppear) {
            fab.setScaleX(1);
            fab.setScaleY(1);
            fab.animate().scaleX(0).scaleY(0).start();
            mIsFABAppear = false;
        }
    }
}
