package com.example.server1.nfcv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_read, btn_write_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_read=findViewById(R.id.buttonRead);
        btn_write_main=findViewById(R.id.buttonWrite);

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(100);
                Intent intent=new Intent(MainActivity.this,ReadActivity.class);
                startActivity(intent);
            }
        });
        btn_write_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibe.vibrate(100);
                Intent intent=new Intent(MainActivity.this,WriteActivity.class);
                startActivity(intent);
            }
        });

    }


}