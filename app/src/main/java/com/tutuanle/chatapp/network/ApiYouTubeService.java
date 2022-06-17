package com.tutuanle.chatapp.network;

import com.tutuanle.chatapp.models.YouTube;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiYouTubeService {
    //    @GET("search?key=AIzaSyAanRwCrLcd4E2HlegoctCfiIv4tmlnhPs&type=video&part=snippet&maxResults=1&q=5LnG6h1gaoE")
    @GET("search")
    Call<YouTube> getInfoYouTube(@Query("key") String key,
                                 @Query("type") String type,
                                 @Query("part") String part,
                                 @Query("maxResults") int maxResults,
                                 @Query("q") String q
    );
}
