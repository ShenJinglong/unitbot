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
import xyz.sjinglong.unitbot.utils.TTS;

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

    public TextView showResult;

    private QMUIRoundButton leftText;
    private QMUIRoundButton middleText;
    private QMUIRoundButton rightText;

    private LinearLayout gameLayout;
    private FrameLayout beginLayout;
    private FrameLayout endLayout;

    private GameComputer gameComputer;
    private GameUser gameUser;

    public SerialDriver serialDriver;
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

        showResult = view.findViewById(R.id.game_fragment_show_result);

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

                if (judgeBuff(redValue, greenValue, blueValue) == 0) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle(getResources().getString(R.string.check_card_dialog_title))
                            .setMessage(getResources().getString(R.string.check_card_dialog_message))
                            .addAction(getResources().getString(R.string.back), new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                    TTS.speakFLUSH(getResources().getString(R.string.check_card_dialog_message));
                } else if (distanceValue <= 100) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle(getResources().getString(R.string.check_distance_title))
                            .setMessage(getResources().getString(R.string.check_distance_message))
                            .addAction(getResources().getString(R.string.back), new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                    TTS.speakFLUSH(getResources().getString(R.string.check_distance_message));
                } else {


                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("游戏规则")
                            .setMessage("点击游戏规则按钮展示游戏规则\n点击直接开始按钮开始游戏")
                            .addAction("游戏规则", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();

                                    sendMessageToChatFragment("hi~ 你好啊！我是来自 UNITETOP 公司的小维", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("现在让我来告诉你这个游戏怎么玩吧", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("首先，你需要挑选一张卡片，插入插卡口中", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("那这些卡片都有什么用呐？？嘿嘿，我先不告诉你，你只需要知道：", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("红色卡： 2 * n + 1", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("绿色卡： n + 4", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("蓝色卡： n ^ 2 / 3", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("hhh...不要被这些公式吓到哦！", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("进入游戏后，你和小维都会随机分到三张牌，这三张牌是从1～5中随机挑选的", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("然后就是...刚才你插入的那张卡，还记得它代表什么函数吗？", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("你出的每一张牌都会经过这个函数运算， 比如说你出一张 1, 然后你插入的是红卡， 那么你出的卡的实际数值就是...多少吖？？...你猜", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("hhh...就是 3 吖， 答对了吗？", TTS.TYPE_ADD);
                                    sendMessageToChatFragment("然后我俩轮流出牌，比谁的牌大，三局两胜，谁赢了谁就有糖吃哦 ：）", TTS.TYPE_ADD);

                                }
                            })
                            .addAction("直接开始", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {

                                    dialog.dismiss();

                                    TTS.stopSpeak();

                                    addBeginGameAnimation();
                                    setLayoutVisibility(2);
                                    enableRocker = 1;

                                    gameComputer = new GameComputer();
                                    gameUser = new GameUser();

                                    GameMaster.beginGame((GameFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout));


                                    // 设置Buff颜色
                                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftBuffButton.getBackground();
                                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_buff_background));
                                    leftBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_buff_text));
                                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleBuffButton.getBackground();
                                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_buff_background));
                                    middleBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_buff_text));
                                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightBuffButton.getBackground();
                                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_buff_background));
                                    rightBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_buff_text));

                                    // 设置电脑卡牌初始颜色
                                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftText.getBackground();
                                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background));
                                    leftText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text));
                                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleText.getBackground();
                                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background));
                                    middleText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text));
                                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightText.getBackground();
                                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background));
                                    rightText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text));

                                    gameUser.setBuff(0);
                                    gameComputer.setBuff();
                                    setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                                    setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));

                                    currentUserCardStatus = 7;

                                    switch (judgeBuff(redValue, greenValue, blueValue)) {
                                        case 1:
                                            leftBuffButton.callOnClick();
                                            break;
                                        case 2:
                                            middleBuffButton.callOnClick();
                                            break;
                                        case 3:
                                            rightBuffButton.callOnClick();
                                            break;
                                        default:
                                            beginGameButton.callOnClick();
                                            break;
                                    }

                                }
                            }).show();


                }
            }
        });









        /*


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
                        // serialDriver.sendMessage("left button clicked");
                        currentUserCardStatus &= 3;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                leftButton.setText(Integer.toString(gameUser.getCards().get(0)));
                leftButton.startAnimation(translateAnimation);

                QMUIRoundButtonDrawable roundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
                roundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
                leftButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));
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
                        // serialDriver.sendMessage("middle button clicked");
                        currentUserCardStatus &= 5;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                middleButton.startAnimation(translateAnimation);
                middleButton.setText(Integer.toString(gameUser.getCards().get(1)));


                QMUIRoundButtonDrawable roundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
                roundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
                middleButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));
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
                        // serialDriver.sendMessage("right button clicked");
                        currentUserCardStatus &= 6;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rightButton.startAnimation(translateAnimation);
                rightButton.setText(Integer.toString(gameUser.getCards().get(2)));

                QMUIRoundButtonDrawable roundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
                roundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
                rightButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));
            }
        });


        */








        leftBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    gameUser.setBuff(1);
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_left_buff_background_selected));
                    leftBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_left_buff_text_selected));
                }
            }
        });

        middleBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    gameUser.setBuff(2);
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_middle_buff_background_selected));
                    middleBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_middle_buff_text_selected));
                }
            }
        });

        rightBuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameUser.buffed) {
                    gameUser.setBuff(3);
                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_right_buff_background_selected));
                    rightBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_right_buff_text_selected));
                }
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

                if (judgeBuff(redValue, greenValue, blueValue) == 0) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle(getResources().getString(R.string.check_card_dialog_title))
                            .setMessage(getResources().getString(R.string.check_card_dialog_message))
                            .addAction(getResources().getString(R.string.back), new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                    TTS.speakFLUSH(getResources().getString(R.string.check_card_dialog_message));
                } else if (distanceValue <= 100) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle(getResources().getString(R.string.check_distance_title))
                            .setMessage(getResources().getString(R.string.check_distance_message))
                            .addAction(getResources().getString(R.string.back), new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog qmuiDialog, int i) {
                                    qmuiDialog.dismiss();
                                }
                            }).show();
                    TTS.speakFLUSH(getResources().getString(R.string.check_distance_message));
                } else {
                    addBeginGameAnimation();
                    setLayoutVisibility(2);
                    enableRocker = 1;
                    gameUser = new GameUser();
                    gameComputer = new GameComputer();
                    GameMaster.beginGame((GameFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.functional_fragment_layout));

                    gameUser.setBuff(0);
                    gameComputer.setBuff();

                    QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_buff_background));
                    leftBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_buff_text));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_buff_background));
                    middleBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_buff_text));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightBuffButton.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_buff_background));
                    rightBuffButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_buff_text));

                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) leftText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background));
                    leftText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) middleText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background));
                    middleText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text));
                    qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable) rightText.getBackground();
                    qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background));
                    rightText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text));

                    setButtonText(gameUser.getCards().get(0), gameUser.getCards().get(1), gameUser.getCards().get(2));
                    setTextText(gameComputer.getCards().get(0), gameComputer.getCards().get(1), gameComputer.getCards().get(2));

                    currentUserCardStatus = 7;

                    switch (judgeBuff(redValue, greenValue, blueValue)) {
                        case 1:
                            leftBuffButton.callOnClick();
                            break;
                        case 2:
                            middleBuffButton.callOnClick();
                            break;
                        case 3:
                            rightBuffButton.callOnClick();
                            break;
                        default:
                            tryAgainButton.callOnClick();
                            break;
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

    private void leftButtonOnclick() {
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
                // serialDriver.sendMessage("left button clicked");
                currentUserCardStatus &= 3;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        leftButton.setText(Integer.toString(gameUser.getCards().get(0)));
        leftButton.startAnimation(translateAnimation);

        QMUIRoundButtonDrawable roundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
        roundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
        leftButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));
    }

    private void middleButtonOnclick() {
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
                // serialDriver.sendMessage("middle button clicked");
                currentUserCardStatus &= 5;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        middleButton.startAnimation(translateAnimation);
        middleButton.setText(Integer.toString(gameUser.getCards().get(1)));


        QMUIRoundButtonDrawable roundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
        roundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
        middleButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));
    }

    private void rightButtonOnclick() {
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
                // serialDriver.sendMessage("right button clicked");
                currentUserCardStatus &= 6;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rightButton.startAnimation(translateAnimation);
        rightButton.setText(Integer.toString(gameUser.getCards().get(2)));

        QMUIRoundButtonDrawable roundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
        roundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
        rightButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));
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

    public void sendMessageToChatFragment(String text, int messageType) {
        MainActivity mainActivity = (MainActivity)getActivity();
        ChatFragment chatFragment = (ChatFragment)mainActivity.getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Msg msg = new Msg(text, Msg.TYPE_RECEIVE);
        chatFragment.addMessage(msg, messageType);
    }

    private void setButtonText(int leftButton, int middleButton, int rightButton) {
        this.leftButton.setText(Integer.toString(leftButton));
        this.middleButton.setText(Integer.toString(middleButton));
        this.rightButton.setText(Integer.toString(rightButton));
    }

    public void setButtonColor(int index) {
        if (index == 0) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
            leftButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            middleButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            rightButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));
        } else if (index == 1) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
            middleButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            leftButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            rightButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));
        } else if (index == 2) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background_selected));
            rightButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text_selected));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            leftButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            middleButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));

        } else if (index == -1) {
            QMUIRoundButtonDrawable qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            rightButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));

            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)leftButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            leftButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));


            qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleButton.getBackground();
            qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_user_card_background));
            middleButton.setTextColor(getResources().getColor(R.color.game_fragment_frame2_user_card_text));
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
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background_selected));
                leftText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text_selected));
                break;
            case 1:
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)middleText.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background_selected));
                middleText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text_selected));
                break;
            case 2:
                qmuiRoundButtonDrawable = (QMUIRoundButtonDrawable)rightText.getBackground();
                qmuiRoundButtonDrawable.setColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_background_selected));
                rightText.setTextColor(getResources().getColor(R.color.game_fragment_frame2_robot_card_text_selected));
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
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_user_score_add_one_text), TTS.TYPE_ADD);
                        } else if (judgeResult == 2) {
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_draw_text), TTS.TYPE_ADD);
                        } else if (judgeResult == 3) {
                            gameComputer.setScore(gameComputer.getScore() + 1);
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_computer_score_add_one_text), TTS.TYPE_ADD);
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
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_user_score_add_one_text), TTS.TYPE_ADD);
                        } else if (judgeResult == 2) {
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_draw_text), TTS.TYPE_ADD);
                        } else if (judgeResult == 3) {
                            gameComputer.setScore(gameComputer.getScore() + 1);
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_computer_score_add_one_text), TTS.TYPE_ADD);
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
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_user_score_add_one_text), TTS.TYPE_ADD);
                        } else if (judgeResult == 2) {
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_draw_text), TTS.TYPE_ADD);
                        } else if (judgeResult == 3) {
                            gameComputer.setScore(gameComputer.getScore() + 1);
                            sendMessageToChatFragment(getResources().getString(R.string.robot_string_computer_score_add_one_text), TTS.TYPE_ADD);
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
            communicateWithSlave("" + buttonIndex + (gameComputer.getPlayCradIndex1() + 1));
        } else if (GameMaster.roundCounter == 2) {
            setTextColor(gameComputer.getPlayCradIndex2());
            addTextAnimation(gameComputer.getPlayCradIndex2(), judgeResult);
            communicateWithSlave("" + buttonIndex + (gameComputer.getPlayCradIndex2() + 1));
        } else if (GameMaster.roundCounter == 3) {
            setTextColor(gameComputer.getPlayCradIndex3());
            addTextAnimation(gameComputer.getPlayCradIndex3(), judgeResult);
            communicateWithSlave("" + buttonIndex + (gameComputer.getPlayCradIndex3() + 1));
            enableRocker = 0;
        }
    }

    private void communicateWithSlave(String message) {
        switch (message) {
            case "11":
                serialDriver.sendMessage("a");
                break;
            case "12":
                serialDriver.sendMessage("b");
                break;
            case "13":
                serialDriver.sendMessage("c");
                break;
            case "21":
                serialDriver.sendMessage("d");
                break;
            case "22":
                serialDriver.sendMessage("e");
                break;
            case "23":
                serialDriver.sendMessage("f");
                break;
            case "31":
                serialDriver.sendMessage("g");
                break;
            case "32":
                serialDriver.sendMessage("h");
                break;
            case "33":
                serialDriver.sendMessage("i");
                break;
            default:
                break;
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
                        // leftButton.callOnClick();
                        leftButtonOnclick();
                    } else if (currentButtonSelection == 1) {
                        // middleButton.callOnClick();
                        middleButtonOnclick();
                    } else if (currentButtonSelection == 2) {
                        // rightButton.callOnClick();
                        rightButtonOnclick();
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
                            // middleButton.callOnClick();
                            middleButtonOnclick();
                        } else if (currentUserCardStatus == 5) {
                            // leftButton.callOnClick();
                            leftButtonOnclick();
                        } else if (currentUserCardStatus == 6) {
                            // leftButton.callOnClick();
                            leftButtonOnclick();
                        }
                    } else if (currentButtonSelection == 1) {
                        if (currentUserCardStatus == 3) {
                            // rightButton.callOnClick();
                            rightButtonOnclick();
                        } else if (currentUserCardStatus == 5) {
                            // rightButton.callOnClick();
                            rightButtonOnclick();
                        } else if (currentUserCardStatus == 6) {
                            // middleButton.callOnClick();
                            middleButtonOnclick();
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
                        // leftButton.callOnClick();
                        leftButtonOnclick();
                    } else if (currentUserCardStatus == 2) {
                        // middleButton.callOnClick();
                        middleButtonOnclick();
                    } else if (currentUserCardStatus == 1) {
                        // rightButton.callOnClick();
                        rightButtonOnclick();
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

    private int judgeBuff(int redValue, int greenValue, int blueValue) {
        if (redValue > greenValue && redValue > blueValue) {
            if (redValue - greenValue > 1500 || redValue - blueValue > 1500) {
                return 1;
            } else {
                return 0;
            }
        } else if (greenValue > redValue && greenValue > blueValue) {
            if (greenValue - redValue > 1500 || greenValue - blueValue > 1500) {
                return 2;
            } else {
                return 0;
            }
        } else {
            if (blueValue - redValue > 1500 || blueValue - greenValue > 1500) {
                return 3;
            } else {
                return 0;
            }
        }
    }

    private void addBeginGameAnimation() {
        Animation translateAnimation1 = new TranslateAnimation(0, 0, 300, 0);
        translateAnimation1.setDuration(1000);
        leftButton.startAnimation(translateAnimation1);


        Animation translateAnimation2 = new TranslateAnimation(0, 0, 300, 0);
        translateAnimation2.setDuration(1000);
        translateAnimation2.setStartOffset(200);
        middleButton.startAnimation(translateAnimation2);



        Animation translateAnimation3 = new TranslateAnimation(0, 0, 300, 0);
        translateAnimation3.setDuration(1000);
        translateAnimation3.setStartOffset(400);
        rightButton.startAnimation(translateAnimation3);

        Animation translateAnimation4 = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation4.setDuration(1000);
        leftText.startAnimation(translateAnimation4);


        Animation translateAnimation5 = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation5.setDuration(1000);
        translateAnimation5.setStartOffset(200);
        middleText.startAnimation(translateAnimation5);



        Animation translateAnimation6 = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation6.setDuration(1000);
        translateAnimation6.setStartOffset(400);
        rightText.startAnimation(translateAnimation6);
    }
}
