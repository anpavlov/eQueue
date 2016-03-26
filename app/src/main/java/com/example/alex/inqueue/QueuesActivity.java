package com.example.alex.inqueue;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QueuesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queues);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Очереди");
        }

        Button addQueue = (Button) findViewById(R.id.addQueue);
        if(addQueue != null) {
            addQueue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBottomSheet();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                openBottomSheet();
                break;
            default:
                break;
        }

        return true;
    }

    public void openBottomSheet() {
        View view = getLayoutInflater().inflate (R.layout.bottom_sheet, null);
        TextView txtQR = (TextView)view.findViewById(R.id.qr_code);
        TextView txtID = (TextView)view.findViewById(R.id.enter_id);
        TextView txtNearby = (TextView)view.findViewById(R.id.findNearby);

        final Dialog mBottomSheetDialog = new Dialog (QueuesActivity.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        txtQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QueuesActivity.this, "QR", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        txtID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QueuesActivity.this, FindQueueActivity.class);
                startActivity(intent);
                mBottomSheetDialog.dismiss();
            }
        });

        txtNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search nearby queues
                mBottomSheetDialog.dismiss();
            }
        });


    }
}
