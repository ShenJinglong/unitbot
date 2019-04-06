package xyz.sjinglong.unitbot.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class GameUser {
    private static final String TAG = "GameUser";

    private List<Integer> cards = new ArrayList<Integer>();
    private int score;
    private int buffCode;

    public boolean buffed = false;

    public GameUser() {
        this.score = 0;
        cards = GameMaster.getCards();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int playCard1() {
        return cards.get(0);
    }

    public int playCard2() {
        return cards.get(1);
    }

    public int playCard3() {
        return cards.get(2);
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setBuff(int i) {
        buffCode = i;
        if (!buffed) {
            for (int j = 0; j < cards.size(); ++j) {
                int jCards = cards.get(j);
                if (buffCode == 1) {
                    cards.set(j, 2 * jCards + 1);
                } else if (buffCode == 2) {
                    cards.set(j, jCards + 4);
                } else if (buffCode == 3) {
                    cards.set(j, jCards * jCards / 3);
                }
            }
            if (buffCode == 0) {
                buffed = false;
            } else {
                buffed = true;
            }
        }
    }
}
