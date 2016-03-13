package com.sudo.equeue.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.sudo.equeue.R;
import com.sudo.equeue.activities.QueueViewerActivity;
import com.sudo.equeue.fragments.myqueues.QueueListAbstractFragment;
import com.sudo.equeue.utils.QueueApplication;

public class FindQueueFragment extends QueueListAbstractFragment {

    public static final String TAG = QueueApplication.prefix + ".fragments.QueueListAbstractFragment";

    private EditText searchField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_queue, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchField = (EditText) view.findViewById(R.id.field_search);

        searchField.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                sendQuery();
                return true;
            }
            return false;
        });
    }

    private void sendQuery() {
        getQueueRequestId = getServiceHelper().findQueue(searchField.getText().toString());
    }

    @Override
    protected int makeRequest() {
        return getServiceHelper().findQueue(null);
    }

    @Override
    protected void onQueueCLick(int position) {
        Intent intent = new Intent(getActivity(), QueueViewerActivity.class);
        intent.putExtra(QueueViewerActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
        startActivity(intent);
    }
}
