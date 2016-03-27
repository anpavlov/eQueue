package com.sudo.equeue.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.basic.PossibleError;
import com.sudo.equeue.models.basic.ResponseBase;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

//import com.example.alex.headhunter.content.contracts.SearchResultContract;
//import com.example.alex.headhunter.models.Employer;
//import com.example.alex.headhunter.models.SearchResults;
//import com.example.alex.headhunter.models.Vacancy;
//import com.example.alex.headhunter.models.VacancyShort;

public class Processor {

    private QueueApi queueApi;
    private QueueApplication context;
//    private final Uri CONTENT_SEARCH_RESULTS_URI = Uri.parse("content://com.example.alex.headhunter.provider/search_result");

    public Processor(QueueApplication context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + context.getString(R.string.server_ip))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d(null, "Processor constructor: after retrofit create");
        queueApi = retrofit.create(QueueApi.class);
    }

    private Bundle createError(String msg) {
        Bundle bundle = new Bundle();
        bundle.putInt(NetService.RETURN_CODE, NetService.CODE_FAILED);
        bundle.putString(NetService.ERROR_MSG, msg);
        return bundle;
    }

//    TODO: чекать наличие интернета
    @NonNull
    private <T extends PossibleError> Bundle makeSimpleRequest(Call<ResponseBase<T>> method, @Nullable String returnKey) {
        Response<ResponseBase<T>> response;
        try {
            response = method.execute();
        } catch (SocketTimeoutException e) {
            return createError(context.getString(R.string.error_msg_server_timeout));
        } catch (ConnectException e) {
            return createError(context.getString(R.string.error_msg_conn_timeout));
        } catch (IOException e) {
            return createError(context.getString(R.string.error_msg_unknown));
        }

        if (!response.isSuccess()) {
            return createError(context.getString(R.string.error_msg_server_error));
        }

        ResponseBase<T> respBody = response.body();
        if (respBody.getCode() == 200) {
            Bundle bundle = new Bundle();
            bundle.putInt(NetService.RETURN_CODE, NetService.CODE_OK);
            if (returnKey != null) {
                bundle.putSerializable(returnKey, respBody.getBody());
            }
            return bundle;
        } else {
            return createError(respBody.getBody().getError());
        }
    }

    public Bundle createQueue(String token) {
        return makeSimpleRequest(queueApi.createQueue(token), NetService.RETURN_QUEUE);
    }

    public Bundle createUser(String email, String password, String token) {
        return makeSimpleRequest(queueApi.createUser(email, password, token), NetService.RETURN_USER);
    }

    public Bundle updateUser(String email, String name, String token) {
        return makeSimpleRequest(queueApi.updateUser(email, name, token), NetService.RETURN_USER);
    }

    public Bundle loginVk(int vkuid) {
        return makeSimpleRequest(queueApi.loginVk(vkuid), NetService.RETURN_USER);
    }

    public Bundle loginEmail(String email, String password) {
        return makeSimpleRequest(queueApi.loginEmail(email, password), NetService.RETURN_USER);
    }

    public Bundle getQueue(int queueId) {
        return makeSimpleRequest(queueApi.getQueue(queueId), NetService.RETURN_QUEUE);
    }

    public Bundle saveQueue(String token, Queue queue) {
        return makeSimpleRequest(queueApi.saveQueue(token, queue.getQid(), queue.getName(), queue.getDescription()), null);
    }

    public Bundle callNext(String token, int queueId) {
        return makeSimpleRequest(queueApi.callNext(token, queueId), null);
    }

    public Bundle findQueue(String query) {
        return makeSimpleRequest(queueApi.findQueue(query), NetService.RETURN_QUEUE_LIST);
    }

    public Bundle myQueues(String token) {
        return makeSimpleRequest(queueApi.myQueues(token), NetService.RETURN_QUEUE_LIST);
    }

    public Bundle meInQueues(String token) {
        return makeSimpleRequest(queueApi.meInQueues(token), NetService.RETURN_QUEUE_LIST);
    }

    public Bundle joinQueue(String token, int queueId) {
        return makeSimpleRequest(queueApi.joinQueue(token, queueId), null);
    }

    public Bundle updateGcmId(String token, String gcmid) {
        return makeSimpleRequest(queueApi.updateGcmId(token, gcmid), null);
    }

}
