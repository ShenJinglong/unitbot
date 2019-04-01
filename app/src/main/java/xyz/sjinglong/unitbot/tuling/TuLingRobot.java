package xyz.sjinglong.unitbot.tuling;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import xyz.sjinglong.unitbot.Msg;
import xyz.sjinglong.unitbot.MsgAdapter;
import xyz.sjinglong.unitbot.R;
import xyz.sjinglong.unitbot.utils.OkHttpUtils;

public class TuLingRobot {
    private static final String tuLingUrl = "https://openapi.tuling123.com/openapi/api/v2";
    private static final String userId = "330920";
    private static final String apiKey = "9abf287d475441f58d479af534cf60f0";
    private static final String TAG = "TuLingRobot";
    private static OkHttpUtils okHttpUtils = new OkHttpUtils();

    private RecyclerView recyclerView;
    private MsgAdapter adapter;
    private List<Msg> mmlist;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String handlerData = msg.getData().getString("response", null);
            Msg msg1 = new Msg(handlerData, Msg.TYPE_RECEIVE);
            mmlist.add(msg1);
            adapter.notifyItemInserted(mmlist.size() - 1);
            recyclerView.scrollToPosition(mmlist.size() - 1);
        }
    };

    public TuLingRobot(RecyclerView recyclerView, MsgAdapter adapter, List<Msg> mmlist) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.mmlist = mmlist;
    }

    public void chatWithTuLingRobot(Activity activity, String text) {
        String jsonRequest = getTuLingJsonRequest(text);
        try {
            okHttpUtils.postJson(tuLingUrl, jsonRequest, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        String jsonResponse = response.body().string();
                        String tuLingResponse = parseTuLingJsonResponse(jsonResponse);
                        activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), tuLingResponse, Toast.LENGTH_SHORT).show());
                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putString("response", tuLingResponse);
                        message.setData(data);
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "网络错误！", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
