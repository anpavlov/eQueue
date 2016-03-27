package com.sudo.equeueadmin.fragments;//package com.sudo.equeue.fragments;
//
//import android.os.Bundle;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.sudo.equeue.R;
//import com.sudo.equeue.fragments.myqueues.MyAdminQueueListFragment;
//import com.sudo.equeue.fragments.myqueues.MyStandingQueueListFragment;
//import com.sudo.equeue.utils.QueueApplication;
//
//public class MyQueuesFragment extends Fragment {
//
//    public static final String TAG = QueueApplication.prefix + ".fragments.MyQueuesFragment";
////    public static final String ARGS_IS_MY = QueueApplication.prefix + ".fragments.args.is_my";
//
////    private int getQueueRequestId = -1;
//
////    private ArrayAdapter<String> adapter;
////    private List<String> queueNamesList = new ArrayList<>();
////    private List<Queue> queueList;
////    private boolean isMy;
//
//    private FragmentPagerAdapter pagerAdapter;
//    private ViewPager viewPager;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my_queues, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//
//        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
//        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
//        pagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
//
//        pager.setAdapter(pagerAdapter);
//        tabs.setupWithViewPager(pager);
//
//        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Мои очереди");
//        }
//
//
////        pagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
////        viewPager = (ViewPager) view.findViewById(R.id.pager);
////        viewPager.setAdapter(pagerAdapter);
////
////        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//    }
//
//
////    @Override
////    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
////        if (requestId == getQueueRequestId) {
////            getServiceHelper().handleResponse(getActivity(), resultCode, data, obj -> updateQueueList((QueueList) obj), NetService.RETURN_QUEUE_LIST);
////        }
////
////    }
//
//    public class TabsPagerAdapter extends FragmentPagerAdapter {
//
//        public TabsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int index) {
//
//            switch (index) {
//                case 0:
//                    // Top Rated fragment activity
//                    return new MyStandingQueueListFragment();
//                case 1:
//                    // Games fragment activity
//                    return new MyAdminQueueListFragment();
//            }
//
//            return null;
//        }
//
//        @Override
//        public int getCount() {
//            // get item count - equal to number of tabs
//            return 2;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//
//            switch (position) {
//                case 0:
//                    return MyStandingQueueListFragment.TAB_NAME;
//                case 1:
//                    return MyAdminQueueListFragment.TAB_NAME;
//            }
//            return super.getPageTitle(position);
//        }
//    }
//
//}