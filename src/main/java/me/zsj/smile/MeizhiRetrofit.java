package me.zsj.smile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import java.util.concurrent.TimeUnit;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;


public class MeizhiRetrofit {

    private GankMeizhi service;

    final static Gson gson =
        new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").serializeNulls().create();

    public MeizhiRetrofit() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(12, TimeUnit.SECONDS);

        RestAdapter restAdapter = new RestAdapter.Builder().setClient(new OkClient(client))
            .setEndpoint("http://gank.avosapps.com/api")
            .setConverter(new GsonConverter(gson))
            .build();
        service = restAdapter.create(GankMeizhi.class);
    }

    public GankMeizhi getService() {
        return service;
    }
}
