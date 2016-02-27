//package com.sudo.equeue.fragments;
//
//import android.app.Fragment;
//import android.app.LoaderManager;
//import android.content.CursorLoader;
//import android.content.Intent;
//import android.content.Loader;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.widget.SimpleCursorAdapter;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.ListView;
//
//import com.example.alex.headhunter.NetBaseActivity;
//import com.example.alex.headhunter.R;
//import com.example.alex.headhunter.activities.VacancyActivity;
//import com.example.alex.headhunter.content.contracts.SearchResultContract;
//
//public class SearchResultsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
//
//    public interface UpdateListCallback {
//        void onListUpdateStarted(int id);
//    }
//
//    private final int LOADER_ID = 0;
//    private final Uri CONTENT_URI = Uri.parse("content://com.example.alex.headhunter.provider/search_result");
//
//    private SimpleCursorAdapter simpleCursorAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_search_results, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        String[] from = new String[] {
//                SearchResultContract.SearchResultEntry.COLUMN_NAME_NAME,
//                SearchResultContract.SearchResultEntry.COLUMN_NAME_EMPLOYER_NAME
//        };
//
//        int[] to = new int[] {
//                R.id.vacancy_name,
//                R.id.vacancy_employer_name
//        };
//
//        ListView listView = (ListView) view.findViewById(R.id.list);
//        simpleCursorAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.result_item,
//                null, from, to, 0
//        );
//        listView.setAdapter(simpleCursorAdapter);
//
//        listView.setOnItemClickListener((parent, view1, position, id) -> {
//            Cursor cursor = simpleCursorAdapter.getCursor();
//            cursor.moveToPosition(position);
//            int vac_id = cursor.getInt(1);
//            Intent i = new Intent(getActivity(), VacancyActivity.class);
//            i.putExtra(VacancyActivity.EXTRA_VACANCY_ID, vac_id);
//            startActivity(i);
//        });
//
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                int threshold = 1;
//                int count = listView.getCount();
//
//                if (scrollState == SCROLL_STATE_IDLE) {
//                    if (listView.getLastVisiblePosition() >= count - threshold) {
//                        int id = ((NetBaseActivity) getActivity()).getServiceHelper().addPageToResults();
//                        if (id != -1) {
//                            ((UpdateListCallback) getActivity()).onListUpdateStarted(id);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
//
//        getActivity().getLoaderManager().initLoader(LOADER_ID, null, this);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        if (id == LOADER_ID) {
//            return new CursorLoader(getActivity(), CONTENT_URI, new String[]{
//                    "_ID",
//                    SearchResultContract.SearchResultEntry.COLUMN_NAME_VACANCY_ID,
//                    SearchResultContract.SearchResultEntry.COLUMN_NAME_NAME,
//                    SearchResultContract.SearchResultEntry.COLUMN_NAME_EMPLOYER_NAME
//            }, null, null, null);
//        }
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (loader.getId() == LOADER_ID) {
//            simpleCursorAdapter.swapCursor(data);
//            simpleCursorAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        if (loader.getId() == LOADER_ID) {
//            simpleCursorAdapter.swapCursor(null);
//            simpleCursorAdapter.notifyDataSetChanged();
//        }
//    }
//
//
////    public void onListItemClick(ListView l, View v, int position, long id) {
////        super.onListItemClick(l, v, position, id);
////        Toast.makeText(getActivity(), "position = " + position, Toast.LENGTH_SHORT).show();
////    }
//}
