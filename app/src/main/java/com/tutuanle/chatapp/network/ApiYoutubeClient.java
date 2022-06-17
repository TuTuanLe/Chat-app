package com.tutuanle.chatapp.network;

import retrofit2.Retrofit;


import retrofit2.converter.gson.GsonConverterFactory;

public class ApiYoutubeClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}