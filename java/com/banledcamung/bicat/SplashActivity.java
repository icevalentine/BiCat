package com.banledcamung.bicat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressBar = findViewById(R.id.progressBar);

        countDownTimer = new CountDownTimer(1500,100){

            @Override
            public void onTick(long l) {
                int currentProgress = 15-(int)l/100;
                progressBar.setProgress(currentProgress);

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}