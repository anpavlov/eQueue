package com.sudo.equeueadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.util.List;

public class EditQueueActivity extends NetBaseActivity implements OnMapReadyCallback {

    private static final String SAVED_STATE_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.queue";
    private static final String SAVED_STATE_ID_SAVE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_save";

    private int saveInfoRequestId = -1;
    private int saveCoordsRequestId = 20;
    private Queue queueInfo;
    private GoogleMap mMap;
    private Marker mMapMaker;
    int RESULT_CODE = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_queue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Параметры очереди");
        }

        if (savedInstanceState == null) {
            queueInfo = (Queue) getIntent().getSerializableExtra(AdminQueueActivity.EXTRA_QUEUE);
//            Toast.makeText(this, queueInfo.getCoords(), Toast.LENGTH_LONG).show();
        } else {
            queueInfo = (Queue) savedInstanceState.getSerializable(SAVED_STATE_QUEUE);
            saveInfoRequestId = savedInstanceState.getInt(SAVED_STATE_ID_SAVE, -1);
        }

        if (queueInfo == null) {
            Toast.makeText(this, "Error: queue is null", Toast.LENGTH_LONG).show();
            finish();
        } else {
            ((EditText) findViewById(R.id.name_field)).setText(queueInfo.getName());
            ((EditText) findViewById(R.id.description_field)).setText(queueInfo.getDescription());
            findViewById(R.id.btn_save).setOnClickListener(v -> saveQueue());
            findViewById(R.id.btn_coords).setOnClickListener(v -> openMap());
        }


        MapView mapView = (MapView) findViewById(R.id.lite_map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
        if (queueInfo.getLatLng() == null) mapView.setVisibility(View.GONE);
    }

    private void saveQueue() {
        queueInfo.setName(((EditText) findViewById(R.id.name_field)).getText().toString());
        queueInfo.setDescription(((EditText) findViewById(R.id.description_field)).getText().toString());
        saveInfoRequestId = getServiceHelper().saveQueueInfo(queueInfo);
    }

    private void openMap() {

        Intent intent = new Intent(EditQueueActivity.this, MapActivity.class);

        if (queueInfo.getCoords() != null) {

            String[] coords = queueInfo.getCoords().split(",");
            Float lat = Float.valueOf(coords[0]);
            Float lon = Float.valueOf(coords[1]);

            intent.putExtra(MapActivity.EXTRA_SHOW_KEY, true);
            intent.putExtra(MapActivity.EXTRA_LATITUDE_KEY, lat);
            intent.putExtra(MapActivity.EXTRA_LONGITUDE_KEY, lon);
        } else {
            intent.putExtra(MapActivity.EXTRA_SHOW_KEY, false);
        }

        startActivityForResult(intent, RESULT_CODE);
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
        outState.putSerializable(SAVED_STATE_QUEUE, queueInfo);
        outState.putInt(SAVED_STATE_ID_SAVE, saveInfoRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == saveInfoRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, null, obj -> {
                Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra(AdminQueueActivity.EXTRA_QUEUE, queueInfo);
                setResult(RESULT_OK, intent);
                finish();
            }, null);
        }
        if (requestId == saveCoordsRequestId) {
//            Toast.makeText(this, "Координаты сохранены", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            double lat = data.getDoubleExtra(MapActivity.EXTRA_LATITUDE_KEY, 0);
            double lon = data.getDoubleExtra(MapActivity.EXTRA_LONGITUDE_KEY, 0);
            LatLng newPlace = new LatLng(lat, lon);
            if (mMapMaker != null) {
                mMapMaker.setPosition(newPlace);
            } else {
                mMapMaker = mMap.addMarker(new MarkerOptions().position(newPlace));
                findViewById(R.id.lite_map).setVisibility(View.VISIBLE);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlace, 15f));

            queueInfo.setCoords(String.valueOf(lat) + "," + String.valueOf(lon));
            saveCoordsRequestId = getServiceHelper().saveCoords(queueInfo);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setAllGesturesEnabled(false);

        if (queueInfo.getLatLng() != null) {
            LatLng place = queueInfo.getLatLng();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15f));
            mMapMaker = mMap.addMarker(new MarkerOptions().position(place));
        }
    }
}

//Intent intent = new Intent();
//intent.putExtra(EXTRA_VKUID, Integer.parseInt(arg.split("=")[1]));
//setResult(RESULT_OK, intent);
//finish();