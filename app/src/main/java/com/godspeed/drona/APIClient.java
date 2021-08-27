package com.godspeed.drona;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anupamchugh on 05/01/17.
 */

class APIClient {

    private static Retrofit retrofit = null;
    public static String testingUrl="https://drona.digitalninza.com/drona_backend_7";
    public static String prodUrl="https://app.dronahub.com";
    static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        https://app.dronahub.com/Front/Front_api/get_login_cred

        retrofit = new Retrofit.Builder()
                .baseUrl(prodUrl+"/Front/Front_api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }

}
