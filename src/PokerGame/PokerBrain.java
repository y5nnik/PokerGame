package PokerGame;

import java.util.List;
import java.util.Random;

public class PokerBrain {

    //we need the pot, (possibly) the player decision, our hand, and the community cards
    public int compute(int playerBet, List<Card> computerHand, List<Card> communityCards) {
        Random random = new Random();
        int decision = random.nextInt(0,2);
        if (decision == 0) {
            return random.nextInt(5,200);
        }
        else {
            return 0;
        }

    }

    //RETURN -1 IF fold

    public int compute(int playerBet, List<String> computerHand, List<String> communityCards, boolean flag) { //flag is an indication if were currently in the preflop
        return 0;
    }
}
