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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.Timer;
import java.util.TimerTask;

import xyz.sjinglong.unitbot.game.GameComputer;
import xyz.sjinglong.unitbot.game.GameMaster;
import xyz.sjinglong.unitbot.game.GameUser;
import xyz.sjinglong.unitbot.hardware.GPIODriver;
import xyz.sjinglong.unitbot.hardware.SerialDriver;
import xyz.sjinglong.unitbot.utils.SerialMessageHandler;

public class GameFragment extends Fragment {
    private static final String TAG = "GameFragment";

    // private QMUILinearLayout mTestLayout;


    private QMUIRoundButton beginGameButton;

    private QMUIRoundButton leftButton;
    private QMUIRoundButton middleButton;
    private QMUIRoundButton rightButton;

    private QMUIRoundButton leftBuffButton;
    private QMUIRoundButton middleBuffButton;
    private QMUIRoundButton rightBuffButton;

    private QMUIRoundButton tryAgainButton;
    private QMUIRoundButton gameOverButton;

    private QMUIRoundButton leftText;
    private QMUIRoundButton middleText;
    private QMUIRoundButton rightText;

    private LinearLayout gameLayout;
    private FrameLayout beginLayout;
    private FrameLayout endLayout;

    private GameComputer gameComputer;
    private GameUser gameUser;

    private SerialDriver serialDriver;
    private GPIODriver gpioDriver;
    private SerialMessageHandler serialMessageHandler;
    private int receiveControlFlag = 0;
    private int enableRocker = 0;

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
                    rockerHandler();
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

        // mTestLayout = view.findViewById(R.id.layout_for_test);

        // mTestLayout.setRadiusAndShadow(QMUIDisplayHelper.dp2px(getContext(), 15), QMUIDisplayHelper.dp2px(getContext(), 100), 1.0f);
        setLayoutVisibility(1);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        beginGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serialMessageHandler.parseMessage(serialDriver.getSerialMessage());

                int redValue = serialMessageHandler.getRedValue();
                int greenValue = serialMessageHandler.getGreenValue();
                int blueValue = serialMessageHandler.getBlueValue();
                int distanceValue = serialMessageHandler.getDistanceValue();

                if (!(redValue >= 6000 && greenValue <= 2000 && blueValue <= 2000)
                    && !(redValue <= 2000 && greenValue >= 6000 && blueValue <= 2000)
                    && !(redValue <= 2000 && greenValue <= 2000 && blueValue >= 6000)) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("卡片")
                            .setMessage("请插入卡片")
                            .addAction("返回", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                } else if (distanceValue <= 100) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("距离")
                            .setMessage("请与机器人保持 10cm 以上的距离")
                            .addAction("返回", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                } else {


                    setLayoutVisibility(2);
                    enableRocker = 1;

                    gameComputer = new GameComputer();
                    gameUser = new GameUser();

                    GameMaster.beginGame((GameFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout));

                    gameUser.setBuff(0);
                    gameComputer.setBuff();

                    // 设置Buff颜色
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));

                    // 设置电脑卡牌初始颜色
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextBefore));


                    setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                    setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));

                    serialDriver.sendMessage("start game");

                    currentUserCardStatus = 7;

                    if (redValue >= 6000 && greenValue <= 2000 && blueValue <= 2000) {
                        leftBuffButton.callOnClick();
                    } else if (redValue <= 2000 && greenValue >= 6000 && blueValue <= 2000) {
                        middleBuffButton.callOnClick();
                    } else if (redValue <= 2000 && greenValue <= 2000 && blueValue >= 6000) {
                        rightBuffButton.callOnClick();
                    }
                }
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation translateAnimation = new TranslateAnimation(0, 0, 0, -35);
                translateAnimation.setDuration(1000);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        gameButtonHandler(1);
                        leftButton.setVisibility(View.GONE);
                        serialDriver.sendMessage("left button clicked");
                        currentUserCardStatus &= 3;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                leftButton.startAnimation(translateAnimation);
                setButtonColor(0);
            }
        });

        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation translateAnimation = new TranslateAnimation(0, 0, 0, -35);
                translateAnimation.setDuration(1000);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        gameButtonHandler(2);
                        middleButton.setVisibility(View.GONE);
                        serialDriver.sendMessage("middle button clicked");
                        currentUserCardStatus &= 5;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                middleButton.startAnimation(translateAnimation);
                setButtonColor(1);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation translateAnimation = new TranslateAnimation(0,0, 0, -35);
                translateAnimation.setDuration(1000);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        gameButtonHandler(3);
                        rightButton.setVisibility(View.GONE);
                        serialDriver.sendMessage("right button clicked");
                        currentUserCardStatus &= 6;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rightButton.startAnimation(translateAnimation);
                setButtonColor(2);
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
                serialMessageHandler.parseMessage(serialDriver.getSerialMessage());

                int redValue = serialMessageHandler.getRedValue();
                int greenValue = serialMessageHandler.getGreenValue();
                int blueValue = serialMessageHandler.getBlueValue();
                int distanceValue = serialMessageHandler.getDistanceValue();

                if (!(redValue >= 6000 && greenValue <= 2000 && blueValue <= 2000)
                        && !(redValue <= 2000 && greenValue >= 6000 && blueValue <= 2000)
                        && !(redValue <= 2000 && greenValue <= 2000 && blueValue >= 6000)) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("卡片")
                            .setMessage("请插入卡片")
                            .addAction("返回", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                } else if (distanceValue <= 100) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("距离")
                            .setMessage("请与机器人保持 10cm 以上的距离")
                            .addAction("返回", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                } else {
                    setLayoutVisibility(2);
                    enableRocker = 1;
                    gameUser = new GameUser();
                    gameComputer = new GameComputer();
                    GameMaster.beginGame((GameFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout));

                    gameUser.setBuff(0);
                    gameComputer.setBuff();

                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.chooseBuffButtonBefore));

                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextBefore));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextBefore));

                    setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                    setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));

                    serialDriver.sendMessage("start game");

                    currentUserCardStatus = 7;

                    if (redValue >= 6000 && greenValue <= 2000 && blueValue <= 2000) {
                        leftBuffButton.callOnClick();
                    } else if (redValue <= 2000 && greenValue >= 6000 && blueValue <= 2000) {
                        middleBuffButton.callOnClick();
                    } else if (redValue <= 2000 && greenValue <= 2000 && blueValue >= 6000) {
                        rightBuffButton.callOnClick();
                    }
                }
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
        serialMessageHandler = new SerialMessageHandler((MainActivity)getActivity());

        timer.schedule(task, 400, 100);
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

    public void setTextVisibility() {
        leftText.setVisibility(View.VISIBLE);
        middleText.setVisibility(View.VISIBLE);
        rightText.setVisibility(View.VISIBLE);
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

    public void setButtonColor(int index) {
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

    private void setTextColor(int computerPlayCardIndex) {
        QMUIRoundButtonDrawable qmuiRoundButtonDrawable;
        switch (computerPlayCardIndex) {
            case 0:
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftText.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextAfter));
                break;
            case 1:
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleText.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextAfter));
                break;
            case 2:
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightText.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.computerTextAfter));
                break;
            default:
                break;
        }
    }

    private void addTextAnimation(int computerPlayCardIndex, int judgeResult) {
        Animation translateAnimation;
        switch (computerPlayCardIndex) {
            case 0:
                translateAnimation = new TranslateAnimation(0,0,0,35);
                translateAnimation.setDuration(1000);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        ++GameMaster.roundCounterForAnimation;

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
                                gameUser.getScore() - gameComputer.getScore());
                        leftText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                leftText.startAnimation(translateAnimation);
                break;
            case 1:
                translateAnimation = new TranslateAnimation(0,0,0,35);
                translateAnimation.setDuration(1000);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        ++GameMaster.roundCounterForAnimation;

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
                                gameUser.getScore() - gameComputer.getScore());
                        middleText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                middleText.startAnimation(translateAnimation);
                break;
            case 2:
                translateAnimation = new TranslateAnimation(0,0,0,35);
                translateAnimation.setDuration(1000);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        ++GameMaster.roundCounterForAnimation;

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
                                gameUser.getScore() - gameComputer.getScore());
                        rightText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rightText.startAnimation(translateAnimation);
                break;
            default:
                break;
        }


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
            setTextColor(gameComputer.getPlayCradIndex1());
            addTextAnimation(gameComputer.getPlayCradIndex1(), judgeResult);
        } else if (GameMaster.roundCounter == 2) {
            setTextColor(gameComputer.getPlayCradIndex2());
            addTextAnimation(gameComputer.getPlayCradIndex2(), judgeResult);
        } else if (GameMaster.roundCounter == 3) {
            setTextColor(gameComputer.getPlayCradIndex3());
            addTextAnimation(gameComputer.getPlayCradIndex3(), judgeResult);
            enableRocker = 0;
        }
    }

    private void rockerHandler() {
        if (enableRocker == 1) {
            int currentRockerStatus = gpioDriver.getCurrentRockerStatus();

            if (receiveControlFlag == 0) {
                if (currentRockerStatus != 0) {
                    receiveControlFlag = 2;
                }
            } else {
                currentRockerStatus = 0;
                --receiveControlFlag;
            }

            // sendMessageToChatFragment("- currentRockerStatus: "+currentRockerStatus);

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
  //                  setButtonColor(currentButtonSelection);
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
   //                 setButtonColor(currentButtonSelection);
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
  //                  setButtonColor(currentButtonSelection);
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
        }
    }
}
