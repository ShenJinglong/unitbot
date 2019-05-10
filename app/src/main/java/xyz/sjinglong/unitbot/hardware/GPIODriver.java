package xyz.sjinglong.unitbot.hardware;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.friendlyarm.FriendlyThings.GPIOEnum;
import com.friendlyarm.FriendlyThings.HardwareControler;

import java.util.Timer;
import java.util.TimerTask;

import xyz.sjinglong.unitbot.ChatFragment;
import xyz.sjinglong.unitbot.MainActivity;
import xyz.sjinglong.unitbot.Msg;
import xyz.sjinglong.unitbot.R;

public class GPIODriver {
    private final int pinLeft = 96;
    private final int pinRight = 97;
    private final int pinMid = 77;
    private int currentRockerStatus = 000;
    private int receiveControlFlag = 0;

    private MainActivity mainActivity;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int leftStatus = HardwareControler.getGPIOValue(pinLeft) == GPIOEnum.HIGH ? 0 : 1;
                    int rightStatus = HardwareControler.getGPIOValue(pinRight) == GPIOEnum.HIGH ? 0 : 1;
                    int midStatus = HardwareControler.getGPIOValue(pinMid) == GPIOEnum.HIGH ? 0 : 1;
                    currentRockerStatus = leftStatus * 100 + midStatus * 10 + rightStatus;
            }
        }
    };

    public GPIODriver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        if (HardwareControler.exportGPIOPin(pinLeft) != 0) {
            sendMessageToChatFragment("设置Leftpin失败！");
        }
        if (HardwareControler.exportGPIOPin(pinMid) != 0) {
            sendMessageToChatFragment("设置Midpin失败！");
        }
        if (HardwareControler.exportGPIOPin(pinRight) != 0) {
            sendMessageToChatFragment("设置Rightpin失败！");
        }

        if (HardwareControler.setGPIODirection(pinLeft, GPIOEnum.OUT) != 0) {
            sendMessageToChatFragment("设置Leftpin方向失败！");
        }
        if (HardwareControler.setGPIODirection(pinMid, GPIOEnum.OUT) != 0) {
            sendMessageToChatFragment("设置Midpin方向失败！");
        }
        if (HardwareControler.setGPIODirection(pinRight, GPIOEnum.OUT) != 0) {
            sendMessageToChatFragment("设置Rightpin方向失败！");
        }

        HardwareControler.setGPIOValue(pinLeft, GPIOEnum.HIGH);
        HardwareControler.setGPIOValue(pinMid, GPIOEnum.HIGH);
        HardwareControler.setGPIOValue(pinRight, GPIOEnum.HIGH);

        timer.schedule(task, 300, 50);
    }

    public int getCurrentRockerStatus() {
        return this.currentRockerStatus;
    }

    public void closeGPIO() {
        timer.cancel();
    }

    private void sendMessageToChatFragment(String text) {
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg);
    }
}
