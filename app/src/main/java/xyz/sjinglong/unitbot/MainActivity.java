package xyz.sjinglong.unitbot;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import xyz.sjinglong.unitbot.hardware.SerialDriver;
import xyz.sjinglong.unitbot.utils.TTS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TTS.initTTS(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
        transaction.replace(R.id.functional_fragment_layout, new GameFragment());
        transaction.commit();

        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.closeTTS();
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }
}
