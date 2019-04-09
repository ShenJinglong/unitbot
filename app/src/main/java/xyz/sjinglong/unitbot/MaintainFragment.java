package xyz.sjinglong.unitbot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import xyz.sjinglong.unitbot.hardware.SerialDriver;

public class MaintainFragment extends Fragment {
    private SerialDriver serialDriver;

    private EditText editText;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maintain_fragment, container, false);

        editText = view.findViewById(R.id.maintain_fragment_serial_edit_text);
        button = view.findViewById(R.id.maintain_fragment_serial_button);

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
    }

    public void sendMessageToChatFragment(String text) {
        MainActivity mainActivity = (MainActivity)getActivity();
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg);
    }
}
