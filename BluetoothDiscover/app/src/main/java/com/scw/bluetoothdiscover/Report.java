package com.scw.bluetoothdiscover;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Report extends AppCompatActivity {
    private static String ipAddress;

    private static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri ImageUri;
    private Button takePhoto;
    private EditText message;
    HttpRequest httpRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();

        ipAddress = intent.getStringExtra("ipAddress");
        httpRequest = new HttpRequest(this, ipAddress);
        message = (EditText) findViewById(R.id.message);
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto = (Button) findViewById(R.id.takephoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //启动相机程序
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
                //startActivityForResult(intent, TAKE_PHOTO);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            picture.setImageBitmap(photo);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
            byte[] outArray = out.toByteArray();
            httpRequest.sendPhoto(outArray, message.getText().toString());
        }
    }


}
