package com.sudo.equeue.utils;

import android.app.Application;

public class QueueApplication extends Application {

//    TODO: move to strings resources
    public static final String prefix = "com.sudo.equeue";
    public static final String APP_PREFS = "com.sudo.equeue.preferences";
    public static final String PREFS_USER_ID_KEY = "com.sudo.equeue.preferences.user_id";
    public static final String PREFS_USER_TOKEN_KEY = "com.sudo.equeue.preferences.user_token";

    private ServiceHelper serviceHelper;

    public void initServiceHelper() {
        serviceHelper = new ServiceHelper(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }
}