package xyz.sjinglong.unitbot.tuling;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import xyz.sjinglong.unitbot.utils.OkHttpUtils;

public class TuLingRobot {
    private static final String tuLingUrl = "https://openapi.tuling123.com/openapi/api/v2";
    private static final String userId = "330920";
    private static final String apiKey = "9abf287d475441f58d479af534cf60f0";
    private static final String TAG = "TuLingRobot";

    private static OkHttpUtils okHttpUtils = new OkHttpUtils();

    public void chatWithTuLingRobot(String text) {
        new Thread() {
            @Override
            public void run() {
                String jsonRequest = getTuLingJsonRequest(text);
                String jsonResponse = "";
                try {
                    jsonResponse = okHttpUtils.postJson(tuLingUrl, jsonRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Log.d(TAG, "run: " + jsonResponse);

                String tuLingResponse = parseTuLingJsonResponse(jsonResponse);
                Log.d(TAG, "run: " + tuLingResponse);
            }
        }.start();
    }

    private String getTuLingJsonRequest(String text) {
        TuLingRequest tuLingRequest = new TuLingRequest();
        tuLingRequest.setReqType(0);
        tuLingRequest.getPerception().getInputText().setText(text);
        tuLingRequest.getUserInfo().setApiKey(apiKey);
        tuLingRequest.getUserInfo().setUserId(userId);

        Gson gson = new Gson();
        String jsonText = gson.toJson(tuLingRequest);
        // Log.d(TAG, "chatWithTuLingRobot: " + jsonText);
        return jsonText;
    }

    private String parseTuLingJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        TuLingResponse tuLingResponse = gson.fromJson(jsonResponse, TuLingResponse.class);
        return tuLingResponse.getResults().get(0).getValues().getText();
    }
}
