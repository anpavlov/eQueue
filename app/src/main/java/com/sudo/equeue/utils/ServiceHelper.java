package com.sudo.equeue.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.widget.Toast;

import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

//import com.sudo.equeue.models.SearchResults;

public class ServiceHelper implements ServiceCallbackListener {

    private ArrayList<ServiceCallbackListener> currentListeners = new ArrayList<>();
    private AtomicInteger idCounter = new AtomicInteger();
    private Application application;
    private SharedPreferences prefs;

    public ServiceHelper(Application app) {
        this.application = app;
        currentListeners.add(this);

        prefs = app.getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);
    }

    public void addListener(ServiceCallbackListener currentListener) {
        currentListeners.add(currentListener);
    }

    public void removeListener(ServiceCallbackListener currentListener) {
        currentListeners.remove(currentListener);
    }

    private Intent createIntent(String action, final int requestId) {
        Intent i = new Intent(application, NetService.class);
        i.setAction(action);

        i.putExtra(NetService.EXTRA_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                for (ServiceCallbackListener currentListener : currentListeners) {
                    if (currentListener != null) {
                        currentListener.onServiceCallback(requestId, resultCode, resultData);
                    }
                }
            }
        });

        return i;
    }

    private int createId() {
        return idCounter.getAndIncrement();
    }

//    ===============================================================
//    ================= Fields for caching results ==================
//    ===============================================================
//    private int searchRequestId;
//    private SearchResults searchResults;
//    private Intent searchIntent;

//    ===============================================================
//    ======= Public custom methods to call from activities =========
//    ===============================================================

    public interface HandleCallbackIntf {
        void call(Serializable obj);
    }

    public void handleResponse(Context context, int resultCode, Bundle data, HandleCallbackIntf callback, String returnKey) {
        if (resultCode == NetService.CODE_OK) {
            if (data.getInt(NetService.RETURN_CODE) == NetService.CODE_OK) {
                if (returnKey != null) {
                    callback.call(data.getSerializable(returnKey));
                } else {
                    callback.call(null);
                }
            } else {
                Toast.makeText(context, data.getString(NetService.ERROR_MSG, context.getString(R.string.error_msg_unknown)), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Error in arguments", Toast.LENGTH_LONG).show();
        }
    }


    public int createUser(String email, String password, String name, boolean needToken) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_CREATE_USER, requestId);

        if (email != null && !email.equals("") && password != null && !password.equals("")) {
            i.putExtra(NetService.EXTRA_EMAIL, email);
            i.putExtra(NetService.EXTRA_PASSWORD, password);
        }
        if (name != null && !name.equals("")) {
            i.putExtra(NetService.EXTRA_NAME, name);
        }
        if (needToken) {
            String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
            i.putExtra(NetService.EXTRA_TOKEN, token);
        }

        application.startService(i);
        return requestId;
    }

    public int updateGcm(String gcmid) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_UPDATE_GCM, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_GCMID, gcmid);

        application.startService(i);
        return requestId;
    }

    public int loginVk(int vkuid) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_LOGIN_VK, requestId);

        i.putExtra(NetService.EXTRA_VKUID, vkuid);

        application.startService(i);
        return requestId;
    }

    public int loginEmail(String email, String password) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_LOGIN_EMAIL, requestId);

        i.putExtra(NetService.EXTRA_EMAIL, email);
        i.putExtra(NetService.EXTRA_PASSWORD, password);

        application.startService(i);
        return requestId;
    }

    public int updateUser(String email, String name) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_UPDATE_USER, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_EMAIL, email);
        i.putExtra(NetService.EXTRA_NAME, name);

        application.startService(i);
        return requestId;
    }

    public int createQueue() {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_CREATE_QUEUE, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);

        application.startService(i);
        return requestId;
    }

    public int getQueue(int queueId) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_GET_QUEUE, requestId);

//        String token = prefs.getString(TOKEN_KEY, null);
//        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_QUEUE_ID, queueId);

        application.startService(i);
        return requestId;
    }

    public int isIn(int queueId) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_IS_IN, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_QUEUE_ID, queueId);

        application.startService(i);
        return requestId;
    }

    public int saveQueueInfo(Queue queue) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_SAVE_QUEUE, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_QUEUE, queue);

        application.startService(i);
        return requestId;
    }

    public int callNext(int queueId) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_CALL_NEXT, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_QUEUE_ID, queueId);

        application.startService(i);
        return requestId;
    }

    public int findQueue(String query) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_FIND_QUEUE, requestId);

        if (query != null && !query.equals("")) {
            i.putExtra(NetService.EXTRA_QUERY, query);
        }

        application.startService(i);
        return requestId;
    }

    public int meInQueues() {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_ME_IN_QUEUES, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);

        application.startService(i);
        return requestId;
    }

    public int myQueues() {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_MY_QUEUES, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);

        application.startService(i);
        return requestId;
    }

    public int joinQueue(int queueId) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_JOIN_QUEUE, requestId);

        String token = prefs.getString(QueueApplication.PREFS_USER_TOKEN_KEY, null);
        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_QUEUE_ID, queueId);

        application.startService(i);
        return requestId;
    }

    public int joinQueueAnonym(int queueId, String token) {
        final int requestId = createId();
        Intent i = createIntent(NetService.ACTION_JOIN_QUEUE, requestId);

        i.putExtra(NetService.EXTRA_TOKEN, token);
        i.putExtra(NetService.EXTRA_QUEUE_ID, queueId);

        application.startService(i);
        return requestId;
    }

//    =============== old======================

//    public int getEmployerInfo(long employerId) {
//        final int requestId = createId();
//        Intent i = createIntent(NetService.ACTION_GET_EMPLOYER, requestId);
//
//        i.putExtra(NetService.EXTRA_EMPLOYER_ID, employerId);
//
//        application.startService(i);
//        return requestId;
//    }
//
//    public int makeSearch(String text, int areaId, String experienceApiId, ArrayList<String> employmentApiIds, ArrayList<String> scheduleApiIds) {
//        final int requestId = createId();
//        searchRequestId = requestId;
//        Intent i = createIntent(NetService.ACTION_MAKE_SEARCH, requestId);
//
//        i.putExtra(NetService.EXTRA_SEARCH_TEXT, text);
//        i.putExtra(NetService.EXTRA_SEARCH_AREA, areaId);
//        i.putExtra(NetService.EXTRA_SEARCH_EXP, experienceApiId);
//        i.putExtra(NetService.EXTRA_SEARCH_EMPL, employmentApiIds);
//        i.putExtra(NetService.EXTRA_SEARCH_SCHED, scheduleApiIds);
//
//        searchIntent = new Intent(i);
//
//        application.startService(i);
//        return requestId;
//    }
//
//    public int getVacancy(int vacancyId) {
//        final int requestId = createId();
//        Intent i = createIntent(NetService.ACTION_GET_VACANCY, requestId);
//
//        i.putExtra(NetService.EXTRA_VACANCY_ID, vacancyId);
//
//        application.startService(i);
//        return requestId;
//    }
//
//    public int addPageToResults() {
//        if (searchResults.getPage() < searchResults.getPages()) {
//            final int requestId = createId();
//            Intent i = searchIntent;
//
//            i.putExtra(NetService.EXTRA_SEARCH_PAGE, searchResults.getPage() + 1);
//
//            application.startService(i);
//            return requestId;
//        }
//        return -1;
//    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
//        if (requestId == searchRequestId) {
//            if (resultCode == NetService.CODE_OK) {
//                searchResults = (SearchResults) data.getSerializable(NetService.RETURN_DATA_SEARCH_RESULTS);
//            }
//        }
    }
}
