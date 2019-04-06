package xyz.sjinglong.unitbot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xyz.sjinglong.unitbot.GameFragment;
import xyz.sjinglong.unitbot.R;

public class GameMaster {

    public static int roundCounter;

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

        gameFragment.setButtonVisibility();
    }

    public static void handleARound(GameFragment gameFragment, int roundCounter, int result) {
        if (roundCounter == 3) {
            gameFragment.setLayoutVisibility(3);
            if (result > 0)
                gameFragment.sendMessageToChatFragment("用户赢");
            else if (result == 0)
                gameFragment.sendMessageToChatFragment("平局");
            else
                gameFragment.sendMessageToChatFragment("电脑赢");
        }
    }

    public static int judge(int userResult, int computerResult) {
        return (userResult > computerResult) ? 1 : ((userResult == computerResult) ? 2 : 3);
    }
}
