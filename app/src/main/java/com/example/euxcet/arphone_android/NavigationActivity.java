package com.example.euxcet.arphone_android;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class NavigationActivity extends AppCompatActivity {

    private final int RESULT_LOAD_IMAGE = 101;
    private Net net = Net.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }

    public void selectFromGallery(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void toImageActivity(Intent intent) {
        intent.setClass(NavigationActivity.this, ImageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                /*
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(myStream, false);
Bitmap region = decoder.decodeRegion(new Rect(10, 10, 50, 50), null);
                 */
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                byte[] byteData = os.toByteArray();
                net.sendImage(bitmap);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            toImageActivity(data);
        }
    }
}

