//package com.sudo.equeue.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.View;
//import android.webkit.WebView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.sudo.equeue.NetBaseActivity;
//import com.sudo.equeue.R;
//import com.sudo.equeue.models.Vacancy;
//
////import com.example.alex.headhunter.NetBaseActivity;
////import com.example.alex.headhunter.R;
////import com.example.alex.headhunter.models.Vacancy;
//
//public class VacancyActivity extends NetBaseActivity {
//
//    public static final String EXTRA_VACANCY_ID = "com.example.alex.headhunter.extra.VACANCY_ID";
//
//    private int vacancyId;
//    private int requestId;
//    private Vacancy vacancy;
//
//    private TextView nameView;
//    private TextView companyView;
//    private TextView salaryView;
//    private TextView areaView;
//    private TextView experienceView;
//    private TextView salaryText;
//    private TextView areaText;
//    private TextView experienceText;
//    private WebView descriptionView;
//
//    private ProgressBar progressBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_vacancy);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress);
//        progressBar.setVisibility(View.VISIBLE);
//
//        Intent incoming_intent = getIntent();
//        if (incoming_intent != null) {
//            vacancyId = incoming_intent.getIntExtra(EXTRA_VACANCY_ID, -1);
//        }
//        if (vacancyId != -1) {
//            requestId = getServiceHelper().getVacancy(vacancyId);
//        } else {
//            progressBar.setVisibility(View.INVISIBLE);
//        }
//
//        nameView = (TextView) findViewById(R.id.name);
//        companyView = (TextView) findViewById(R.id.company);
//        salaryView = (TextView) findViewById(R.id.salary);
//        areaView = (TextView) findViewById(R.id.area);
//        experienceView = (TextView) findViewById(R.id.experience);
//        descriptionView = (WebView) findViewById(R.id.description);
//        salaryText = (TextView) findViewById(R.id.salaryText);
//        areaText = (TextView) findViewById(R.id.areaText);
//        experienceText = (TextView) findViewById(R.id.experienceText);
//
//    }
//
//    @Override
//    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
//        if (requestId == this.requestId) {
//            if (resultCode == 200) {
//                vacancy = (Vacancy) data.getSerializable("vacancy");
//                updateVacancyInfo();
//            }
//        }
//    }
//
//    private void updateVacancyInfo() {
//        if (vacancy != null) {
//            if (vacancy.getName() != null) {
//                nameView.setText(vacancy.getName());
//            }
//            if (vacancy.getEmployer() != null) {
//                companyView.setText(vacancy.getEmployer().getName());
//            }
//            if (vacancy.getArea() != null) {
//                areaView.setText(vacancy.getArea().getName());
//            }
//            if (vacancy.getExperience() != null) {
//                experienceView.setText(vacancy.getExperience().getName());
//            }
//            if (vacancy.getDescription() != null) {
//                descriptionView.loadData(vacancy.getDescription(), "text/html; charset=utf-8", null);
//            }
//            if (vacancy.getSalary() != null) {
//                String salaryString = "";
//                if (vacancy.getSalary().getFrom() != null) {
//                    salaryString += "от " + vacancy.getSalary().getFrom();
//                }
//                if (vacancy.getSalary().getTo() != null) {
//                    salaryString += " до " + vacancy.getSalary().getTo();
//                }
//                if (vacancy.getSalary().getCurrency().equals("RUR")) {
//                    salaryString += " руб.";
//                }
//                salaryView.setText(salaryString);
//                salaryText.setVisibility(View.VISIBLE);
//            } else {
//                salaryText.setVisibility(View.GONE);
//                salaryView.setVisibility(View.GONE);
//            }
//            areaText.setVisibility(View.VISIBLE);
//            experienceText.setVisibility(View.VISIBLE);
//        }
//        progressBar.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
