package com.example.messmate.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.messmate.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Handler to start the main activity after the duration
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }, 1500);

    }
}