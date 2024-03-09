package api;

import model.BeatInfor;
import model.SongInfor;
import model.VersionInfor;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("chords_url.php")
    Call<SongInfor> getSongInfor(@Field("folder") String folder,
                                 @Field("url") String url);

    @FormUrlEncoded
    @POST("beats.php")
    Call<BeatInfor> getBeatInfor(@Field("folder") String folder,
                                 @Field("filename") String filename);

    @GET("results")
    Call<ResponseBody> getVideoInfor(@Query("search_query") String name);

    @GET("trending")
    Call<ResponseBody> getVideoTrendingInfor(@Query("bp") String name);

    @GET("apimb/v1/categories/")
    Call<VersionInfor> getVersionInfor(@Header("Content-Type") String content_type, @Header("appid") String pid, @Header("timezone") String timezone
            ,@Header("sversion") String version, @Header("lfcode") String lfcode
            ,@Header("os") String os);
}