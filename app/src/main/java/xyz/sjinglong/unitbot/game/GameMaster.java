package xyz.sjinglong.unitbot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xyz.sjinglong.unitbot.GameFragment;
import xyz.sjinglong.unitbot.R;
import xyz.sjinglong.unitbot.utils.TTS;

public class GameMaster {

    public static int roundCounter;
    public static int roundCounterForAnimation;

    public GameMaster() {

    }

    public static List<Integer> getCards() {
        List<Integer> cards = new ArrayList<Integer>();
        Random random = new Random();
        cards.add(random.nextInt(5) + 1);
        cards.add(random.nextInt(5) + 1);
        cards.add(random.nextInt(5) + 1);
        return cards;
    }

    public static void beginGame(GameFragment gameFragment) {

        roundCounter = 0;
        roundCounterForAnimation = 0;

        gameFragment.setButtonVisibility();
        gameFragment.setTextVisibility();
        gameFragment.setButtonColor(-1);
    }

    public static void handleARound(GameFragment gameFragment, int result) {
        if (roundCounterForAnimation == 3) {
            gameFragment.setLayoutVisibility(3);
            if (result > 0) {
                gameFragment.sendMessageToChatFragment(gameFragment.getResources().getString(R.string.robot_string_user_win), TTS.TYPE_ADD);
                gameFragment.showResult.setText(gameFragment.getResources().getString(R.string.game_fragment_show_result_you_win));
                gameFragment.serialDriver.sendMessage("u");
            } else if (result == 0) {
                gameFragment.sendMessageToChatFragment(gameFragment.getResources().getString(R.string.robot_string_draw_text), TTS.TYPE_ADD);
                gameFragment.showResult.setText(gameFragment.getResources().getString(R.string.game_fragment_show_result_draw));
            } else {
                gameFragment.sendMessageToChatFragment(gameFragment.getResources().getString(R.string.robot_string_robot_win), TTS.TYPE_ADD);
                gameFragment.showResult.setText(gameFragment.getResources().getString(R.string.game_fragment_show_result_you_lose));
            }
        }
    }

    public static int judge(int userResult, int computerResult) {
        return (userResult > computerResult) ? 1 : ((userResult == computerResult) ? 2 : 3);
    }
}
