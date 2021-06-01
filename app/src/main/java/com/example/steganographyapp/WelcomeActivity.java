package com.example.steganographyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    //region Components
    Button mainButtonGo;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mainButtonGo = findViewById(R.id.main_button_go);
        mainButtonGo.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}