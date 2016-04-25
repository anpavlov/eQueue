package com.sudo.equeue;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.basic.PossibleError;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketService extends NonStopIntentService {

    public static final String ACTION_SOCKET = "com.sudo.equeue.websocket.action.socket";
    public static final String ACTION_BIND = "com.sudo.equeue.websocket.action.bind";
    public static final String ACTION_UNBIND = "com.sudo.equeue.websocket.action.unbind";
    public static final String ACTION_MES = "com.sudo.equeue.websocket.action.mes";
    public static final String ACTION_QID = "com.sudo.equeue.websocket.action.qid";

    public static final String ACTION_QUEUE_CHANGE = "com.sudo.equeue.websocket.action.queue_chenge";

    public static final String EXTRA_QUEUE_ID = "com.sudo.equeue.websocket.extra.QUEUE_ID";
    public static final String EXTRA_QUEUE_CHANGE_TYPE = "com.sudo.equeue.websocket.extra.QUEUE_change_type";
    public static final String EXTRA_QUEUE_CHANGE_ACTION = "com.sudo.equeue.websocket.extra.QUEUE_change_action";

    private AtomicInteger count = new AtomicInteger(0);
//    private int qid = -1;

    private boolean isClosed = false;
    private WebSocket ws;

    public WebSocketService() {
        super("WebSocketService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WebSocketService", "Started!");

        try {
            ws = (new WebSocketFactory()).createSocket("ws://p30282.lab1.stud.tech-mail.ru/ws/client/queue"); //factory = new WebSocketFactory();
            ws.addListener(new SocketCustomListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("WebSocketService", "Stopped!");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        switch (intent.getAction()) {
            case ACTION_BIND:
                if (!ws.isOpen()) {
                    ws.connectAsynchronously();
                }
//                Log.d("WebSocketService", "Bind call");
//                Log.d("WebSocketService", "count before = " + count.toString());
                count.incrementAndGet();
//                Log.d("WebSocketService", "count after = " + count.toString());
                break;
            case ACTION_UNBIND:
//                Log.d("WebSocketService", "Unbind call");
//                Log.d("WebSocketService", "count before = " + count.toString());
                if (count.decrementAndGet() == 0) {
//                    Log.d("WebSocketService", "count == 0");
                    isClosed = true;
                    ws.disconnect();
                    stopSelf();
                }
//                Log.d("WebSocketService", "count after = " + count.toString());
                break;
            case ACTION_SOCKET:
//                Log.d("WebSocketService", "connect call");
//                ws.connectAsynchronously();
                break;
            case ACTION_MES:
                ws.sendText("hello there!");
                break;
            case ACTION_QID:
                if (!ws.isOpen()) {
                    ws.connectAsynchronously();
                    Log.d(null, "Trying to connect, was not open");
                }
                final int queueId = intent.getIntExtra(EXTRA_QUEUE_ID, -1);
                if (queueId != -1) {
//                    qid = queueId;
                    Log.d(null, "Sending qid = " + Integer.toString(queueId));
                    ws.sendText(Integer.toString(queueId));
                }
        }
    }

    public class SocketCustomListener extends WebSocketAdapter {

//        @Override
//        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
//            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
//            if (!isClosed) {
//
//            }
//        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
//            if(Looper.myLooper() != Looper.getMainLooper()) {
//                Log.d("WebSocketService", "Got message, not main thread!");
//            }
            Log.d("WebSocketService", "Got message: " + text);

//            Gson gson = new Gson();
//            gson.fromJson(text, PossibleError.class);
            JsonElement jelem = new JsonParser().parse(text);
            JsonObject jobj = jelem.getAsJsonObject();
            String type = jobj.get("type").getAsString();
            String action = jobj.get("action").getAsString();
            int queueId = jobj.get("qid").getAsInt();

            Intent queueMessage = new Intent(ACTION_QUEUE_CHANGE);
            queueMessage.putExtra(EXTRA_QUEUE_CHANGE_ACTION, action);
            queueMessage.putExtra(EXTRA_QUEUE_ID, queueId);
            queueMessage.putExtra(EXTRA_QUEUE_CHANGE_TYPE, type);
            LocalBroadcastManager.getInstance(WebSocketService.this).sendBroadcast(queueMessage);
        }
    }
}
