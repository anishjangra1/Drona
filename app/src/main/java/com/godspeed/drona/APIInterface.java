package com.godspeed.drona;

import com.godspeed.drona.models.Login;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;


interface APIInterface {

    @POST("get_login_cred")
    Call<Login> createUser(@Body Login login);

    @FormUrlEncoded
    @POST("get_login_cred")
    retrofit2.Call<Login>getMediaList(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("save_token_details_of_user")
    retrofit2.Call<Login>saveFirebaseToken(@FieldMap Map<String, String> param);

}
