package com.sudo.equeue.utils;

//import com.example.alex.headhunter.models.Employer;
//import com.example.alex.headhunter.models.SearchResults;
//import com.example.alex.headhunter.models.Vacancy;

//import com.sudo.equeue.models.CreateQueueResponse;
//import com.sudo.equeue.models.Employer;

import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.models.User;
import com.sudo.equeue.models.basic.PossibleError;
import com.sudo.equeue.models.basic.ResponseBase;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

//import com.sudo.equeue.models.SearchResults;
//import com.sudo.equeue.models.Vacancy;

public interface QueueApi {

//    @GET("/employers/{id}")
//    Call<Employer> getEmployer(@Path("id") long id);
//
//    @GET("/vacancies")
//    Call<SearchResults> makeSearch(@Query("text") String text, @Query("area") int areaId,
//                                   @Query("experience") String experience,
//                                   @Query("employment") List<String> employment,
//                                   @Query("schedule") List<String> schedule,
//                                   @Query("page") int page);
//
//    @GET("/vacancies/{id}")
//    Call<Vacancy> getVacancy(@Path("id") int id);

    @FormUrlEncoded
    @POST("/api/user/create/")
    Call<ResponseBase<User>> createUser(@Field("email") String email, @Field("password") String password, @Field("token") String token);

    @FormUrlEncoded
    @POST("/api/user/update/")
    Call<ResponseBase<User>> updateUser(@Field("email") String email, @Field("username") String name, @Field("token") String token);

    @FormUrlEncoded
    @POST("/api/user/login/")
    Call<ResponseBase<User>> loginEmail(@Field("email") String token, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/user/logout/")
    Call<ResponseBase<PossibleError>> logout();

    @FormUrlEncoded
    @POST("/api/user/vkauth/")
    Call<ResponseBase<User>> loginVk(@Field("vkuid") int token);

    @FormUrlEncoded
    @POST("/api/queue/create/")
    Call<ResponseBase<Queue>> createQueue(@Field("token") String token);

    @GET("/api/queue/info/")
    Call<ResponseBase<Queue>> getQueue(@Query("qid") int qid);

    @FormUrlEncoded
    @POST("/api/queue/update/")
    Call<ResponseBase<PossibleError>> saveQueue(@Field("token") String token, @Field("qid") int qid,
                                                @Field("name") String name, @Field("description") String description);

    @FormUrlEncoded
    @POST("/api/queue/call/")
    Call<ResponseBase<PossibleError>> callNext(@Field("token") String token, @Field("qid") int qid);

    @GET("/api/queue/find/")
    Call<ResponseBase<QueueList>> findQueue(@Query("query") String query);

    @FormUrlEncoded
    @POST("/api/queue/join/")
    Call<ResponseBase<PossibleError>> joinQueue(@Field("token") String token, @Field("qid") int qid);

    @FormUrlEncoded
    @POST("/api/queue/my/")
    Call<ResponseBase<QueueList>> myQueues(@Field("token") String token);

    @FormUrlEncoded
    @POST("/api/queue/in-queue/")
    Call<ResponseBase<QueueList>> meInQueues(@Field("token") String token);

    @FormUrlEncoded
    @POST("/api/user/updategcm/")
    Call<ResponseBase<QueueList>> updateGcmId(@Field("token") String token, @Field("gcmid") String gcmid);
}
