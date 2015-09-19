package me.zsj.smile.utils;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by zsj on 2015/9/18 0018.
 */
public class TaskUtils {

    public static <Params, Progress, Result> void executeTask(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}
