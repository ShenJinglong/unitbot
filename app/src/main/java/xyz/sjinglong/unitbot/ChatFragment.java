package xyz.sjinglong.unitbot;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.ArrayList;
import java.util.List;

import xyz.sjinglong.unitbot.tuling.TuLingRobot;
import xyz.sjinglong.unitbot.utils.TTS;

public class ChatFragment extends Fragment {
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private TuLingRobot tuLingRobot;
    private QMUILinearLayout qmuiLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        initMsgs();
        inputText = (EditText)view.findViewById(R.id.chat_fragment_edit_text);
        send = (Button)view.findViewById(R.id.chat_fragment_send_button);
        msgRecyclerView = (RecyclerView)view.findViewById(R.id.chat_fragment_recycler_view);
        qmuiLinearLayout = (QMUILinearLayout)view.findViewById(R.id.chat_fragment_QMUILayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        tuLingRobot = new TuLingRobot(msgRecyclerView, adapter, msgList);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SEND);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                    tuLingRobot.chatWithTuLingRobot(getActivity(), content);
                }
            }
        });
        qmuiLinearLayout.setRadiusAndShadow(QMUIDisplayHelper.dp2px(getContext(), 35),
                QMUIDisplayHelper.dp2px(getContext(),14), 0.25f);
    }

    public void addMessage(Msg msg, int speakType) {
        msgList.add(msg);
        adapter.notifyItemInserted(msgList.size() - 1);
        while (msgList.size() > 40) {
            msgList.remove(14);
            adapter.notifyItemRemoved(14);
        }
        msgRecyclerView.scrollToPosition(msgList.size() - 1);

        if (speakType == TTS.TYPE_ADD)
            TTS.speakADD(msg.getContent());
        else if (speakType == TTS.TYPE_FLUSH)
            TTS.speakFLUSH(msg.getContent());
    }

    private void initMsgs() {
        Msg msg1 = new Msg(getResources().getString(R.string.robot_string_game_rule_title), Msg.TYPE_RECEIVE);

        Msg msg2 = new Msg(getResources().getString(R.string.rule_sentence1)
                , Msg.TYPE_RECEIVE);
        Msg msg3 = new Msg(getResources().getString(R.string.rule_sentence2)
                , Msg.TYPE_RECEIVE);
        Msg msg4 = new Msg(getResources().getString(R.string.rule_sentence3)
                , Msg.TYPE_RECEIVE);
        Msg msg5 = new Msg(getResources().getString(R.string.rule_sentence4)
                , Msg.TYPE_RECEIVE);
        Msg msg6 = new Msg(getResources().getString(R.string.rule_sentence5)
                , Msg.TYPE_RECEIVE);
        Msg msg7 = new Msg(getResources().getString(R.string.rule_sentence6)
                , Msg.TYPE_RECEIVE);
        Msg msg8 = new Msg(getResources().getString(R.string.rule_sentence7)
                , Msg.TYPE_RECEIVE);
        Msg msg9 = new Msg(getResources().getString(R.string.rule_sentence8)
                , Msg.TYPE_RECEIVE);
        Msg msg10 = new Msg(getResources().getString(R.string.rule_sentence9)
                , Msg.TYPE_RECEIVE);
        Msg msg11 = new Msg(getResources().getString(R.string.rule_sentence10)
                , Msg.TYPE_RECEIVE);
        Msg msg12 = new Msg(getResources().getString(R.string.rule_sentence11)
                , Msg.TYPE_RECEIVE);
        Msg msg13 = new Msg(getResources().getString(R.string.rule_sentence12)
                , Msg.TYPE_RECEIVE);
        Msg msg14 = new Msg(getResources().getString(R.string.rule_sentence13)
                , Msg.TYPE_RECEIVE);
        Msg msg15 = new Msg(getResources().getString(R.string.rule_sentence14)
                , Msg.TYPE_RECEIVE);

        msgList.add(msg1);
        msgList.add(msg2);
        msgList.add(msg3);
        msgList.add(msg4);
        msgList.add(msg5);
        msgList.add(msg6);
        msgList.add(msg7);
        msgList.add(msg8);
        msgList.add(msg9);
        msgList.add(msg10);
        msgList.add(msg11);
        msgList.add(msg12);
        msgList.add(msg13);
        msgList.add(msg14);
        msgList.add(msg15);

    }
}
