package xyz.sjinglong.unitbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.sjinglong.unitbot.hardware.SerialDriver;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
