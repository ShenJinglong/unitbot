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

import java.util.ArrayList;
import java.util.List;

import xyz.sjinglong.unitbot.tuling.TuLingRobot;

public class ChatFragment extends Fragment {
    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private TuLingRobot tuLingRobot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        initMsgs();
        inputText = (EditText)view.findViewById(R.id.chat_fragment_edit_text);
        send = (Button)view.findViewById(R.id.chat_fragment_send_button);
        msgRecyclerView = (RecyclerView)view.findViewById(R.id.chat_fragment_recycler_view);
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
    }

    private void initMsgs() {
        Msg msg1 = new Msg("游戏规则", Msg.TYPE_RECEIVE);
        Msg msg2 = new Msg("巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉，乌拉乌拉乌拉乌拉乌拉乌拉乌拉乌拉"
                , Msg.TYPE_RECEIVE);
        msgList.add(msg1);
        msgList.add(msg2);
    }
}
