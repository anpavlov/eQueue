package com.sudo.equeue.utils;

import android.app.Application;

public class QueueApplication extends Application {

    public static final String prefix = "com.sudo.equeue";
    public static final String APP_PREFS = "com.sudo.equeue.preferences";
    public static final String PREFS_USER_ID_KEY = "com.sudo.equeue.preferences.user_id";
    public static final String PREFS_USER_TOKEN_KEY = "com.sudo.equeue.preferences.user_token";
    public static final String PREFS_USER_IS_LOGGED_IN = "com.sudo.equeue.preferences.is_logged_in";
    public static final String PREFS_USER_EMAIL = "com.sudo.equeue.preferences.user_email";
    public static final String PREFS_USER_NAME = "com.sudo.equeue.preferences.user_name";
    public static final String PREFS_SENT_TOKEN = "com.sudo.equeue.preferences.sent_token";

    private ServiceHelper serviceHelper;

    public void initServiceHelper() {
        serviceHelper = new ServiceHelper(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }

//    public void updateUserData
}