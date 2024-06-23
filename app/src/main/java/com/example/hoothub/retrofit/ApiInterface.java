package com.example.hoothub.retrofit;

import android.database.Observable;

import com.example.hoothub.model.comment;
import com.example.hoothub.model.like_comment;
import com.example.hoothub.model.like_post;
import com.example.hoothub.model.like_reply;
import com.example.hoothub.model.post;
import com.example.hoothub.model.reply;
import com.example.hoothub.model.user;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
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

    //Update a User
    @FormUrlEncoded
    @PATCH("User")
    Call<List<user>> updateUser(
            @Query("id") String user_id,
            @Field("first name") String first_name,
            @Field("last name") String last_name,
            @Field("username") String username,
            @Field("bio") String bio);

    //Update a User Img Profile
    @FormUrlEncoded
    @PATCH("User")
    Call<List<user>> updateUserImgProfile(
            @Query("id") String user_id,
            @Field("img_profile") String img_profile);

    //GET Filtering User sebagai indikasi login (Kalau ada usernya berarti dia masuk)
    @GET("User")
    Call<List<user>> login(@Query("email") String email, @Query("password") String password, @Query("select") String select);

    //GET Current User untuk profile
    @GET("User")
    Call<List<user>> getCurrentUser(@Query("id") String id, @Query("select") String select);

    //Get semua post yang di send oleh current user
    @GET("Hoothub_Post_1")
    Call<List<post>> getCurrentUserPost(
            @Query("user_id") String user_id,
            @Query("select") String select,
            @Query("order") String order
    );

    //PATCH Update semua post dengan username yang diganti
    @FormUrlEncoded
    @PATCH("Hoothub_Post_1")
    Call<List<post>> updatePostUserName(
            @Query("user_id") String user_id,
            @Field("username") String username);

    @FormUrlEncoded
    @PATCH("Hoothub_Comment_2")
    Call<List<comment>> updateCommentUserName(
            @Query("user_id") String user_id,
            @Field("username") String username);

    @FormUrlEncoded
    @PATCH("Hoothub_Reply_3")
    Call<List<reply>> updateReplyUserName(
            @Query("user_id") String user_id,
            @Field("username") String username);



    @FormUrlEncoded
    @POST("Hoothub_Post_1")
    Call<List<post>> createPost(
            @Field("content") String content,
            @Field("user_id") String user_id,
            @Field("username") String username,
            @Header("Prefer") String preferHeader
    );

    @GET("Hoothub_Post_1")
    Call<List<post>> getPosts(@Query("order") String order);

    @GET("Hoothub_Post_1")
    Call<List<post>> getContent(
            @Query("id") String id,
            @Query("select") String select
    );

    @FormUrlEncoded
    @POST("Hoothub_Comment_2")
    Call<List<comment>> createComment(
            @Field("post_id") String post_id,
            @Field("user_id") String user_id,
            @Field("username") String username,
            @Field("content") String content,
            @Header("Prefer") String preferHeader
    );

    @GET("Hoothub_Comment_2")
    Call<List<comment>> getComment(
            @Query("post_id") String post_id,
            @Query("select") String select,
            @Query("order") String order
    );
    @GET("Hoothub_Comment_2")
    Call<List<comment>> getCommentById(
            @Query("id") String id,
            @Query("select") String select
    );

    @FormUrlEncoded
    @POST("Hoothub_Reply_3")
    Call<List<reply>> createReply(
            @Field("comment_id") String comment_id,
            @Field("user_id") String user_id,
            @Field("username") String username,
            @Field("content") String content,
            @Header("Prefer") String preferHeader
    );

    @GET("Hoothub_Reply_3")
    Call<List<reply>> getReply(
            @Query("comment_id") String comment_id,
            @Query("select") String select,
            @Query("order") String order
    );
    @GET("Like_Post")
    Call<List<like_post>> getLikePost(
            @Query("user_id") String user_id,
            @Query("post_id") String post_id,
            @Query("select") String select
    );
    @FormUrlEncoded
    @POST("Like_Post")
    Call<List<like_post>> createLikePost(
            @Field("user_id") String user_id,
            @Field("post_id") String post_id,
            @Header("Prefer") String preferHeader
    );
    @DELETE("Like_Post")
    Call<List<like_post>> deleteLikePost(
            @Query("user_id") String user_id,
            @Query("post_id") String post_id
    );
    @GET("Like_Comment")
    Call<List<like_comment>> getLikeComment(
            @Query("user_id") String user_id,
            @Query("comment_id") String comment_id,
            @Query("select") String select
    );
    @FormUrlEncoded
    @POST("Like_Comment")
    Call<List<like_comment>> createLikeComment(
            @Field("user_id") String user_id,
            @Field("comment_id") String comment_ic,
            @Header("Prefer") String preferHeader
    );
    @DELETE("Like_Comment")
    Call<List<like_comment>> deleteLikeComment(
            @Query("user_id") String user_id,
            @Query("comment_id") String comment_id
    );
    @GET("Like_Reply")
    Call<List<like_reply>> getLikeReply(
            @Query("user_id") String user_id,
            @Query("reply_id") String reply_id,
            @Query("select") String select
    );
    @FormUrlEncoded
    @POST("Like_Reply")
    Call<List<like_reply>> createLikeReply(
            @Field("user_id") String user_id,
            @Field("reply_id") String reply_id,
            @Header("Prefer") String preferHeader
    );
    @DELETE("Like_Reply")
    Call<List<like_reply>> deleteLikeReply(
            @Query("user_id") String user_id,
            @Query("reply_id") String reply_id
    );
}
