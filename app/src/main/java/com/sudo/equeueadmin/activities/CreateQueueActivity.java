package com.sudo.equeueadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.util.List;
import java.util.StringTokenizer;

public class CreateQueueActivity extends NetBaseActivity {

    private static final String SAVED_STATE_ID_CREATE = QueueApplication.prefix + ".CreateQueueActivity.saved.id_create_queue";

    private int createQueueRequestId = -1;
    private ArrayAdapter<String> adapter;
    private String[] tags = {
            "accounting",
    "airport",
            "amusement_park",
            "aquarium",
            "art_gallery",
            "atm",
            "bakery",
            "bank",
            "bar",
            "beauty_salon",
            "bicycle_store",
            "book_store",
            "bowling_alley",
            "bus_station",
            "cafe",
            "campground",
            "car_dealer",
            "car_rental",
            "car_repair",
            "car_wash",
            "casino",
            "cemetery",
            "church",
            "city_hall",
            "clothing_store",
            "convenience_store",
            "courthouse",
            "dentist",
            "department_store",
            "doctor",
            "electrician",
            "electronics_store",
            "embassy",
            "establishment",
            "finance",
            "fire_station",
            "florist",
            "food",
            "funeral_home",
            "furniture_store",
            "gas_station",
            "general_contractor",
            "grocery_or_supermarket",
            "gym",
            "hair_care",
            "hardware_store",
            "health",
            "hindu_temple",
            "home_goods_store",
            "hospital",
            "insurance_agency",
            "jewelry_store",
            "laundry",
            "lawyer",
            "library",
            "liquor_store",
            "local_government_office",
            "locksmith",
            "lodging",
            "meal_delivery",
            "meal_takeaway",
            "mosque",
            "movie_rental",
            "movie_theater",
            "moving_company",
            "museum",
            "night_club",
            "painter",
            "park",
            "parking",
            "pet_store",
            "pharmacy",
            "physiotherapist",
            "place_of_worship",
            "plumber",
            "police",
            "post_office",
            "real_estate_agency",
            "restaurant",
            "roofing_contractor",
            "rv_park",
            "school",
            "shoe_store",
            "shopping_mall",
            "spa",
            "stadium",
            "storage",
            "store",
            "subway_station",
            "synagogue",
            "taxi_stand",
            "train_station",
            "travel_agency",
            "university",
            "veterinary_care",
            "zoo"
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_queue);

        if (savedInstanceState != null) {
            createQueueRequestId = savedInstanceState.getInt(SAVED_STATE_ID_CREATE, -1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Создать очередь");
        }

        findViewById(R.id.btn_create).setOnClickListener(v -> createQueue());

        Spinner tagsSpinner = (Spinner) findViewById(R.id.tags_spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tags);
        tagsSpinner.setAdapter(adapter);
        tagsSpinner.setPrompt("Выбирите тэг");


    }

    private void createQueue() {
//        ((EditText) findViewById(R.id.name_field)).getText().toString();
        createQueueRequestId = getServiceHelper().createQueue(
                ((EditText) findViewById(R.id.name_field)).getText().toString(),
                ((EditText) findViewById(R.id.description_field)).getText().toString());
    }

    private void creationSuccess(Queue queue) {
        if (queue != null) {
            Intent intent = new Intent(CreateQueueActivity.this, AdminQueueActivity.class);
            intent.putExtra(AdminQueueActivity.EXTRA_QUEUE, queue);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error: queue is null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_STATE_ID_CREATE, createQueueRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == createQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> creationSuccess((Queue) obj), null);
        }
    }
}
