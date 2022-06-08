package com.example.m8uf2recu.Retrofit;

import com.example.m8uf2recu.Retrofit.Model.ModelApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCall {

    @GET("data/2.5/weather?")
    Call<ModelApi> getData(@Query("q") String city, @Query("appid") String appid);
}
