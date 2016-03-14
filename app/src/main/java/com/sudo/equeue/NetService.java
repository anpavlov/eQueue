package com.sudo.equeue;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.Processor;

//import com.sudo.equeue.models.Employer;
//import com.sudo.equeue.models.SearchResults;
//import com.sudo.equeue.models.Vacancy;

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
    public static final String ACTION_JOIN_QUEUE = QueueApplication.prefix + ".action.JOIN_QUEUE";
    public static final String ACTION_CREATE_USER = QueueApplication.prefix + ".action.CREATE_USER";
    public static final String ACTION_UPDATE_USER = QueueApplication.prefix + ".action.UPDATE_USER";
    public static final String ACTION_MY_QUEUES = QueueApplication.prefix + ".action.MY_QUEUES";
    public static final String ACTION_LOGIN_VK = QueueApplication.prefix + ".action.LOGIN_VK";
    public static final String ACTION_LOGIN_EMAIL = QueueApplication.prefix + ".action.LOGIN_EMAIL";
    public static final String ACTION_ME_IN_QUEUES = QueueApplication.prefix + ".action.ME_IN_QUEUES";
    public static final String ACTION_UPDATE_GCM = QueueApplication.prefix + ".action.UPDATE_GCM";


//    public static final String ACTION_GET_EMPLOYER = QueueApplication.prefix + ".action.GET_EMPLOYER";
//    public static final String ACTION_MAKE_SEARCH = QueueApplication.prefix + ".action.MAKE_SEARCH";
//    public static final String ACTION_GET_VACANCY = QueueApplication.prefix + ".action.ACTION_GET_VACANCY";

//    Data extras names
    public static final String EXTRA_TOKEN = QueueApplication.prefix + ".extra.TOKEN";
    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.QUEUE_ID";
    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.QUEUE";
    public static final String EXTRA_VKUID = QueueApplication.prefix + ".extra.VKUID";
    public static final String EXTRA_EMAIL = QueueApplication.prefix + ".extra.EMAIL";
    public static final String EXTRA_NAME = QueueApplication.prefix + ".extra.NAME";
    public static final String EXTRA_PASSWORD = QueueApplication.prefix + ".extra.PASSWORD";
    public static final String EXTRA_QUERY = QueueApplication.prefix + ".extra.QUERY";
    public static final String EXTRA_GCMID = QueueApplication.prefix + ".extra.GCMID";

//    public static final String EXTRA_EMPLOYER_ID = QueueApplication.prefix + ".extra.EMPLOYER_ID";
//    public static final String EXTRA_SEARCH_TEXT = QueueApplication.prefix + ".extra.SEARCH_TEXT";
//    public static final String EXTRA_SEARCH_AREA = QueueApplication.prefix + ".extra.SEARCH_AREA";
//    public static final String EXTRA_SEARCH_EXP = QueueApplication.prefix + ".extra.SEARCH_EXP";
//    public static final String EXTRA_SEARCH_EMPL = QueueApplication.prefix + ".extra.SEARCH_EMPL";
//    public static final String EXTRA_SEARCH_SCHED = QueueApplication.prefix + ".extra.SEARCH_SCHED";
//    public static final String EXTRA_SEARCH_PAGE = QueueApplication.prefix + ".extra.SEARCH_PAGE";
//    public static final String EXTRA_VACANCY_ID = QueueApplication.prefix + ".extra.VACANCY_ID";

    //    Return extras
    public static final String RETURN_CODE = QueueApplication.prefix + ".return.CODE";
    public static final String ERROR_MSG = QueueApplication.prefix + ".return.ERROR_MSG";
    public static final String RETURN_QUEUE = QueueApplication.prefix + ".return.QUEUE";
    public static final String RETURN_QUEUE_LIST = QueueApplication.prefix + ".return.QUEUE_LIST";
    public static final String RETURN_USER = QueueApplication.prefix + ".return.USER";
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
                    final String query = intent.getStringExtra(EXTRA_QUERY);
                    handleFindQueue(query);
                    break;
                }
                case ACTION_JOIN_QUEUE: {
                    final int queueId = intent.getIntExtra(EXTRA_QUEUE_ID, -1);
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleJoinQueue(token, queueId);
                    break;
                }
                case ACTION_CREATE_USER: {
                    final String email = intent.getStringExtra(EXTRA_EMAIL);
                    final String password = intent.getStringExtra(EXTRA_PASSWORD);
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleCreateUser(email, password, token);
                    break;
                }
                case ACTION_UPDATE_USER: {
                    final String email = intent.getStringExtra(EXTRA_EMAIL);
                    final String name = intent.getStringExtra(EXTRA_NAME);
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleUpdateUser(email, name, token);
                    break;
                }
                case ACTION_MY_QUEUES: {
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleMyQueues(token);
                    break;
                }
                case ACTION_ME_IN_QUEUES: {
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    handleMeInQueues(token);
                    break;
                }
                case ACTION_LOGIN_VK: {
                    final int vkuid = intent.getIntExtra(EXTRA_VKUID, -1);
                    handleLoginVk(vkuid);
                    break;
                }
                case ACTION_LOGIN_EMAIL: {
                    final String email = intent.getStringExtra(EXTRA_EMAIL);
                    final String password = intent.getStringExtra(EXTRA_PASSWORD);
                    handleLoginEmail(email, password);
                    break;
                }
                case ACTION_UPDATE_GCM: {
                    final String token = intent.getStringExtra(EXTRA_TOKEN);
                    final String gcmid = intent.getStringExtra(EXTRA_GCMID);
                    handleUpdateGcm(token, gcmid);
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

    private void handleCreateUser(String email, String password, String token) {
        if (email != null && email.equals("") || password != null && password.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }
        Bundle bundle = processor.createUser(email, password, token);
        receiver.send(CODE_OK, bundle);
    }

    private void handleUpdateUser(String email, String name, String token) {
        if (email != null && email.equals("") || name != null && name.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }
        if (token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }
        Bundle bundle = processor.updateUser(email, name, token);
        receiver.send(CODE_OK, bundle);
    }

    private void handleLoginVk(int vkuid) {
        if (vkuid == -1) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.loginVk(vkuid);
        receiver.send(CODE_OK, bundle);
    }

    private void handleLoginEmail(String email, String password) {
        if (email == null || email.equals("") || password == null || password.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.loginEmail(email, password);
        receiver.send(CODE_OK, bundle);
    }

    private void handleCreateQueue(String token) {
        if (token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.createQueue(token);
        receiver.send(CODE_OK, bundle);
    }

    private void handleGetQueue(int queueId) {
        if (queueId == -1) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.getQueue(queueId);
        receiver.send(CODE_OK, bundle);
    }

    private void handleSaveQueue(String token, Queue queue) {
        if (queue == null || token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.saveQueue(token, queue);
//        receiver.send(result, null);
        receiver.send(CODE_OK, bundle);
    }

    private void handleCallNext(String token, int queueId) {
        if (token == null || token.equals("") || queueId == -1) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.callNext(token, queueId);
//        receiver.send(result, null);
        receiver.send(CODE_OK, bundle);
    }

    private void handleFindQueue(String query) {
        Bundle bundle = processor.findQueue(query);
        receiver.send(CODE_OK, bundle);
    }

    private void handleMyQueues(String token) {
        if (token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.myQueues(token);
        receiver.send(CODE_OK, bundle);
    }

    private void handleMeInQueues(String token) {
        if (token == null || token.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.meInQueues(token);
        receiver.send(CODE_OK, bundle);
    }

    private void handleJoinQueue(String token, int queueId) {
        if (token == null || token.equals("") || queueId == -1) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.joinQueue(token, queueId);
//        receiver.send(result, null);
        receiver.send(CODE_OK, bundle);
    }

    private void handleUpdateGcm(String token, String gcmid) {
        if (token == null || token.equals("") || gcmid == null || gcmid.equals("")) {
            receiver.send(CODE_FAILED, null);
            return;
        }

        Bundle bundle = processor.updateGcmId(token, gcmid);
        receiver.send(CODE_OK, bundle);
    }

}
