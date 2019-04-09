package xyz.sjinglong.unitbot;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import xyz.sjinglong.unitbot.game.GameComputer;
import xyz.sjinglong.unitbot.game.GameMaster;
import xyz.sjinglong.unitbot.game.GameUser;
import xyz.sjinglong.unitbot.hardware.SerialDriver;

public class GameFragment extends Fragment {
    private static final String TAG = "GameFragment";

    private Button beginGameButton;

    private Button leftButton;
    private Button middleButton;
    private Button rightButton;

    private Button leftBuffButton;
    private Button middleBuffButton;
    private Button rightBuffButton;

    private Button tryAgainButton;
    private Button gameOverButton;

    private TextView leftText;
    private TextView middleText;
    private TextView rightText;

    private LinearLayout gameLayout;
    private FrameLayout beginLayout;
    private FrameLayout endLayout;

    private GameComputer gameComputer;
    private GameUser gameUser;


    private SerialDriver serialDriver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        beginGameButton = view.findViewById(R.id.game_fragment_button_begin_geme);
        leftButton = view.findViewById(R.id.game_fragment_button_left);
        middleButton = view.findViewById(R.id.game_fragment_button_middle);
        rightButton = view.findViewById(R.id.game_fragment_button_right);
        tryAgainButton = view.findViewById(R.id.game_fragment_button_try_again);
        gameOverButton = view.findViewById(R.id.game_fragment_button_game_over);
        beginLayout = view.findViewById(R.id.frame_begin);
        gameLayout = view.findViewById(R.id.frame_game);
        endLayout = view.findViewById(R.id.frame_result);

        leftText = view.findViewById(R.id.game_fragment_text_left);
        middleText = view.findViewById(R.id.game_fragment_text_middle);
        rightText = view.findViewById(R.id.game_fragment_text_right);

        leftBuffButton = view.findViewById(R.id.game_fragment_button_buff_left);
        middleBuffButton = view.findViewById(R.id.game_fragment_button_buff_center);
        rightBuffButton = view.findViewById(R.id.game_fragment_button_buff_right);

        setLayoutVisibility(1);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        beginGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutVisibility(2);

                gameComputer = new GameComputer();
                gameUser = new GameUser();

                GameMaster.beginGame((GameFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout));

                gameUser.setBuff(0);
                gameComputer.setBuff();

                leftBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                middleBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                rightBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonBefore));

                leftText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                middleText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                rightText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));

                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButtonHandler(1);
                leftButton.setVisibility(View.GONE);
            }
        });

        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButtonHandler(2);
                middleButton.setVisibility(View.GONE);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButtonHandler(3);
                rightButton.setVisibility(View.GONE);
            }
        });

        leftBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    leftBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonAfter));
                }
                gameUser.setBuff(1);
                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
            }
        });

        middleBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    middleBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonAfter));
                }
                gameUser.setBuff(2);
                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
            }
        });

        rightBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    rightBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonAfter));
                }
                gameUser.setBuff(3);
                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutVisibility(2);
                gameUser = new GameUser();
                gameComputer = new GameComputer();
                GameMaster.beginGame((GameFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout));

                gameUser.setBuff(0);
                gameComputer.setBuff();

                leftBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                middleBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                rightBuffButton.setBackgroundColor(getResources().getColor(R.color.chooseBuffButtonBefore));

                leftText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                middleText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                rightText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));

                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));
            }
        });

        gameOverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutVisibility(1);
            }
        });

        serialDriver = new SerialDriver((MainActivity)getActivity());
    }

    @Override
    public void onDestroy() {
        serialDriver.closeSerial();
        super.onDestroy();
    }

    public void setLayoutVisibility(int index) {
        switch (index) {
            case 1:
                beginLayout.setVisibility(View.VISIBLE);
                gameLayout.setVisibility(View.GONE);
                endLayout.setVisibility(View.GONE);
                break;
            case 2:
                beginLayout.setVisibility(View.GONE);
                gameLayout.setVisibility(View.VISIBLE);
                endLayout.setVisibility(View.GONE);
                break;
            case 3:
                beginLayout.setVisibility(View.GONE);
                gameLayout.setVisibility(View.GONE);
                endLayout.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    public void setButtonVisibility() {
        leftButton.setVisibility(View.VISIBLE);
        middleButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
    }

    public void sendMessageToChatFragment(String text) {
        MainActivity mainActivity = (MainActivity)getActivity();
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg);
    }

    private void setButtonText(int leftButton, int middleButton, int rightButton) {
        this.leftButton.setText(Integer.toString(leftButton));
        this.middleButton.setText(Integer.toString(middleButton));
        this.rightButton.setText(Integer.toString(rightButton));
    }

    private void setTextText(int leftText, int middleText, int rightText) {
        this.leftText.setText(Integer.toString(leftText));
        this.middleText.setText(Integer.toString(middleText));
        this.rightText.setText(Integer.toString(rightText));
    }

    private void gameButtonHandler(int buttonIndex) {
        ++GameMaster.roundCounter;

        int userPlayCardResult = buttonIndex == 1 ? gameUser.playCard1()
                : buttonIndex == 2 ? gameUser.playCard2()
                : gameUser.playCard3();
        int judgeResult = GameMaster.roundCounter == 1 ? GameMaster.judge(userPlayCardResult, gameComputer.playCard1())
                : GameMaster.roundCounter == 2 ? GameMaster.judge(userPlayCardResult, gameComputer.playCard2())
                : GameMaster.judge(userPlayCardResult, gameComputer.playCard3());

        if (GameMaster.roundCounter == 1) {
            switch (gameComputer.getPlayCradIndex1()) {
                case 0:
                    leftText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                case 1:
                    middleText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                case 2:
                    rightText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                default:
                    break;
            }
        } else if (GameMaster.roundCounter == 2) {
            switch (gameComputer.getPlayCradIndex2()) {
                case 0:
                    leftText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                case 1:
                    middleText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                case 2:
                    rightText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                default:
                    break;
            }
        } else if (GameMaster.roundCounter == 3) {
            switch (gameComputer.getPlayCradIndex3()) {
                case 0:
                    leftText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                case 1:
                    middleText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                case 2:
                    rightText.setBackgroundColor(getResources().getColor(R.color.computerTextAfter));
                    break;
                default:
                    break;
            }
        }

        if (judgeResult == 1) {
            gameUser.setScore(gameUser.getScore() + 1);
            sendMessageToChatFragment(getResources().getString(R.string.robot_string_user_score_add_one_text));
        } else if (judgeResult == 2) {
            sendMessageToChatFragment(getResources().getString(R.string.robot_string_draw_text));
        } else if (judgeResult == 3) {
            gameComputer.setScore(gameComputer.getScore() + 1);
            sendMessageToChatFragment(getResources().getString(R.string.robot_string_computer_score_add_one_text));
        }

        GameMaster.handleARound((GameFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout),
                GameMaster.roundCounter,
                gameUser.getScore() - gameComputer.getScore());
    }
}
