package com.example.alex.inqueue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class QueueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Название очереди");
        }

        final Button enterButton = (Button) findViewById(R.id.enter);
        if(enterButton != null) {
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterButton.setVisibility(View.GONE);
                    Animation bottomUp = AnimationUtils.loadAnimation(QueueActivity.this,
                            R.anim.bottom_up);
                    ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.ticket);
                    if (hiddenPanel != null) {
                        hiddenPanel.startAnimation(bottomUp);
                        hiddenPanel.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }
}
