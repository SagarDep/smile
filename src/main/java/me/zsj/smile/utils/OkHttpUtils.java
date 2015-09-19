package me.zsj.smile.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by zsj on 2015/9/13 0013.
 */
public class OkHttpUtils {

    private static OkHttpClient client = new OkHttpClient();
    static String str = "";

    public static String run(String url) {

        Request request = new Request.Builder().url(url).build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            str = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;

    }
}
