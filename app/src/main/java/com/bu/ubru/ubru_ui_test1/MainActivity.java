package com.bu.ubru.ubru_ui_test1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testInfoMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), TestInfoActivity.class);
        startActivity(intent);
    }

    public void arduinoMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), ArduinoTestActivity.class);
        startActivity(intent);
    }

    public void dbMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), DbTestActivity.class);
        startActivity(intent);
    }


}