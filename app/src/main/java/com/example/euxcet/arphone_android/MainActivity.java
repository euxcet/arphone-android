package com.example.euxcet.arphone_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Net net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void toNavigationActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    public void onConnectClick(View v) {
        String ip = ((EditText)findViewById(R.id.text_ip)).getText().toString();
        int port = Integer.parseInt(((EditText)findViewById(R.id.text_port)).getText().toString());
        if (net != null) net.close();
        net = Net.getInstance(ip, port);
        net.connect();
        toNavigationActivity();
    }
}
