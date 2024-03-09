package model;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChordRetrofitClient {
    public static Retrofit retrofit;
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static Retrofit getRetrofitInstance()
    {
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://www.mazmazika.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
