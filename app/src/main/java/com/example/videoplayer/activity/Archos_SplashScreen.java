package com.example.videoplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.videoplayer.R;

public class Archos_SplashScreen extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archos_activity_splash2);
        getWindow().setFlags(1024, 1024);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Archos_SplashScreen.this, Archos_PermissionActivity.class));
            }
        }, 1500);
    }
}

