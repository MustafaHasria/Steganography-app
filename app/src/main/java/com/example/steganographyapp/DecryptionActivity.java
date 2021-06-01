package com.example.steganographyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.example.steganographyapp.R;
import com.example.steganographyapp.util.Constants;

import java.io.IOException;

public class DecryptionActivity extends AppCompatActivity implements TextDecodingCallback {
    //region Components
    Button decryptionButtonDecrypt;
    TextView decryptionEditTextText;
    ImageView decryptionImage;
    //endregion

    //region Variables
    Bitmap bitmap = null;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decryption);
        decryptionButtonDecrypt = findViewById(R.id.decryption_button_decrypt);
        decryptionEditTextText = findViewById(R.id.decryption_edit_text_text);
        decryptionImage = findViewById(R.id.decryption_image);

        decryptionButtonDecrypt.setOnClickListener(v -> {

            if (bitmap != null){

                ImageSteganography imageSteganography = new ImageSteganography("",
                        bitmap);

                TextDecoding textDecoding = new TextDecoding(this,
                        this);
                textDecoding.execute(imageSteganography);
            }else
                Toast.makeText(this, "please choose encrypted picture before", Toast.LENGTH_SHORT).show();
        });

        decryptionImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.PERMISSIONS_EXTERNAL_STORAGE);

            } else {
                chooseImage();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), selectedImageUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                decryptionImage.setImageBitmap(bitmap);
            }

        }
    }

    public void chooseImage() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.choose_image)),
                Constants.SELECT_FILE);
    }

    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography imageSteganography) {
        Toast.makeText(this, imageSteganography.getMessage() + "", Toast.LENGTH_SHORT).show();

    }
}