package xyz.sjinglong.unitbot.utils;

import android.widget.Toast;

import java.util.ArrayList;

import xyz.sjinglong.unitbot.ChatFragment;
import xyz.sjinglong.unitbot.MainActivity;
import xyz.sjinglong.unitbot.Msg;
import xyz.sjinglong.unitbot.R;

public class SerialMessageHandler {

    private MainActivity mainActivity;

    private int redValue;
    private int greenValue;
    private int blueValue;
    private int distanceValue;

    public int getRedValue() {
        return redValue;
    }

    public int getGreenValue() {
        return greenValue;
    }

    public int getBlueValue() {
        return blueValue;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public void parseMessage(String message) {

        char[] charArray = message.toCharArray();
        ArrayList<Character> dataBag = new ArrayList<Character>();
        int i = 1;
        while (charArray.length - i - 1 >= 0 && charArray[charArray.length - i - 1] != 'D' && charArray[charArray.length - i] != 'B') {
            dataBag.add(new Character(charArray[charArray.length - i]));
            ++i;
        }

        ArrayList<StringBuilder> datas = new ArrayList<StringBuilder>();

        datas.add(new StringBuilder());
        datas.add(new StringBuilder());
        datas.add(new StringBuilder());
        datas.add(new StringBuilder());

        int dataID = -1;

        for (int j = dataBag.size() - 1; j >= 0; --j) {
            if (dataBag.get(j) == ' ') {
                ++dataID;
            } else if (dataBag.get(j) == 'F' && dataBag.get(j - 1) == 'E') {
                break;
            } else {
                datas.get(dataID).append(dataBag.get(j));
            }
        }

        redValue = Integer.parseInt(datas.get(0).toString());
        greenValue = Integer.parseInt(datas.get(1).toString());
        blueValue = Integer.parseInt(datas.get(2).toString());
        distanceValue = Integer.parseInt(datas.get(3).toString());
    }

    public SerialMessageHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void sendMessageToChatFragment(String text) {
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg);
    }
}