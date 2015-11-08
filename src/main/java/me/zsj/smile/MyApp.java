package me.zsj.smile;

import android.app.Application;

import com.litesuits.orm.LiteOrm;

/**
 * Created by zsj on 2015/7/20 0020.
 */
public class MyApp extends Application {

    public static LiteOrm mLiteOrm;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        mLiteOrm = LiteOrm.newSingleInstance(this, "smile.db");
        if (BuildConfig.DEBUG) {
            mLiteOrm.setDebugged(true);
        }
    }
}
