package com.example.steganographyapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //region Components
    Button mainButtonDecryption;
    Button mainButtonEncryption;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainButtonDecryption = findViewById(R.id.main_button_decryption);
        mainButtonEncryption = findViewById(R.id.main_button_encryption);
        mainButtonEncryption.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EncryptionActivity.class)));
        mainButtonDecryption.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DecryptionActivity.class)));

    }
}