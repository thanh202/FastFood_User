package com.fpt.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.fpt.foodapp.R;

public class IntroActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);



        //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000);

    }


}