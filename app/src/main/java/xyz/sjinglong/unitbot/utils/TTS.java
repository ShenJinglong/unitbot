package xyz.sjinglong.unitbot.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class TTS {
    public static int TYPE_SLIENT = 0;
    public static int TYPE_FLUSH = 10;
    public static int TYPE_ADD = 20;

    private static TextToSpeech mTTS;
    private static float pitch;
    private static float speachRate;

    public static void initTTS(Context mContext) {
        mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int supported = mTTS.setLanguage(Locale.US);
                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(mContext, "不支持当前语言！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mTTS.setPitch(1.0f);
        pitch = 1.0f;
        mTTS.setSpeechRate(1.0f);
        speachRate = 1.0f;
    }

    public static void setPitch(float value) {
        mTTS.setPitch(value);
        pitch = value;
    }

    public static void setSpeechRate(float value) {
        mTTS.setSpeechRate(value);
        speachRate = value;
    }

    public static float getPitch() {
        return pitch;
    }

    public static float getSpeachRate() {
        return speachRate;
    }

    public static void speakFLUSH(String words) {
        mTTS.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    }

    public static void speakADD(String words) {
        mTTS.speak(words, TextToSpeech.QUEUE_ADD, null);
    }

    public static void closeTTS() {
        if (mTTS != null) {
            mTTS.shutdown();
        }
    }
}
