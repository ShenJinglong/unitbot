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

        /*
        Msg msg2 = new Msg(getResources().getString(R.string.robot_string_game_rule_text1)
                , Msg.TYPE_RECEIVE);
        Msg msg3 = new Msg(getResources().getString(R.string.robot_string_game_rule_text2)
                , Msg.TYPE_RECEIVE);
        Msg msg4 = new Msg(getResources().getString(R.string.robot_string_game_rule_text3)
                , Msg.TYPE_RECEIVE);


        */


        Msg msg2 = new Msg("hi~ 你好啊！我是来自 UNITETOP 公司的小维"
                , Msg.TYPE_RECEIVE);
        Msg msg3 = new Msg("现在让我来告诉你这个游戏怎么玩吧~"
                , Msg.TYPE_RECEIVE);
        Msg msg4 = new Msg("首先，你需要挑选一张卡片，插入插卡口中"
                , Msg.TYPE_RECEIVE);
        Msg msg5 = new Msg("那这些卡片都有什么用呐？？嘿嘿，我先不告诉你，你只需要知道："
                , Msg.TYPE_RECEIVE);
        Msg msg6 = new Msg("红色卡： 2 * n + 1"
                , Msg.TYPE_RECEIVE);
        Msg msg7 = new Msg("绿色卡： n + 4"
                , Msg.TYPE_RECEIVE);
        Msg msg8 = new Msg("蓝色卡： n ^ 2 / 3"
                , Msg.TYPE_RECEIVE);
        Msg msg9 = new Msg("hhh...不要被这些公式吓到哦！"
                , Msg.TYPE_RECEIVE);
        Msg msg10 = new Msg("进入游戏后，你和小维都会随机分到三张牌，这三张牌是从1～5中随机挑选的"
                , Msg.TYPE_RECEIVE);
        Msg msg11 = new Msg("然后就是...刚才你插入的那张卡，还记得它代表什么函数吗？"
                , Msg.TYPE_RECEIVE);
        Msg msg12 = new Msg("你出的每一张牌都会经过这个函数运算， 比如说你出一张 1, 然后你插入的是红卡， 那么你出的卡的实际数值就是...多少吖？？...你猜"
                , Msg.TYPE_RECEIVE);
        Msg msg13 = new Msg("hhh...就是 3 吖， 答对了吗？"
                , Msg.TYPE_RECEIVE);
        Msg msg14 = new Msg("然后我俩轮流出牌，比谁的牌大，三局两胜，谁赢了谁就有糖吃哦 ：）"
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

    }
}
