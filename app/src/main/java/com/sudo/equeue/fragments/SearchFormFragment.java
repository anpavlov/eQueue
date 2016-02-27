//package com.sudo.equeue.fragments;
//
//import android.app.Fragment;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.RadioGroup;
//
//import com.example.alex.headhunter.NetBaseActivity;
//import com.example.alex.headhunter.R;
//
//import java.util.ArrayList;
//
//
//public class SearchFormFragment extends Fragment {
//
//    public interface SearchButtonCallback {
//        void onSearchButtonClick();
//        void saveRequestId(int id);
//    }
//
//    private View fragmentView;
//    private final Uri CONTENT_SEARCH_RESULTS_URI = Uri.parse("content://com.example.alex.headhunter.provider/search_result");
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_search_form, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        fragmentView = view;
//        final Button searchButton = (Button) fragmentView.findViewById(R.id.search_btn);
//
//        searchButton.setOnClickListener(v -> searchButtonClick());
//    }
//
//    private void searchButtonClick() {
//        String text;
//        int areaId;
//        String experienceApiId;
//        ArrayList<String> employmentApiIds = new ArrayList<>(4);
//        ArrayList<String> scheduleApiIds = new ArrayList<>(4);
////        get search text
//        text = ((EditText) fragmentView.findViewById(R.id.text)).getText().toString();
////        get area id
////        just Moscow this time
//        areaId = 1;
////        get experience id
//        switch (((RadioGroup) fragmentView.findViewById(R.id.experience)).getCheckedRadioButtonId()) {
//            case R.id.exp0:
//                experienceApiId = "noExperience";
//                break;
//
//            case R.id.exp1:
//                experienceApiId = "between1And3";
//                break;
//
//            case R.id.exp2:
//                experienceApiId = "between3And6";
//                break;
//
//            case R.id.exp3:
//                experienceApiId = "moreThan6";
//                break;
//
//            default:
//                experienceApiId = "";
//                break;
//        }
////        get employment ids
//        if (((CheckBox) fragmentView.findViewById(R.id.emp0)).isChecked()) {
//            employmentApiIds.add("full");
//        }
//        if (((CheckBox) fragmentView.findViewById(R.id.emp1)).isChecked()) {
//            employmentApiIds.add("part");
//        }
//        if (((CheckBox) fragmentView.findViewById(R.id.emp2)).isChecked()) {
//            employmentApiIds.add("project");
//        }
//        if (((CheckBox) fragmentView.findViewById(R.id.emp3)).isChecked()) {
//            employmentApiIds.add("volunteer");
//        }
////        get schedule ids
//        if (((CheckBox) fragmentView.findViewById(R.id.sch0)).isChecked()) {
//            scheduleApiIds.add("fullDay");
//        }
//        if (((CheckBox) fragmentView.findViewById(R.id.sch1)).isChecked()) {
//            scheduleApiIds.add("shift");
//        }
//        if (((CheckBox) fragmentView.findViewById(R.id.sch2)).isChecked()) {
//            scheduleApiIds.add("flexible");
//        }
//        if (((CheckBox) fragmentView.findViewById(R.id.sch3)).isChecked()) {
//            scheduleApiIds.add("remote");
//        }
//        getActivity().getContentResolver().delete(CONTENT_SEARCH_RESULTS_URI, null, null);
//        int id = ((NetBaseActivity) getActivity()).getServiceHelper().makeSearch(text, areaId, experienceApiId, employmentApiIds, scheduleApiIds);
//        ((SearchButtonCallback) getActivity()).saveRequestId(id);
//        ((SearchButtonCallback) getActivity()).onSearchButtonClick();
//    }
//
//}
