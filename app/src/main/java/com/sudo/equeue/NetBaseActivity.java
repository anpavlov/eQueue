package com.sudo.equeue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.ServiceCallbackListener;
import com.sudo.equeue.utils.ServiceHelper;

//import com.example.alex.headhunter.utils.QueueApplication;
//import com.example.alex.headhunter.utils.ServiceCallbackListener;
//import com.example.alex.headhunter.utils.ServiceHelper;

public abstract class NetBaseActivity extends AppCompatActivity implements ServiceCallbackListener {

    private ServiceHelper serviceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = ((QueueApplication) getApplication()).getServiceHelper();

        if (serviceHelper == null) {
            ((QueueApplication) getApplication()).initServiceHelper();
            serviceHelper = ((QueueApplication) getApplication()).getServiceHelper();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceHelper.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        serviceHelper.removeListener(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }

}
