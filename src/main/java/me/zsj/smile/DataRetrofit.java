package me.zsj.smile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;


public class DataRetrofit {

    private DataApi service;

    final static Gson gson =
        new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").serializeNulls().create();

    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s

    public DataRetrofit() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.avosapps.com/api")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(DataApi.class);

    }

    public DataApi getService() {
        return service;
    }
}
