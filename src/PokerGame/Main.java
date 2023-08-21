package PokerGame;

import java.sql.SQLOutput;
import java.util.*;

public class Main {

    /*TODO
        edgecases for round > 1
        displaytable with cards
        hand rank
        problem with displayTable (preflop display)
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

    private static int stage;
    /*
    0 -> pre flop
    1 -> flop
    2 -> turn
    3 -> river
     */

    private static List<String> standardDeck = new ArrayList<>();
    private static List<String> currentDeck = new ArrayList<>();
    private static List<String> playerHand = new ArrayList<>();
    private static List<String> computerHand = new ArrayList<>();
    private static List<String> communityCards = new ArrayList<>();
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

    private static void updateMoney() {
        computerMoney -= computerBet;
        playerMoney -= playerBet;
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

        System.out.println("Your cards: " + playerHand);

        //2. big and small blind
        if (bigBlind){
            System.out.println("Computer is Big Blind");
            computerBet = BIG_BLIND;
            playerBet = SMALL_BLIND;
        }
        else {
            System.out.println("You are Big Blind");
            computerBet = SMALL_BLIND;
            playerBet = BIG_BLIND;
        }

        // 3. Preflop
        if (!bettingRound()) {
            endGame(true);
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }
        displayTable();

        // 4. Reveal community cards (flop, turn, river)
        // 4.1 Flop
        stage++;
        dealCards(communityCards, 3); // Flop
        System.out.println("Flop: " + communityCards);
        if (!bettingRound()){
            endGame(true);
        }
        else {
            pot += computerBet + playerBet;

            computerBet = playerBet = 0;
        }
        displayTable();

        // 4.2 turn
        stage++;
        dealCards(communityCards, 1); // Turn
        System.out.println("Turn: " + communityCards);
        if (!bettingRound()){
            endGame(true);
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }
        displayTable();

        // 4.3 river
        stage++;
        dealCards(communityCards, 1); // River
        System.out.println("River: " + communityCards);

        if (!bettingRound()){
            endGame(true);
        }
        else {
            pot += computerBet + playerBet;
            computerBet = playerBet = 0;
        }
        displayTable();

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
        }
        else {
            System.out.println("the pot of $" + pot + " is split!");
            bothWin();
        }

        System.out.println("Computer had the following cards: ["+ computerHand.get(0) +"] ["+ computerHand.get(1) +"]");
        //TODO show the winning hand eg Computer wins with pair of queens

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
                if (i <= 10) {
                    standardDeck.add(i+suit);
                } else {
                    char cardChar = switch (i) {
                        case 11 -> 'J';
                        case 12 -> 'Q';
                        case 13 -> 'K';
                        default -> 'A';
                    };
                    standardDeck.add(cardChar+suit);
                }
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

    private static void dealCards(List<String> hand, int numCards) {
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
            System.out.println("You call/check.");
            return true;
        }
        else {
            System.out.println("You raise.");
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

        try {
            Scanner scanner = new Scanner(System.in);

            //small blind starts betting --> the player with the least current bet starts
            if (bigBlind) {
                //Computer is big blind -> player starts
                // continue till a common bet amount is agreed upon
                if (stage == 0) {
                    while (computerBet != playerBet) {
                        if (responsePlayer(scanner)){
                            computerBet += pokerBrain.compute(playerBet, computerHand, communityCards);
                            if (computerBet < playerBet) {
                                PCWin = false;
                                return false;
                            }
                            //else computer has raised of called...either way the loop is run again
                        }
                        else {
                            PCWin = true;
                            return false;
                        }
                    }
                }
                else {
                    //if it is not the preflop stage, the players will not start out with different bet amounts
                    if (responsePlayer(scanner)){
                        computerBet += pokerBrain.compute(playerBet, computerHand, communityCards);
                        if (computerBet < playerBet) {
                            PCWin = false;
                            return false;
                        }
                        //else computer has raised of called...either way the loop is run
                    }
                    else {
                        PCWin = true;
                        return false;
                    }
                    while (computerBet != playerBet) {
                        if (responsePlayer(scanner)){
                            computerBet += pokerBrain.compute(playerBet, computerHand, communityCards);
                            if (computerBet < playerBet) {
                                PCWin = false;
                                return false;
                            }
                            //else computer has raised of called...either way the loop is run again
                        }
                        else {
                            PCWin = true;
                            return false;
                        }
                    }
                }
            }
            else {
                //Player is big blind -> PC starts
                // continue till a common bet amount is agreed upon
                if (stage == 0) {
                    while (computerBet != playerBet) {
                        computerBet += pokerBrain.compute(playerBet, computerHand, communityCards);
                        if (computerBet >= playerBet){
                            if (!responsePlayer(scanner)) {
                                PCWin = true;
                                return false;
                            }
                            //else player has raised or called...either way the loop is run again
                        }
                        else {
                            PCWin = false;
                            return false;
                        }
                    }
                }
                else {
                    //if it is not the preflop stage, the players will not start out with different bet amounts
                    computerBet += pokerBrain.compute(playerBet, computerHand, communityCards);
                    if (computerBet >= playerBet){
                        if (!responsePlayer(scanner)) {
                            PCWin = true;
                            return false;
                        }
                        //else computer has raised of called...either way the loop is run
                    }
                    else {
                        PCWin = false;
                        return false;
                    }
                    while (computerBet != playerBet) {
                        computerBet += pokerBrain.compute(playerBet, computerHand, communityCards);
                        if (computerBet >= 0){
                            if (!responsePlayer(scanner)) {
                                PCWin = true;
                                return false;
                            }
                            //else player has raised or called...either way the loop is run again
                        }
                        else {
                            PCWin = false;
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        finally {
            updateMoney();
        }


    }

    //just a bit cosmetics..irrelevant
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


    private static boolean determineWinner(List<String> playerHand, List<String> communityCards, List<String> computerHand) {
        // method to determine the winner based on the best hand.
        List<String> allPlayerCards = new ArrayList<>(playerHand);
        allPlayerCards.addAll(communityCards);
        List<String> allComputerCards = new ArrayList<>(computerHand);
        allComputerCards.addAll(communityCards);

        int playerRank = evaluateHand(allPlayerCards);
        int computerRank = evaluateHand(allComputerCards);

        if (playerRank != computerRank) {
            if (playerRank < computerRank) {
                PCWin = true;
            } else {
                PCWin = false;
            }
            return true;
        }
        else { //case same card strength -> pot is split
            return false;
        }
    }

    private static int evaluateHand(List<String> hand) {
        // Sort the hand for easier evaluation
        Collections.sort(hand);

        if (isRoyalFlush(hand)) return 1;
        if (isStraightFlush(hand)) return 2;
        if (isFourOfAKind(hand)) return 3;
        if (isFullHouse(hand)) return 4;
        if (isFlush(hand)) return 5;
        if (isStraight(hand)) return 6;
        if (isThreeOfAKind(hand)) return 7;
        if (isTwoPair(hand)) return 8;
        if (isPair(hand)) return 9;
        return 10; // High card
    }

    private static boolean isRoyalFlush(List<String> hand) {
        return isStraightFlush(hand) && hand.contains("AH");
    }

    private static boolean isStraightFlush(List<String> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    private static boolean isFourOfAKind(List<String> hand) {
        Map<String, Integer> counts = getCardCounts(hand);
        return counts.values().contains(4);
    }

    private static boolean isFullHouse(List<String> hand) {
        Map<String, Integer> counts = getCardCounts(hand);
        return counts.values().contains(3) && counts.values().contains(2);
    }

    private static boolean isFlush(List<String> hand) {
        char suit = hand.get(0).charAt(0);
        for (String card : hand) {
            if (card.charAt(0) != suit) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStraight(List<String> hand) {
        List<Integer> values = getCardValues(hand);
        Collections.sort(values);
        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i + 1) - values.get(i) != 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean isThreeOfAKind(List<String> hand) {
        Map<String, Integer> counts = getCardCounts(hand);
        return counts.values().contains(3);
    }

    private static boolean isTwoPair(List<String> hand) {
        Map<String, Integer> counts = getCardCounts(hand);
        int pairCount = 0;
        for (int count : counts.values()) {
            if (count == 2) {
                pairCount++;
            }
        }
        return pairCount == 2;
    }

    private static boolean isPair(List<String> hand) {
        Map<String, Integer> counts = getCardCounts(hand);
        return counts.values().contains(2);
    }

    private static Map<String, Integer> getCardCounts(List<String> hand) {
        Map<String, Integer> counts = new HashMap<>();
        for (String card : hand) {
            String rank = card.substring(1);
            counts.put(rank, counts.getOrDefault(rank, 0) + 1);
        }
        return counts;
    }

    private static List<Integer> getCardValues(List<String> hand) {
        List<Integer> values = new ArrayList<>();
        for (String card : hand) {
            char rank = card.charAt(1);
            if (Character.isDigit(rank)) {
                values.add(Character.getNumericValue(rank));
            } else {
                switch (rank) {
                    case 'J':
                        values.add(11);
                        break;
                    case 'Q':
                        values.add(12);
                        break;
                    case 'K':
                        values.add(13);
                        break;
                    case 'A':
                        values.add(14);
                        break;
                }
            }
        }
        return values;
    }



    public static void main(String[] args) {
        startGame();
    }
}