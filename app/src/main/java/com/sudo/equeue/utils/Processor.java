package com.sudo.equeue.utils;

import android.content.ContentValues;
import android.net.Uri;

//import com.example.alex.headhunter.content.contracts.SearchResultContract;
//import com.example.alex.headhunter.models.Employer;
//import com.example.alex.headhunter.models.SearchResults;
//import com.example.alex.headhunter.models.Vacancy;
//import com.example.alex.headhunter.models.VacancyShort;

import com.sudo.equeue.NetService;
import com.sudo.equeue.content.contracts.SearchResultContract;
import com.sudo.equeue.models.CreateQueueResponse;
import com.sudo.equeue.models.Employer;
import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.models.SearchResults;
import com.sudo.equeue.models.Vacancy;
import com.sudo.equeue.models.VacancyShort;
import com.sudo.equeue.models.basic.QueueList;
import com.sudo.equeue.models.basic.ResponseBase;
import com.sudo.equeue.models.basic.User;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class Processor {

    private QueueApi queueApi;
    private QueueApplication context;
    private final Uri CONTENT_SEARCH_RESULTS_URI = Uri.parse("content://com.example.alex.headhunter.provider/search_result");

    public Processor(QueueApplication context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        queueApi = retrofit.create(QueueApi.class);
    }

    public Queue createQueue(String token) {
        Response<ResponseBase<Queue>> response;
        try {
            response = queueApi.createQueue(token).execute();
        } catch (IOException e) {
            return null;
        }

        if (!response.isSuccess()) {
            return null;
        }

        ResponseBase<Queue> respBody = response.body();
        if (respBody.getCode() == 200) {
            return respBody.getBody();
        }

//        if (respBody.getCode() == 403) { // token wrong
//            User user = createUser(token);
////            TODO: save user to sharedPrefs
//            if (user == null) {
//                return null;
//            }
//            token = user.getToken();
//
//            try {
//                response = queueApi.createQueue(token).execute();
//            } catch (IOException e) {
//                return null;
//            }
//
//            if (!response.isSuccess()) {
//                return null;
//            }
//
//            respBody = response.body();
//            if (respBody.getCode() != 200) {
//                return null;
//            }
//            return respBody.getBody();
//        }

        return null;
    }

    public User createUser(String token) {
        Response<ResponseBase<User>> response;
        try {
            response = queueApi.createUser(token).execute();
        } catch (IOException e) {
            return null;
        }
        if (response.isSuccess()) {
            return response.body().getBody();
        }
        return null;
    }

    public Queue getQueue(int queueId) {
        Response<ResponseBase<Queue>> response;
        try {
            response = queueApi.getQueue(queueId).execute();
        } catch (IOException e) {
            return null;
        }
        if (response.isSuccess()) {
            ResponseBase<Queue> respBody = response.body();
            if (respBody.getCode() == 200) {
                return respBody.getBody();
            }
        }
        return null;
    }

    public int saveQueue(String token, Queue queue) {
        Response<ResponseBase<Void>> response;
        try {
            response = queueApi.saveQueue(token, queue.getQueueId(), queue.getName(), queue.getDescription()).execute();
        } catch (IOException e) {
            return NetService.CODE_FAILED;
        }
        if (response.isSuccess() && response.body().getCode() == 200) {
            return NetService.CODE_OK;
        }
        return NetService.CODE_FAILED;
    }

    public int callNext(String token, int queueId) {
        Response<ResponseBase<Void>> response;
        try {
            response = queueApi.callNext(token, queueId).execute();
        } catch (IOException e) {
            return NetService.CODE_FAILED;
        }
        if (response.isSuccess() && response.body().getCode() == 200) {
            return NetService.CODE_OK;
        }
        return NetService.CODE_FAILED;
    }

    public QueueList findQueue() {
        Response<ResponseBase<QueueList>> response;
        try {
            response = queueApi.findQueue().execute();
        } catch (IOException e) {
            return null;
        }
        if (response.isSuccess() && response.body().getCode() == 200) {
            return response.body().getBody();
        }
        return null;
    }

//    public Employer getEmployer(long id) {
//        Response<Employer> response;
//        try {
//            response = queueApi.getEmployer(id).execute();
//        } catch (IOException e) {
//            return null;
//        }
//        if (response.isSuccess()) {
//            return response.body();
//        }
//        return null;
//    }
//
//    public Vacancy getVacancy(int id) {
//        Response<Vacancy> response;
//        try {
//            response = queueApi.getVacancy(id).execute();
//        } catch (IOException e) {
//            return null;
//        }
//        if (response.isSuccess()) {
//            return response.body();
//        }
//        return null;
//    }
//
//    public SearchResults makeSearch(String text, int areaId, String experienceApiId, ArrayList<String> employmentApiIds, ArrayList<String> scheduleApiIds, int page) {
//        Response<SearchResults> response;
//        try {
//            response = queueApi.makeSearch(text, areaId, experienceApiId, employmentApiIds, scheduleApiIds, page).execute();
//        } catch (IOException e) {
//            return null;
//        }
//        if (response.isSuccess()) {
//            SearchResults results = response.body();
////            context.getContentResolver().delete(CONTENT_SEARCH_RESULTS_URI, null, null);
//
//            ContentValues contentValues = new ContentValues();
//
//            for (VacancyShort vacancy : results.getItems()) {
//                contentValues.put(SearchResultContract.SearchResultEntry.COLUMN_NAME_VACANCY_ID, vacancy.getId());
//                contentValues.put(SearchResultContract.SearchResultEntry.COLUMN_NAME_NAME, vacancy.getName());
//                contentValues.put(SearchResultContract.SearchResultEntry.COLUMN_NAME_EMPLOYER_NAME, vacancy.getEmployer().getName());
//
//                context.getContentResolver().insert(CONTENT_SEARCH_RESULTS_URI, contentValues);
//            }
//            return results;
//        }
//        return null;
//    }
}
