package com.godspeed.drona.models;

import com.google.gson.annotations.SerializedName;


public class Login {
    @SerializedName("token")
    public String token;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("token_id")
    public String token_id;
    @SerializedName("error")
    public String error;
    @SerializedName("message")
    public String message;
    @SerializedName("user_pass")
    public String user_pass;

    public Login(String user_id, String user_pass) {
        this.user_id = user_id;
        this.user_pass = user_pass;
    }
}
