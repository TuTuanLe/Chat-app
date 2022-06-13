package com.tutuanle.chatapp.Api;
import com.tutuanle.chatapp.models.CustomizeChat;
import com.tutuanle.chatapp.models.Message;
import com.tutuanle.chatapp.models.RequestFriend;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.Story;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.models.UserStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {
    @GET("users")
    Call<List<User>> getUsers();

    @FormUrlEncoded
    @POST("users")
    Call<List<User>> getListFriend(@Field("uid") int id);

    @GET("messages")
    Call<List<Message>> getMessage();

    @GET("status")
    Call<List<Status>> getStatus();

    @GET("customize")
    Call<List<CustomizeChat>> getCustomizeChat();

    @GET("story")
    Call<List<Story>> getStory();

    @GET("user_status")
    Call<List<UserStatus>> getUserStatus();

    @GET("requestFriend")
    Call<List<RequestFriend>> getRequestFriend();

    @FormUrlEncoded
    @POST("addNewUser")
    Call<List<User>> addNewUser(@Field("id") String id, @Field("name") String name, @Field("email") String email, @Field("img") String img, @Field("isDark") String isDark, @Field("isEnglish") String isEnglish);

    @FormUrlEncoded
    @POST("getUserWithId")
    Call<List<User>> getUserFromID(@Field("id") String id);

    @FormUrlEncoded
    @POST("getMessageFromUid")
    Call<List<Message>> getMessageFromUid(@Field("id") int id);

    @FormUrlEncoded
    @POST("deleteMessage")
    Call<List<Message>> delete(@Field("id") int id);
}
