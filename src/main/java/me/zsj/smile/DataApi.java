package me.zsj.smile;

import me.zsj.smile.data.MeizhiData;
import me.zsj.smile.data.RestVideoData;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by zsj on 2015/9/16 0016.
 */
public interface DataApi {

    @GET("/data/福利/30/{page}")
    Observable<MeizhiData> getMeizhi(@Path("page")int page);

    @GET("/data/休息视频/" + 30 + "/{page}")
    Observable<RestVideoData> getRestVideoData(@Path("page") int page);

    @GET("/data/福利/10/{page}")
    Observable<MeizhiData> fetchRandomMeizhi(@Path("page")int page);

}
