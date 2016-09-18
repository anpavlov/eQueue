package com.sudo.equeue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.adapters.BeaconsRVAdapter;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.CustomSnackBar;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Format for configurating beacons:
 *
 *  URL http://equeue/id
 *
 *  id - id of queue
 *  example: http://equeue/159
 *
 */
public class FindBeaconsActivity extends NetBaseActivity implements BeaconConsumer, RangeNotifier {

    private int searchQueueRequestId = -1;

    private ArrayList<Beacon> beaconArrayList;
    private ArrayList<Queue> queues;
    private BeaconsRVAdapter adapter;

    private BeaconManager mBeaconManager;

    private RecyclerView beaconsRVList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.open_slide_in, R.anim.open_slide_out);
        setContentView(R.layout.activity_beacons);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Поиск очереди");
        }

        beaconsRVList = (RecyclerView) findViewById(R.id.beacons_rv);
        beaconsRVList.setLayoutManager(new LinearLayoutManager(FindBeaconsActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        mBeaconManager.bind(this);

        beaconArrayList = new ArrayList<>();
        queues = new ArrayList<>();
        adapter = new BeaconsRVAdapter(queues, FindBeaconsActivity.this);
        beaconsRVList.setAdapter(adapter);

    }

    private void gotQueue(Queue queue) {
        if (queue != null) {
            queues.add(queue);
            adapter.notifyDataSetChanged();
        }
    }

    public void searchForQueue(String id) {

        try {
            Integer qid = Integer.parseInt(id);
            searchQueueRequestId = getServiceHelper().getQueue(qid);
        } catch (NumberFormatException e) {
            RelativeLayout findQueueLayout= (RelativeLayout) findViewById(R.id.findQueueLayout);
            if(findQueueLayout != null) {
                CustomSnackBar.show(findQueueLayout, "Ошибка");
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == searchQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> gotQueue((Queue) obj), () -> {

            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.close_slide_in, R.anim.close_slide_out);
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon : beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                // This is a Eddystone-URL frame
                final String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                Log.d("Beacons", "I see a beacon transmitting a url: " + url +
                        " approximately " + beacon.getDistance() + " meters away.");
                if (url.lastIndexOf("equeue") != -1 && !beaconArrayList.contains(beacon)){
                    runOnUiThread(() -> {
                            beaconArrayList.add(beacon);
                    String number = url.substring(url.lastIndexOf('/') + 1);
                    searchForQueue(number);
                    });
                }

            }
        }
    }
}
