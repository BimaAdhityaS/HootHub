package com.example.hoothub.retrofit;

import android.database.Observable;

import com.example.hoothub.model.user;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    //Create a User
    @FormUrlEncoded
    @POST("User")
    Call<List<user>> createUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Header("Prefer") String preferHeader);

    //GET Filtering User sebagai indikasi login (Kalau ada usernya berarti dia masuk)
    @GET("User")
    Call<List<user>> login(@Query("email") String email, @Query("password") String password, @Query("select") String select);
}
