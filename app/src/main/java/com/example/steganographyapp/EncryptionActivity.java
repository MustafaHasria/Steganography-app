package com.example.steganographyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;
import com.example.steganographyapp.util.Constants;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class EncryptionActivity extends AppCompatActivity implements TextEncodingCallback{

    //region Components
    Button encryptionButtonEncrypt;
    TextView encryptionTextChoose;
    ImageView encryptionImage;
    RadioButton encryptionRadioButtonPdf;
    RadioButton encryptionRadioButtonText;
    RadioButton encryptionRadioButtonPicture;
    RadioButton encryptionRadioButtonApk;
    CardView encryptionCard;
    EditText encryptionEditTextText;
    //endregion

    //region Variables
    Bitmap bitmap = null;

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);
        encryptionButtonEncrypt = findViewById(R.id.encryption_button_encrypt);
        encryptionRadioButtonPdf = findViewById(R.id.encryption_radio_button_pdf);
        encryptionRadioButtonText = findViewById(R.id.encryption_radio_button_text);
        encryptionRadioButtonPicture = findViewById(R.id.encryption_radio_button_picture);
        encryptionRadioButtonApk = findViewById(R.id.encryption_radio_button_apk);
        encryptionEditTextText = findViewById(R.id.encryption_edit_text_text);
        encryptionCard = findViewById(R.id.encryption_card);
        encryptionImage = findViewById(R.id.encryption_image);
        encryptionTextChoose = findViewById(R.id.encryption_text_choose);

        encryptionCard.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(EncryptionActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.PERMISSIONS_EXTERNAL_STORAGE);

            } else {
                chooseImage();
            }

        });


        encryptionRadioButtonPdf.setOnClickListener(v -> {
            hideText();

        });
        encryptionRadioButtonText.setOnClickListener(v -> {
            encryptionEditTextText.setVisibility(View.VISIBLE);
        });
        encryptionRadioButtonPicture.setOnClickListener(v -> {
            hideText();

        });
        encryptionRadioButtonApk.setOnClickListener(v -> {
            hideText();

        });

        encryptionButtonEncrypt.setOnClickListener(v -> {
            ImageSteganography imageSteganography = new ImageSteganography("mustafa",
                    "",
                    bitmap);
            TextEncoding textEncoding = new TextEncoding(this,
                    this);
            textEncoding.execute(imageSteganography);


        });


    }


    private void hideText() {
        encryptionEditTextText.setVisibility(View.GONE);
        encryptionEditTextText.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
//            case Constants.PERMISSIONS_CAMERA:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    openCamera();
//                }
//                break;
            case Constants.PERMISSIONS_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                }
                break;
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
                encryptionImage.setImageBitmap(bitmap);
            }

        }
    }

    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        Toast.makeText(this, result.getMessage() + "", Toast.LENGTH_SHORT).show();

        if (result.isEncoded()) {

            //encrypted image bitmap is extracted from imageSteganography object
            Bitmap encoded_image = result.getEncoded_image();

            //set text and image to the UI component.
            encryptionImage.setImageBitmap(encoded_image);
            File sd = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
            String imageFile = "image" +  Calendar.getInstance().getTime() + ".png";

            File directory = new File(sd.getAbsolutePath() + "/steganography/");
            if (!directory.isDirectory()) {
                directory.mkdir();
                directory.mkdirs();
            }
            File file = new File(directory, imageFile);

            try {
                file.createNewFile();
                FileOutputStream oStream = new FileOutputStream(file);
                encoded_image.compress(Bitmap.CompressFormat.PNG, 100, oStream);
                oStream.flush();
                oStream.close();
                Toast.makeText(this, "Save Image Successfully..", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("TAG", "There was an issue saving the image.");
            }
        }
    }
}