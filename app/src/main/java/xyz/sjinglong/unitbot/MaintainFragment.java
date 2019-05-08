package xyz.sjinglong.unitbot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.friendlyarm.FriendlyThings.GPIOEnum;
import com.friendlyarm.FriendlyThings.HardwareControler;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import xyz.sjinglong.unitbot.hardware.GPIODriver;
import xyz.sjinglong.unitbot.hardware.SerialDriver;

public class MaintainFragment extends Fragment {
    private SerialDriver serialDriver;

    private EditText editText;
    private Button button;

    private QMUIRoundButton baudButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maintain_fragment, container, false);

        editText = view.findViewById(R.id.maintain_fragment_serial_edit_text);
        button = view.findViewById(R.id.maintain_fragment_serial_button);
        baudButton = view.findViewById(R.id.maintain_fragment_baud);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        serialDriver = new SerialDriver((MainActivity)getActivity());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextString = editText.getText().toString();
                int send_result = serialDriver.sendMessage(editTextString);
                if (send_result > 0) {
                    sendMessageToChatFragment(editTextString);
                    sendMessageToChatFragment(getResources().getString(R.string.robot_string_maintain_sent_successfully));
                } else if (send_result == 0) {
                    sendMessageToChatFragment(getResources().getString(R.string.robot_string_maintain_edit_text_not_empty));
                } else {
                    sendMessageToChatFragment(editTextString);
                    sendMessageToChatFragment(getResources().getString(R.string.robot_string_maintain_failed_to_send));
                }
            }
        });

        baudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String []array = new String[] {
                        "4800",
                        "9600",
                        "19200",
                        "115200",
                        "921600"
                };
                ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                        new ArrayList<>(Arrays.asList(array)));
                QMUIListPopup mListPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
                mListPopup.create(QMUIDisplayHelper.dp2px(getContext(), 250),
                        QMUIDisplayHelper.dp2px(getContext(), 200),
                        (adapterView, view, i, l)->{
                            switch (i) {
                                case 0:
                                    serialDriver.setBuad(4800);
                                    break;
                                case 1:
                                    serialDriver.setBuad(9600);
                                    break;
                                case 2:
                                    serialDriver.setBuad(19200);
                                    break;
                                case 3:
                                    serialDriver.setBuad(115200);
                                    break;
                                case 4:
                                    serialDriver.setBuad(921600);
                                    break;
                                default:
                                    break;
                            }
                            mListPopup.dismiss();
                        });

                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.setPreferredDirection(0);
                mListPopup.show(v);
            }
        });
    }

    public void sendMessageToChatFragment(String text) {
        MainActivity mainActivity = (MainActivity)getActivity();
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg);
    }
}
