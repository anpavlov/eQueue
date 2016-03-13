package com.sudo.equeue;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.ServiceCallbackListener;
import com.sudo.equeue.utils.ServiceHelper;

public abstract class NetBaseFragment extends Fragment implements ServiceCallbackListener {

    private ServiceHelper serviceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = ((QueueApplication) getActivity().getApplication()).getServiceHelper();

        if (serviceHelper == null) {
            ((QueueApplication) getActivity().getApplication()).initServiceHelper();
            serviceHelper = ((QueueApplication) getActivity().getApplication()).getServiceHelper();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceHelper.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceHelper.removeListener(this);
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }
}
