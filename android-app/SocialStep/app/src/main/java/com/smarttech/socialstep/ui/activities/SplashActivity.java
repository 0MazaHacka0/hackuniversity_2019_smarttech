package com.smarttech.socialstep.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.smarttech.socialstep.R;

public class SplashActivity extends AppCompatActivity {

    /**
     * Just start login activity
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }
}
