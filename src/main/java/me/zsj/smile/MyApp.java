package me.zsj.smile;

import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

/**
 * Created by zsj on 2015/7/20 0020.
 */
public class MyApp extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        SQLiteDatabase db = Connector.getDatabase();
    }
}
