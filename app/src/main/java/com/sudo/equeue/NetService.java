package com.sudo.equeue;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.models.basic.QueueList;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.Processor;

import com.sudo.equeue.models.Employer;
import com.sudo.equeue.models.SearchResults;
import com.sudo.equeue.models.Vacancy;

import java.util.ArrayList;
import java.util.List;

public class NetService extends IntentService {

    public static final String EXTRA_RECEIVER = QueueApplication.prefix + ".extra.RECEIVER";

    public static final int CODE_OK = 0;
    public static final int CODE_FAILED = 1;

//    Actions
    public static final String ACTION_CREATE_QUEUE = QueueApplication.prefix + ".action.CREATE_QUEUE";
    public static final String ACTION_GET_QUEUE = QueueApplication.prefix + ".action.GET_QUEUE";
    public static final String ACTION_SAVE_QUEUE = QueueApplication.prefix + ".action.SAVE_QUEUE";
    public static final String ACTION_CALL_NEXT = QueueApplication.prefix + ".action.CALL_NEXT";
    public static final String ACTION_FIND_QUEUE = QueueApplication.prefix + ".action.FIND_QUEUE";

//    public static final String ACTION_GET_EMPLOYER = QueueApplication.prefix + ".action.GET_EMPLOYER";
//    public static final String ACTION_MAKE_SEARCH = QueueApplication.prefix + ".action.MAKE_SEARCH";
//    public static final String ACTION_GET_VACANCY = QueueApplication.prefix + ".action.ACTION_GET_VACANCY";

//    Data extras names
    public static final String EXTRA_TOKEN = QueueApplication.prefix + ".extra.TOKEN";
    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.QUEUE_ID";
    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.QUEUE";

//    public static final String EXTRA_EMPLOYER_ID = QueueApplication.prefix + ".extra.EMPLOYER_ID";
//    public static final String EXTRA_SEARCH_TEXT = QueueApplication.prefix + ".extra.SEARCH_TEXT";
//    public static final String EXTRA_SEARCH_AREA = QueueApplication.prefix + ".extra.SEARCH_AREA";
//    public static final String EXTRA_SEARCH_EXP = QueueApplication.prefix + ".extra.SEARCH_EXP";
//    public static final String EXTRA_SEARCH_EMPL = QueueApplication.prefix + ".extra.SEARCH_EMPL";
//    public static final String EXTRA_SEARCH_SCHED = QueueApplication.prefix + ".extra.SEARCH_SCHED";
//    public static final String EXTRA_SEARCH_PAGE = QueueApplication.prefix + ".extra.SEARCH_PAGE";
//    public static final String EXTRA_VACANCY_ID = QueueApplication.prefix + ".extra.VACANCY_ID";

    //    Return extras
    public static final String RETURN_QUEUE = QueueApplication.prefix + ".return.QUEUE";
    public static final String RETURN_QUEUE_LIST = QueueApplication.prefix + ".return.QUEUE_LIST";
//    public static final String RETURN_DATA_SEARCH_RESULTS = QueueApplication.prefix + ".return.SEARCH_RESULTS";

    private Processor processor;
    private ResultReceiver receiver;

    public NetService() {
        super("NetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        processor = new Processor((QueueApplication) getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();

            switch (action) {
                case ACTION_CREATE_QUEUE: {
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleCreateQueue(token);
                    break;
                }
                case ACTION_GET_QUEUE: {
                    final int queueId = intent.getIntExtra(EXTRA_QUEUE_ID, -1);
                    handleGetQueue(queueId);
                    break;
                }
                case ACTION_SAVE_QUEUE: {
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    final Queue queue = (Queue) intent.getSerializableExtra(EXTRA_QUEUE);
                    handleSaveQueue(token, queue);
                    break;
                }
                case ACTION_CALL_NEXT: {
                    final int queueId = intent.getIntExtra(EXTRA_QUEUE_ID, -1);
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleCallNext(token, queueId);
                    break;
                }
                case ACTION_FIND_QUEUE: {
                    handleFindQueue();
                    break;
                }
            }



//            Main switch-case by action
//            if (ACTION_GET_EMPLOYER.equals(action)) {
//
//                final long id = intent.getLongExtra(EXTRA_EMPLOYER_ID, -1);
//                handleGetEmployer(receiver, id);
//
//            } else
//            if (ACTION_MAKE_SEARCH.equals(action)) {
//
//                String text = intent.getStringExtra(EXTRA_SEARCH_TEXT);
//                int areaId = intent.getIntExtra(EXTRA_SEARCH_AREA, 1);
//                String experienceApiId = intent.getStringExtra(NetService.EXTRA_SEARCH_EXP);
//                ArrayList<String> employmentApiIds = (ArrayList<String>) intent.getSerializableExtra(NetService.EXTRA_SEARCH_EMPL);
//                ArrayList<String> scheduleApiIds = (ArrayList<String>) intent.getSerializableExtra(NetService.EXTRA_SEARCH_SCHED);
//                int page = intent.getIntExtra(EXTRA_SEARCH_PAGE, 0);
//
//                SearchResults results = processor.makeSearch(text, areaId, experienceApiId, employmentApiIds, scheduleApiIds, page);
//                handleSearch(receiver, results);
//
//            } else
//            if (ACTION_GET_VACANCY.equals(action)) {
//
//                int vacancy_id = intent.getIntExtra(EXTRA_VACANCY_ID, -1);
//                if (vacancy_id != -1) {
//                    handleGetVacancy(receiver, vacancy_id);
//                }
//
//            }
        }
    }

//    ===============================================================
//    ============== Private methods to handle actions ==============
//    ===============================================================

    private void handleCreateQueue(String token) {
        if (token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Queue queue = processor.createQueue(token);
        if (queue != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RETURN_QUEUE, queue);
            receiver.send(CODE_OK, bundle);
        } else {
            receiver.send(CODE_FAILED, null);
        }
    }

    private void handleGetQueue(int queueId) {
        if (queueId == -1) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Queue queue = processor.getQueue(queueId);
        if (queue != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RETURN_QUEUE, queue);
            receiver.send(CODE_OK, bundle);
        } else {
            receiver.send(CODE_FAILED, null);
        }
    }

    private void handleSaveQueue(String token, Queue queue) {
        if (queue == null || token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        int result = processor.saveQueue(token, queue);
        receiver.send(result, null);
    }

    private void handleCallNext(String token, int queueId) {
        if (token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        int result = processor.callNext(token, queueId);
        receiver.send(result, null);
    }

    private void handleFindQueue() {
        QueueList queues = processor.findQueue();
        if (queues != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RETURN_QUEUE_LIST, queues);
            receiver.send(CODE_OK, bundle);
        } else {
            receiver.send(CODE_FAILED, null);
        }
    }

//    private void handleGetEmployer(ResultReceiver receiver, long id) {
//        Employer employer = processor.getEmployer(id);
//        if (employer != null) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("employer", employer);
//            receiver.send(CODE_OK, bundle);
//        } else {
//            receiver.send(CODE_FAILED, null);
//        }
//    }
//
//    private void handleGetVacancy(ResultReceiver receiver, int id) {
//        Vacancy vacancy = processor.getVacancy(id);
//        if (vacancy != null) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("vacancy", vacancy);
//            receiver.send(CODE_OK, bundle);
//        } else {
//            receiver.send(CODE_FAILED, null);
//        }
//    }
//
//    private void handleSearch(ResultReceiver receiver, SearchResults results) {
//        if (results != null) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(RETURN_DATA_SEARCH_RESULTS, results);
//            receiver.send(CODE_OK, bundle);
//        } else {
//            receiver.send(CODE_FAILED, null);
//        }
//    }
}
