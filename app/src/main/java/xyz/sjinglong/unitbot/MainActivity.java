package xyz.sjinglong.unitbot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import xyz.sjinglong.unitbot.tuling.TuLingRobot;

/*
import static com.friendlyarm.FriendlyThings.HardwareControler.*;
import static com.friendlyarm.FriendlyThings.BoardType.*;
import static com.friendlyarm.FriendlyThings.SPIEnum.*;
import static com.friendlyarm.FriendlyThings.GPIOEnum.*;
import static com.friendlyarm.FriendlyThings.FileCtlEnum.*;
*/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // int i = getBoardType();
        // Log.d(TAG, "onCreate: " + i);
    }
}
