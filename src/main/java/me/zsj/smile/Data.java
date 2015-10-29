package me.zsj.smile;

import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.data.RestVideoData;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by zsj on 2015/9/16 0016.
 */
public interface Data {

    @GET("/data/福利/10/{page}")
    MeizhiData getMeizhi(@Path("page")int page);

    @GET("/data/休息视频/" + 10 + "/{page}")
    RestVideoData getRestVedioData(@Path("page") int page);

}