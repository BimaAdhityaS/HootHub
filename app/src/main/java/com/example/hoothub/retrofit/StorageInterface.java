package com.example.hoothub.retrofit;

import com.example.hoothub.model.comment;
import com.example.hoothub.model.file_image;
import com.example.hoothub.model.like_comment;
import com.example.hoothub.model.like_post;
import com.example.hoothub.model.like_reply;
import com.example.hoothub.model.post;
import com.example.hoothub.model.reply;
import com.example.hoothub.model.user;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StorageInterface {

    //POST Upload An Image
    @Multipart
    @POST("object/profile_image/{filename}")
    Call<file_image> uploadImage(
            @Part MultipartBody.Part file_path,
            @Path("filename") String filename
    );

    @Multipart
    @PUT("object/profile_image/{filename}")
    Call<file_image> updateImage(
            @Part MultipartBody.Part file_path,
            @Path("filename") String filename
    );
}
