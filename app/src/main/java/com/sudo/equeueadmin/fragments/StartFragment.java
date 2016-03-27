package com.sudo.equeueadmin.fragments;//package com.sudo.equeue.fragments;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.sudo.equeue.R;
////import com.sudo.equeue.activities.MainActivity;
//import com.sudo.equeue.activities.QueueAdminActivity;
//
//
//public class StartFragment extends Fragment {
//
//    public interface StartFragmentListener {
//        void findQueueButtonCallback();
//    }
//
//    public static final String TAG = "com.sudo.fragments.StartFragment";
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_start, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        view.findViewById(R.id.btn_create_queue).setOnClickListener(v ->
//        {
//            Intent intent = new Intent(getActivity(), QueueAdminActivity.class);
//            intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, true);
//            startActivity(intent);
//        });
//
//        view.findViewById(R.id.btn_find_queue).setOnClickListener(v ->
//                ((StartFragmentListener) getActivity()).findQueueButtonCallback());
//
//
//    }
//}
