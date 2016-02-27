package com.sudo.equeue.utils;

//import com.example.alex.headhunter.models.Employer;
//import com.example.alex.headhunter.models.SearchResults;
//import com.example.alex.headhunter.models.Vacancy;

import com.sudo.equeue.models.CreateQueueResponse;
import com.sudo.equeue.models.Employer;
import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.models.SearchResults;
import com.sudo.equeue.models.Vacancy;
import com.sudo.equeue.models.basic.ResponseBase;
import com.sudo.equeue.models.basic.User;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

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
    @POST("/queue/create")
    Call<ResponseBase<Queue>> createQueue(@Field("token") String token);

    @FormUrlEncoded
    @POST("/user/create")
    Call<ResponseBase<User>> createUser(@Field("token") String token);

    @GET("/queue/info")
    Call<ResponseBase<Queue>> getQueue(@Query("qid") int qid);

    @FormUrlEncoded
    @POST("/queue/update")
    Call<ResponseBase<Void>> saveQueue(@Field("token") String token, @Field("qid") int qid,
                                       @Field("name") String name, @Field("description") String description);

    @FormUrlEncoded
    @POST("/queue/call")
    Call<ResponseBase<Void>> callNext(@Field("token") String token, @Field("qid") int qid);

    @GET("/queue/find")
    Call<ResponseBase<List<Queue>>> findQueue();

    @FormUrlEncoded
    @POST("/queue/join")
    Call<ResponseBase<Void>> joinQueue(@Field("token") String token, @Field("qid") int qid);
}
