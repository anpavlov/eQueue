package com.sudo.equeue.utils;

import android.app.Application;

public class QueueApplication extends Application {

    public static final String prefix = "com.sudo.equeue";
    public static final String APP_PREFS = "com.sudo.equeue.preferences";

    private ServiceHelper serviceHelper;

    public void initServiceHelper() {
        serviceHelper = new ServiceHelper(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }
}