package xyz.sjinglong.unitbot.hardware;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.friendlyarm.FriendlyThings.HardwareControler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import xyz.sjinglong.unitbot.ChatFragment;
import xyz.sjinglong.unitbot.MainActivity;
import xyz.sjinglong.unitbot.Msg;
import xyz.sjinglong.unitbot.R;

import static android.app.PendingIntent.getActivity;

public class SerialDriver {

    private String serialMessage = "DE 0 0 0 0 FE";

    private MainActivity mainActivity;

    private int devfd = -1;
    private static int baud = 9600;
    private int dataBits = 8;
    private int stopBits = 1;
    private String devName = "/dev/ttyAMA3";

    private final int BUFSIZE = 512;
    private byte[] buf = new byte[BUFSIZE];
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
                    if (HardwareControler.select(devfd, 0, 0) == 1) {
                        int retSize = HardwareControler.read(devfd, buf, BUFSIZE);
                        if (retSize > 0) {
                            String str = new String(buf, 0, retSize);
                            sendMessageToChatFragment(str);
                            serialMessage = str;
                        }
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public SerialDriver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        devfd = HardwareControler.openSerialPort(devName, SerialDriver.baud, dataBits, stopBits);
        if (devfd >= 0) {
            timer.schedule(task, 0, 500);
        } else {
            devfd = -1;
            sendMessageToChatFragment(mainActivity.getResources().getString(R.string.robot_string_serial_open_failed_text));
        }
    }

    public void closeSerial() {
        timer.cancel();
        if (devfd != -1) {
            HardwareControler.close(devfd);
            devfd = -1;
        }
    }

    public int sendMessage(String text) {
        if (text.length() > 0) {
            int ret = HardwareControler.write(devfd, text.getBytes());
            if (ret > 0)
                return ret;
            else
                return -1;
        }
        return 0;
    }

    public int setBuad(int baud) {
        closeSerial();
        devfd = HardwareControler.openSerialPort(devName, baud, dataBits, stopBits);
        if (devfd >= 0) {
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
            timer.schedule(task, 0, 500);
            SerialDriver.baud = baud;
            sendMessageToChatFragment("波特率设置成功，当前波特率为：" + this.baud);
            return 1;
        } else {
            devfd = -1;
            sendMessageToChatFragment("重启串口失败");
            return 0;
        }
    }

    public String getSerialMessage() {
        return this.serialMessage;
    }

    private void sendMessageToChatFragment(String text) {
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg);
    }
}
