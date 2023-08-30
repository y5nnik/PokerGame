package PokerGame;

import java.sql.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Main {

    /*TODO
        edgecases for round > 1
     */

    private static final int STARTING_MONEY = 1000;
    private static boolean bigBlind;
    private static final int BIG_BLIND = STARTING_MONEY / 100;
    private static final int SMALL_BLIND = BIG_BLIND / 2;

    private static int playerMoney = STARTING_MONEY;
    private static int computerMoney = STARTING_MONEY;
    private static int playerBet;
    private static int computerBet;
    private static int pot = 0;
    private static boolean PCWin;
    private static String winningHand;
    private static int stage;
    /*
    0 -> pre flop
    1 -> flop
    2 -> turn
    3 -> river
     */
    private static String [] suitChar = new String[]{"H", "D", "C", "S"};

    private static int maxValuePlayer;
    private static int maxSuitPlayer;
    private static int maxValueComputer;
    private static int maxSuitComputer;
    private static int sameHandKickerComputer;
    private static int sameHandKickerPlayer;
    private static List <Integer> numberOfValuesPlayer;
    private static List <Integer> numberOfValuesComputer;

    private static List<Card> standardDeck = new ArrayList<>();
    private static List<Card> currentDeck = new ArrayList<>();
    private static List<Card> playerHand = new ArrayList<>();
    private static List<Card> computerHand = new ArrayList<>();
    private static List<Card> communityCards = new ArrayList<>();
    private static PokerBrain pokerBrain = new PokerBrain();

    public static void startGame (){
        System.out.println("Poker Game has started...");

        displayCards(); //cards are displayed in the terminal
        initializeDeckForGame(); //Deck is initialized once
        bigBlind = true; //computer starts with big blind, else change this variable to false

        while (playerMoney > 0 && computerMoney > 0) {
            playRound();
        }

        if (playerMoney <= 0) {
            System.out.println("Computer wins! Better luck next time.");
        } else {
            System.out.println("Humans win! (for now)");
        }
    }

    private static void displayCards() {
        System.out.println("Cards are abbreviated as follows:");
        String format = "|   %-5s";
        System.out.format("+--------+--------+--------+--------+%n");
        System.out.format("| Hearts |Diamonds| Clubs  | Spades |%n");
        System.out.format("+--------+--------+--------+--------+%n");
        String[] suits = {"H", "D", "C", "S"};
        for (int i = 2; i <= 14; i++) {
            for (String suit : suits) {
                if (i <= 10) {
                    System.out.format(format, i+suit );
                } else {
                    char cardChar = switch (i) {
                        case 11 -> 'J';
                        case 12 -> 'Q';
                        case 13 -> 'K';
                        default -> 'A';
                    };
                    System.out.format(format, cardChar+suit);
                }
            }
            System.out.println("|");
        }
        System.out.format("+--------+--------+--------+--------+%n");
    }


    private static void playRound() {
        //0. SetUp
        Scanner scanner = new Scanner(System.in);
        initializeDeck();
        playerHand.clear();
        computerHand.clear();
        communityCards.clear();

        System.out.println("\n ");

        // 1. Deal cards to player and computer
        dealCards(playerHand, 2);
        dealCards(computerHand, 2);
        stage = 0;

        System.out.println("Your cards: " + playerHand.stream().map(Card::getName).toList());

        //2. big and small blind
        if (bigBlind){
            System.out.println("Computer is Big Blind");
            computerBet = BIG_BLIND;
            playerBet = SMALL_BLIND;
            computerMoney -= computerBet;
            playerMoney -= playerBet;
        }
        else {
            System.out.println("You are Big Blind");
            computerBet = SMALL_BLIND;
            playerBet = BIG_BLIND;
            computerMoney -= computerBet;
            playerMoney -= playerBet;
        }

        // 3. Preflop
        if (!bettingRound()) {
            endGame(true);
            return;
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }

        // 4. Reveal community cards (flop, turn, river)
        // 4.1 Flop
        stage++;
        dealCards(communityCards, 3); // Flop
        System.out.println("Flop: " + communityCards.stream().map(Card::getName).toList());
        if (!bettingRound()){
            endGame(true);
            return;
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }

        // 4.2 turn
        stage++;
        dealCards(communityCards, 1); // Turn
        System.out.println("Turn: " + communityCards.stream().map(Card::getName).toList());
        if (!bettingRound()){
            endGame(true);
            return;
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }

        // 4.3 river
        stage++;
        dealCards(communityCards, 1); // River
        System.out.println("River: " + communityCards.stream().map(Card::getName).toList());

        if (!bettingRound()){
            endGame(true);
            return;
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }

        // 5. Determine winner and award pot
        endGame(false);

    }


    private static void endGame (boolean fold) {

        //winner through fold
        if (fold) {
            if (PCWin){
                System.out.println("you folded");
                computerWins();
            }
            else {
                System.out.println("PC folded");
                playerWins();
            }
        }
        else if (determineWinner(playerHand, communityCards, computerHand)) {   //otherwise determine
            if (PCWin){
                computerWins();
            }
            else {
                playerWins();
            }
            System.out.println("Computer had the following cards: ["+ computerHand.get(0).getName() +"] ["+ computerHand.get(1).getName()  +"]");
            System.out.println("You had the following cards: ["+ playerHand.get(0).getName()  +"] ["+ playerHand.get(1).getName()  +"]");
            System.out.print("The community  cards were: ");
            for (Card communityCard : communityCards) {
                System.out.print("[" + communityCard.getName()  + "] ");
            }
            System.out.println("\nThe winning hand was a " + winningHand);
        }
        else {
            System.out.println("the pot of $" + pot + " is split!");
            bothWin();
            System.out.println("Computer had the following cards: ["+ computerHand.get(0).getName()  +"] ["+ computerHand.get(1).getName()  +"]");
            System.out.println("You had the following cards: ["+ playerHand.get(0).getName()  +"] ["+ playerHand.get(1).getName()  +"]");
            System.out.print("The community  cards were: ");
            for (Card communityCard : communityCards) {
                System.out.print("[" + communityCard.getName()  + "] ");
            }
            System.out.println("\nThe winning hand was a " + winningHand);
        }



        //reset variables and switch the big blind
        pot = 0;
        computerBet = 0;
        playerBet = 0;
        bigBlind = !bigBlind; //toggle
    }

    private static void initializeDeckForGame() {
        String[] suits = {"H", "D", "C", "S"};
        for (String suit : suits) {
            for (int i = 2; i <= 14; i++) {
                standardDeck.add(new Card(i,suit));
            }
        }
    }

    private static void initializeDeck() {
        //Delete deck of prev round
        currentDeck.clear();

        //the working deck (here currentDeck is "initialized" each round, as it is manipulated during the round
        currentDeck.addAll(standardDeck);
        Collections.shuffle(currentDeck);
    }

    private static void dealCards(List<Card> hand, int numCards) {
        for (int i = 0; i < numCards; i++) {
            hand.add(currentDeck.remove(0));
        }
    }

    private static boolean responsePlayer(Scanner scanner){
        //This method returns true if the player continues the game
        System.out.println("Enter your bet (minimum Bet to continue is " + (Math.abs(computerBet - playerBet)) + "):"); //abs -> since it doesn't matter who is raising
        System.out.println("Bets below the given minimum will be recognized as a fold");

        int response = scanner.nextInt();
        playerBet += response;
        int raise = playerBet - computerBet;

        if (raise < 0) {
            System.out.println("You folded. Computer Wins.");
            return false;
        } else if (raise == 0) {
            System.out.println("you have raised/called by " + response);
            playerMoney -= response;
            System.out.println("You are now betting a total of $" + playerBet + ", with a total of $"+ playerMoney + " remaining at your disposal.");
            return true;
        }
        else {
            System.out.println("you have raised/called by " + response);
            playerMoney -= response;
            System.out.println("You are now betting a total of $" + playerBet + ", with a total of $"+ playerMoney + " remaining at your disposal.");
            return true;
        }
    }

    private static void playerWins () {
        System.out.println("Player Wins");
        playerMoney += pot + computerBet + playerBet;
        computerMoney -= computerBet; //computer only looses his current bet.. as the money bet in the previous round already has been substracted
    }

    private static void computerWins () {
        System.out.println("Computer Wins");
        computerMoney += pot + computerBet + playerBet;
        playerMoney -= playerBet;
    }

    private static void bothWin () {
        System.out.println("Both Win");
        int share = (pot + computerBet + playerBet)/2;
        computerMoney += share;
        playerMoney += share;
    }

    private static boolean bettingRound() {

        Scanner scanner = new Scanner(System.in);

        //small blind starts betting --> the player with the least current bet starts
        if (bigBlind) {
            //Computer is big blind -> player starts
            // continue till a common bet amount is agreed upon
            //if it is not the preflop stage, the players will not start out with different bet amounts
            if (responsePlayer(scanner)) {
                int currentBet = pokerBrain.compute(playerBet, computerHand, communityCards);
                System.out.println("Computer has raised/called by " + currentBet);
                computerBet += currentBet;
                computerMoney -= currentBet;
                System.out.println("Computer is now betting a total of $" + computerBet + " and has $" + computerMoney + " left");
                if (computerBet < playerBet) {
                    PCWin = false;
                    return false;
                } else if (computerBet == playerBet) {
                    //if the second player has checked, it is not possible to re-raise
                    return true;
                }
                //else computer has raised...the loop is run again
            } else {
                PCWin = true;
                return false;
            }
            while (computerBet != playerBet) {
                if (responsePlayer(scanner)) {
                    if (computerBet == playerBet) {
                        //if the second player has checked, it is not possible to re-raise
                        return true;
                    }
                    int currentBet = pokerBrain.compute(playerBet, computerHand, communityCards);
                    System.out.println("Computer has raised/called by " + currentBet);
                    computerBet += currentBet;
                    computerMoney -= currentBet;
                    System.out.println("Computer is now betting a total of $" + computerBet + " and has $" + computerMoney + " left");
                    if (computerBet < playerBet) {
                        PCWin = false;
                        return false;
                    } else if (computerBet == playerBet) {
                        //if the second player has checked, it is not possible to re-raise
                        return true;
                    }
                    //else computer has raised...the loop is run again
                } else {
                    PCWin = true;
                    return false;
                }
            }

        }
        else {
            //Player is big blind -> PC starts
            // continue till a common bet amount is agreed upon
            //if it is not the preflop stage, the players will not start out with different bet amounts
            int currentBet = pokerBrain.compute(playerBet, computerHand, communityCards);
            int temp;
            if (stage == 0) {
                temp = BIG_BLIND;
            }
            else {
                temp = 0;
            }
            if (currentBet >= temp) {
                System.out.println("Computer has raised/called by " + currentBet);
                computerBet += currentBet;
                computerMoney -= currentBet;
                System.out.println("Computer is now betting a total of $" + computerBet + " and has $" + computerMoney + " left");
            }
            else {
                PCWin = false;
                return false;
            }
            if (!responsePlayer(scanner)) {
                PCWin = true;
                return false;
            }
            else if (computerBet == playerBet) {
                //player calls the raise
                return true;
            }
            //else player has raised...the loop is run again
            while (computerBet != playerBet) {
                currentBet = pokerBrain.compute(playerBet, computerHand, communityCards);
                if (currentBet < playerBet) {
                    PCWin = false;
                    return false;
                }
                System.out.println("Computer has raised/called by " + currentBet);
                computerBet += currentBet;
                computerMoney -= currentBet;
                System.out.println("Computer is now betting a total of $" + computerBet + " and has $" + computerMoney + " left");
                if (computerBet >= 0){
                    if (!responsePlayer(scanner)) {
                        PCWin = true;
                        return false;
                    }
                    else if (computerBet == playerBet) {
                        //player calls the raise
                        return true;
                    }
                    //else player has raised...the loop is run again
                }
                else {
                    PCWin = false;
                    return false;
                }
            }

        }
        return true;



    }

    //just a bit cosmetics..irrelevant
    /*
    private static void displayTable() {
        if (bigBlind){
            System.out.println("        +------------BB-------------+               Protocol:");
        }
        else {
            System.out.println("        +---------------------------+               Protocol:");
        }
        System.out.format("       /                             \\              Computer has $%d%n", computerMoney);
        System.out.format("      /         Computer: $%-5d      \\             Computer is currently betting $%d%n",computerMoney, computerBet);
        System.out.format("     /          Bet: $%-5d            \\            The Pot is $%d%n", computerBet, pot);
        System.out.format("    /                                   \\           You have $%d%n", playerMoney);
        System.out.format("   |                                     |          You are currently betting $%d%n", playerBet);
        System.out.println("   |                                     |");
        System.out.format("   |            POT: $%-5d              |%n", pot);
        System.out.println("   |                                     |");
        System.out.println("    \\                                   /");
        System.out.format("     \\          Bet: $%-5d            /%n", playerBet);
        System.out.format("      \\         Player: $%-5d        /%n", playerMoney);
        System.out.format("       \\          [%s] [%s]          /%n", playerHand.get(0), playerHand.get(1));
        if (bigBlind){
            System.out.println("        +---------------------------+");
        }
        else {
            System.out.println("        +------------BB-------------+");
        }
    }

     */



    // Determine Winner

    public static boolean determineWinner(List<Card> playerHand, List<Card> communityCards, List<Card> computerHand) {
        // method to determine the winner based on the best hand.
        List<Card> allPlayerCards = new ArrayList<>(playerHand);
        allPlayerCards.addAll(communityCards);
        List<Card> allComputerCards = new ArrayList<>(computerHand);
        allComputerCards.addAll(communityCards);

        //preDeterminationCalc
        maxValuePlayer = maxValue(allPlayerCards, false);
        maxSuitPlayer = maxSuit(allPlayerCards);
        maxValueComputer = maxValue(allComputerCards, true);
        maxSuitComputer = maxSuit(allComputerCards);

        int playerRank = evaluateHand(allPlayerCards, false);
        int computerRank = evaluateHand(allComputerCards, true);

        if (playerRank != computerRank) {
            if (playerRank < computerRank) {
                PCWin = true;
                setWinningHand(computerRank);
            } else {
                PCWin = false;
                setWinningHand(playerRank);
            }
            return true;
        }
        else { //case same card strength -> check for card rank -> pot is split
            if (sameHandKickerPlayer < sameHandKickerComputer) {
                PCWin = true;
                setWinningHand(computerRank);
                return true;
            } else if (sameHandKickerPlayer > sameHandKickerComputer){
                PCWin = false;
                setWinningHand(playerRank);
                return true;
            }
            else {
                setWinningHand(playerRank);
                return false;
            }
        }
    }

    private static void setWinningHand(int rank) {
        winningHand = switch (rank) {
            case 10 -> "Royal FLush";
            case 9 -> "Straight Flush";
            case 8 -> "Four of a Kind";
            case 7 -> "Full House";
            case 6 -> "Flush";
            case 5 -> "Straight";
            case 4 -> "Three of a Kind";
            case 3 -> "Two Pairs";
            case 2 -> "Pair";
            case 1 -> "High Card";
            default -> throw new IllegalStateException("Unexpected value: " + rank);
        };
    }

    public static int maxValue(List<Card> hand, boolean computer) {
        //number of same value cards (max)
        Map<Integer, Integer> valueCounts = new HashMap<>();
        int maxCount = 0;
        int ValueOfMaxCount = 0;

        for (Card card : hand) {
            int valueOfCard = card.getRank();
            int newCount = valueCounts.getOrDefault(valueOfCard, 0) + 1;
            valueCounts.put(valueOfCard, newCount);

            if (maxCount < newCount) {
                maxCount = newCount;
                ValueOfMaxCount = valueOfCard;
            }
        }

        if (computer) {
            numberOfValuesComputer = valueCounts.values().stream().toList();
        }
        else {
            numberOfValuesPlayer = valueCounts.values().stream().toList();
        }

        if (computer) {
            sameHandKickerComputer = ValueOfMaxCount;
        }
        else {
            sameHandKickerPlayer = ValueOfMaxCount;
        }

        return maxCount;

    }

    public static int maxSuit(List<Card> hand) {
        //number of same suit cards (max)
        Map<String, Integer> suitCounts = new HashMap<>();
        int count = 0;

        for (Card card : hand) {
            String suit = card.getSuit();
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }

        for (int value : suitCounts.values()) {
            if (count < value) {
                count = value;
            }
        }

        return count;
    }

    public static int evaluateHand(List<Card> hand, boolean computer) {
        // Sort the hand for easier evaluation
        Collections.sort(hand, Comparator.comparing(Card::getRank));

        if (isRoyalFlush(hand, computer)) return 10;
        if (isStraightFlush(hand, computer)) return 9;
        if (isFourOfAKind(hand, computer)) return 8;
        if (isFullHouse(hand, computer)) return 7;
        if (isFlush(hand, computer)) return 6;
        if (isStraight(hand, computer)) return 5;
        if (isThreeOfAKind(hand, computer)) return 4;
        if (isTwoPair(hand, computer)) return 3;
        if (isPair(hand, computer)) return 2;
        if (isHighCard(hand, computer)) return 1;// High card
        return -1; //error...something went wrong
    }

    public static boolean isRoyalFlush(List<Card> hand, boolean computer) {
        boolean royal = false;

        if (hand.stream().anyMatch(x -> x.getRank() == 14)) {
            if (hand.stream().anyMatch(x -> x.getRank() == 13)) {
                if (hand.stream().anyMatch(x -> x.getRank() == 12)) {
                    royal = true;
                }
            }
        }

        List<Card> hand2 = new ArrayList<>();
        for (Card card : hand) {
            if (card.getRank() >= 10) {
                hand2.add(card);
            }
        }

        if (hand2.size() < 5) {
            return false;
        }


        return isStraightFlush(hand2, computer) && royal;
    }

    public static boolean isStraightFlush(List<Card> hand, boolean computer) {
        return isFlush(hand, computer) && isStraight(hand, computer);
    }

    public static boolean isFourOfAKind(List<Card> hand, boolean computer) {
        int value = 0;
        if (computer) {
            value = maxValueComputer;
        }
        else {
            value = maxValuePlayer;
        }

        return value == 4;
    }

    public static boolean isFullHouse(List<Card> hand, boolean computer) {
        if (computer) {
            return numberOfValuesComputer.contains(3) && numberOfValuesComputer.contains(2);
        }
        else {
            return numberOfValuesPlayer.contains(3) && numberOfValuesPlayer.contains(2);
        }
    }

    public static boolean isFlush(List<Card> hand, boolean computer) {
        int value = 0;
        List<Integer> values = getCardValues(hand);
        value = maxSuit(hand);

        if (value >= 5) {
            if (computer) {
                sameHandKickerComputer = values.get(values.size() -1);
            }
            else {
                sameHandKickerPlayer = values.get(values.size() -1);
            }
        }

        return value >= 5;
    }

    public static boolean isStraight(List<Card> hand, boolean computer) {
        List<Integer> values = getCardValues(hand);
        int highestValueOfStraight = 0;
        boolean straight = false;

        for (int i = 0; i < hand.size() - 4; i++) {
            if (values.get(i + 1) - values.get(i) == 1) {
                if (values.get(i + 2) - values.get(i+1) == 1) {
                    if (values.get(i + 3) - values.get(i+2) == 1) {
                        if (values.get(i + 4) - values.get(i+3) == 1) {
                            highestValueOfStraight = i+4;
                            straight = true;
                        }
                    }
                }
            }
        }

        //case if Ace is 1 (A,2,3,4,5)
        if (values.contains(14)){
            if (values.contains(2)) {
                if (values.contains(3)) {
                    if (values.contains(4)) {
                        if (values.contains(5)) {
                            highestValueOfStraight = 5;
                            straight = true;
                        }
                    }
                }
            }
        }

        if (straight) {
            if (computer) {
                sameHandKickerComputer = highestValueOfStraight;
            }
            else {
                sameHandKickerPlayer = highestValueOfStraight;
            }
            return true;
        }

        return false;
    }

    public static List<Integer> getCardValues(List<Card> hand) {
        List <Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getRank());
        }
        Collections.sort(values);
        return values;
    }

    public static boolean isThreeOfAKind(List<Card> hand, boolean computer) {
        int value = 0;
        if (computer) {
            value = maxValueComputer;
        }
        else {
            value = maxValuePlayer;
        }

        return value == 3;
    }

    public static boolean isTwoPair(List<Card> hand, boolean computer) {
        int pairCount = 0;
        int highPair = 0;

        if (computer) {
            for (Integer i : numberOfValuesComputer) {
                if (i == 2) {
                    pairCount++;
                    if (i > highPair) {
                        highPair = i;
                    }
                }
            }
        }
        else {
            for (Integer i : numberOfValuesPlayer) {
                if (i == 2) {
                    pairCount++;
                    if (i > highPair) {
                        highPair = i;
                    }
                }
            }
        }

        if (pairCount >= 2) {
            if (computer) {
                sameHandKickerComputer = highPair;
            }
            else {
                sameHandKickerPlayer = highPair;
            }
        }

        return pairCount >= 2;
    }

    public static boolean isPair(List<Card> hand, boolean computer) {
        if (computer) {
            return numberOfValuesComputer.contains(2);
        }
        else {
            return numberOfValuesPlayer.contains(2);
        }
    }

    public static boolean isHighCard(List<Card> hand, boolean computer) {
        List<Integer> values = getCardValues(hand);
        if (computer) {
            sameHandKickerComputer = values.get(6);
        }
        else {
            sameHandKickerPlayer = values.get(6);
        }
        return true;
    }

    public static String getWinningHand() {
        return winningHand;
    }

    public static int getStage() {
        return stage;
    }

    public static void main(String[] args) {
        startGame();
    }
}