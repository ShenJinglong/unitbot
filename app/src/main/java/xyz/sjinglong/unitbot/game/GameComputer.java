package xyz.sjinglong.unitbot.game;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameComputer {
    private List<Integer> cards = new ArrayList<Integer>();
    private int score;
    private int buffCode;

    private int playCradIndex1 = -1;
    private int playCradIndex2 = -1;
    private int playCradIndex3 = -1;

    private static final String TAG = "GameComputer";

    public GameComputer() {
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
        Random random = new Random();
        int index = random.nextInt(3);
        playCradIndex1 = index;
        return cards.get(index);
    }

    public int  playCard2() {
        Random random = new Random();
        int index = random.nextInt(3);
        while (index == playCradIndex1) {
            index = random.nextInt(3);
        }
        playCradIndex2 = index;
        return cards.get(index);
    }

    public int playCard3() {
        Random random = new Random();
        int index = random.nextInt(3);
        while (index == playCradIndex1 || index == playCradIndex2) {
            index = random.nextInt(3);
        }
        playCradIndex3 = index;
        return cards.get(index);
    }

    public List<Integer> getCards() {
        return cards;
    }

    public int getPlayCradIndex1() {
        return playCradIndex1;
    }

    public int getPlayCradIndex2() {
        return playCradIndex2;
    }

    public int getPlayCradIndex3() {
        return playCradIndex3;
    }

    public void setBuff() {
        buffCode = new Random().nextInt(3) + 1;
        Log.d(TAG, "setBuff: " + buffCode);
        for (int i = 0; i < cards.size(); ++i) {
            if (buffCode == 1) {
                cards.set(i, 2 * cards.get(i) + 1);
            } else if (buffCode == 2) {
                cards.set(i, cards.get(i) + 4);
            } else if (buffCode == 3) {
                cards.set(i, cards.get(i) * cards.get(i) / 3);
            }
        }
    }
}
