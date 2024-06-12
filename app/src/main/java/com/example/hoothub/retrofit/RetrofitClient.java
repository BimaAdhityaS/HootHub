package com.example.hoothub.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create an interceptor to add the headers
            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder()
                            .header("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVlb3l4aG91dnp0amx1enh3YnZnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTc0ODE4NzIsImV4cCI6MjAzMzA1Nzg3Mn0.aSt4Huc0JG95aKWwaQOFxGL6hlkQTZowvxaW-HxfUjU")
                            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVlb3l4aG91dnp0amx1enh3YnZnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTc0ODE4NzIsImV4cCI6MjAzMzA1Nzg3Mn0.aSt4Huc0JG95aKWwaQOFxGL6hlkQTZowvxaW-HxfUjU");
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                }
            };

            // Create OkHttpClient and add the interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://ueoyxhouvztjluzxwbvg.supabase.co/rest/v1/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
