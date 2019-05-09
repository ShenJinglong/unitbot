package xyz.sjinglong.unitbot;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.Timer;
import java.util.TimerTask;

import xyz.sjinglong.unitbot.game.GameComputer;
import xyz.sjinglong.unitbot.game.GameMaster;
import xyz.sjinglong.unitbot.game.GameUser;
import xyz.sjinglong.unitbot.hardware.GPIODriver;
import xyz.sjinglong.unitbot.hardware.SerialDriver;

public class GameFragment extends Fragment {
    private static final String TAG = "GameFragment";

    private QMUIRoundButton beginGameButton;

    private QMUIRoundButton leftButton;
    private QMUIRoundButton middleButton;
    private QMUIRoundButton rightButton;

    private QMUIRoundButton leftBuffButton;
    private QMUIRoundButton middleBuffButton;
    private QMUIRoundButton rightBuffButton;

    private QMUIRoundButton tryAgainButton;
    private QMUIRoundButton gameOverButton;

    private TextView leftText;
    private TextView middleText;
    private TextView rightText;

    private LinearLayout gameLayout;
    private FrameLayout beginLayout;
    private FrameLayout endLayout;

    private GameComputer gameComputer;
    private GameUser gameUser;

    private SerialDriver serialDriver;
    private GPIODriver gpioDriver;

    private int currentButtonSelection = -1;
    private int currentUserCardStatus = 7;
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
                    int currentRockerStatus = gpioDriver.getCurrentRockerStatus();

                    // sendMessageToChatFragment("****currentRockerStatus"+currentRockerStatus);

                    if (GameMaster.roundCounter == 0) {
                        if (currentRockerStatus == 100) {
                            if (currentButtonSelection <= 0) {
                                currentButtonSelection = 2;
                            } else {
                                --currentButtonSelection;
                            }
                            setButtonColor(currentButtonSelection);
                        } else if (currentRockerStatus == 10) {
                            if (currentButtonSelection == 0) {
                                leftButton.callOnClick();
                            } else if (currentButtonSelection == 1) {
                                middleButton.callOnClick();
                            } else if (currentButtonSelection == 2) {
                                rightButton.callOnClick();
                            }
                            currentButtonSelection = -1;
                            setButtonColor(currentButtonSelection);
                        } else if (currentRockerStatus == 1) {
                            if (currentButtonSelection == 2 || currentRockerStatus == -1) {
                                currentButtonSelection = 0;
                            } else {
                                ++currentButtonSelection;
                            }
                            setButtonColor(currentButtonSelection);
                        }
                    } else if (GameMaster.roundCounter == 1) {
                        if (currentRockerStatus == 100) {
                            if (currentButtonSelection <= 0) {
                                currentButtonSelection = 1;
                            } else {
                                --currentButtonSelection;
                            }
                            if (currentUserCardStatus == 3) {
                                if (currentButtonSelection == 0) {
                                    setButtonColor(1);
                                } else {
                                    setButtonColor(2);
                                }
                            } else if (currentUserCardStatus == 5) {
                                if (currentButtonSelection == 0) {
                                    setButtonColor(0);
                                } else {
                                    setButtonColor(2);
                                }
                            } else if (currentUserCardStatus == 6) {
                                if (currentButtonSelection == 0) {
                                    setButtonColor(0);
                                } else {
                                    setButtonColor(1);
                                }
                            }
                        } else if (currentRockerStatus == 10) {
                            if (currentButtonSelection == 0) {
                                if (currentUserCardStatus == 3) {
                                    middleButton.callOnClick();
                                } else if (currentUserCardStatus == 5) {
                                    leftButton.callOnClick();
                                } else if (currentUserCardStatus == 6) {
                                    leftButton.callOnClick();
                                }
                            } else if (currentButtonSelection == 1) {
                                if (currentUserCardStatus == 3) {
                                    rightButton.callOnClick();
                                } else if (currentUserCardStatus == 5) {
                                    rightButton.callOnClick();
                                } else if (currentUserCardStatus == 6) {
                                    middleButton.callOnClick();
                                }
                            }
                            currentButtonSelection = -1;
                            setButtonColor(currentButtonSelection);
                        } else if (currentRockerStatus == 1) {
                            if (currentButtonSelection == 1 || currentRockerStatus == -1) {
                                currentButtonSelection = 0;
                            } else {
                                ++currentButtonSelection;
                            }
                            if (currentUserCardStatus == 3) {
                                if (currentButtonSelection == 0) {
                                    setButtonColor(1);
                                } else {
                                    setButtonColor(2);
                                }
                            } else if (currentUserCardStatus == 5) {
                                if (currentButtonSelection == 0) {
                                    setButtonColor(0);
                                } else {
                                    setButtonColor(2);
                                }
                            } else if (currentUserCardStatus == 6) {
                                if (currentButtonSelection == 0) {
                                    setButtonColor(0);
                                } else {
                                    setButtonColor(1);
                                }
                            }
                        }
                    } else if (GameMaster.roundCounter == 2) {
                        if (currentRockerStatus == 100) {
                            if (currentButtonSelection <= 0) {
                                currentButtonSelection = 0;
                            } else {
                                --currentButtonSelection;
                            }
                            if (currentUserCardStatus == 4) {
                                setButtonColor(0);
                            } else if (currentUserCardStatus == 2) {
                                setButtonColor(1);
                            } else if (currentUserCardStatus == 1) {
                                setButtonColor(2);
                            }
                        } else if (currentRockerStatus == 10) {
                            if (currentUserCardStatus == 4) {
                                leftButton.callOnClick();
                            } else if (currentUserCardStatus == 2) {
                                middleButton.callOnClick();
                            } else if (currentUserCardStatus == 1) {
                                rightButton.callOnClick();
                            }
                            currentButtonSelection = -1;
                            setButtonColor(currentButtonSelection);
                        } else if (currentRockerStatus == 1) {
                            if (currentButtonSelection == 0 || currentRockerStatus == -1) {
                                currentButtonSelection = 0;
                            } else {
                                ++currentButtonSelection;
                            }
                            if (currentUserCardStatus == 4) {
                                setButtonColor(0);
                            } else if (currentUserCardStatus == 2) {
                                setButtonColor(1);
                            } else if (currentUserCardStatus == 1) {
                                setButtonColor(2);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };


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

                QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftBuffButton.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleBuffButton.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightBuffButton.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));

                leftText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                middleText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                rightText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));

                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));

                serialDriver.sendMessage("start game");

                currentUserCardStatus = 7;
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButtonHandler(1);
                leftButton.setVisibility(View.GONE);
                serialDriver.sendMessage("left button clicked");
                currentUserCardStatus &= 3;
            }
        });

        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButtonHandler(2);
                middleButton.setVisibility(View.GONE);
                serialDriver.sendMessage("middle button clicked");
                currentUserCardStatus &= 5;
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButtonHandler(3);
                rightButton.setVisibility(View.GONE);
                serialDriver.sendMessage("right button clicked");
                currentUserCardStatus &= 6;
            }
        });

        leftBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonAfter));
                }
                gameUser.setBuff(1);
                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
            }
        });

        middleBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonAfter));
                }
                gameUser.setBuff(2);
                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
            }
        });

        rightBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonAfter));
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

                QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftBuffButton.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleBuffButton.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightBuffButton.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));

                leftText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                middleText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));
                rightText.setBackgroundColor(getResources().getColor(R.color.computerTextBefore));

                setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));

                serialDriver.sendMessage("start game");

                currentUserCardStatus = 7;
            }
        });

        gameOverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutVisibility(1);
            }
        });

        serialDriver = new SerialDriver((MainActivity)getActivity());
        gpioDriver = new GPIODriver((MainActivity)getActivity());

 //       timer.schedule(task, 400, 500);
    }

    @Override
    public void onDestroy() {
        serialDriver.closeSerial();
        gpioDriver.closeGPIO();
        timer.cancel();
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

    private void setButtonColor(int index) {
        if (index == 0) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonAfter));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
        } else if (index == 1) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonAfter));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
        } else if (index == 2) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonAfter));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
        } else if (index == -1) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
        }
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
