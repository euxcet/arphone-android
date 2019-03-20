package com.example.euxcet.arphone_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class ImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Image");
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        ZoomImageView zoomImageView = findViewById(R.id.zoomImageView);
        zoomImageView.setImageURI(uri);
    }


}
