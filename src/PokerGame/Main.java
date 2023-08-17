package PokerGame;

import java.sql.SQLOutput;
import java.util.*;

public class Main {

    private static final int STARTING_MONEY = 1000;
    private static boolean bigBlind;
    private static final int BIG_BLIND = STARTING_MONEY / 100;
    private static final int SMALL_BLIND = BIG_BLIND / 2;

    private static int playerMoney = STARTING_MONEY;
    private static int computerMoney = STARTING_MONEY;
    private static int playerBet;
    private static int computerBet;

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
            System.out.println("Human wins! (for now)");
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
        System.out.println("\n ");
    }

    private static void playRound() {
        //0. SetUp
        Scanner scanner = new Scanner(System.in);
        initializeDeck();
        playerHand.clear();
        computerHand.clear();
        communityCards.clear();

        // 1. Deal cards to player and computer
        dealCards(playerHand, 2);
        dealCards(computerHand, 2);

        System.out.println("Your cards: " + playerHand);

        //2. big and small blind
        if (bigBlind){
            System.out.println("Computer is Big Blind");
            computerBet = BIG_BLIND;
            playerBet = SMALL_BLIND;
            computerMoney -= BIG_BLIND;
            playerMoney -= SMALL_BLIND;
        }
        else {
            System.out.println("You are Big Blind");
            computerBet = SMALL_BLIND;
            playerBet = BIG_BLIND;
            computerMoney -= SMALL_BLIND;
            playerMoney -= BIG_BLIND;
        }
        int pot = SMALL_BLIND + BIG_BLIND;
        displayTable(0);

        // 4. Preflop
        pot += bettingRound();

        // 3. Reveal community cards (flop, turn, river)
        dealCards(communityCards, 3); // Flop
        System.out.println("Flop: " + communityCards);
        pot += bettingRound();

        dealCards(communityCards, 1); // Turn
        System.out.println("Turn: " + communityCards);
        pot += bettingRound();

        dealCards(communityCards, 1); // River
        System.out.println("River: " + communityCards);

        // 4. Final betting round
        pot += bettingRound();

        // 5. Determine winner and award pot
        if (determineWinner(playerHand, communityCards, computerHand)) {
            System.out.println("You win the pot of $" + pot + "!");
            playerMoney += pot;
        } else {
            System.out.println("Computer wins the pot of $" + pot + "!");
            computerMoney += pot;
        }

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

    private static int bettingRound() {
        int pot = 0;
        Scanner scanner = new Scanner(System.in);
        while(playerBet < computerBet) {
            //case for small blind, player has to call
            System.out.println("Enter your bet (or 0 to check):");
            int playerBet = scanner.nextInt();
        }
        int computerBet = pokerBrain.compute(playerBet, computerHand, communityCards);
        System.out.println("Computer bets: $" + computerBet);
        if (computerBet > playerBet) {
            System.out.println("Do you want to match/raise the bet? (y/n)");
            String response = scanner.next();
            if (response.equalsIgnoreCase("y")) {
                System.out.println("Enter your bet (minimum Bet to continue is "+ (computerBet-playerBet) +"):");
                int playerBet = scanner.nextInt();

                if (playerBet > computerBet) { //player raises..in this case the computer should fold as it always bets the expected value/ theorems...exploitable will improve later
                    System.out.println("You folded. You wins this round.");
                    playerMoney += pot;
                    return playerBet;
                }
                playerMoney -= computerBet;
                computerMoney -= computerBet;
                return 2 * computerBet;
            } else {
                System.out.println("You folded. Computer wins this round.");
                computerMoney += playerBet;
                return playerBet;
            }
        } else {
            playerMoney -= playerBet;
            computerMoney -= playerBet;
            return 2 * playerBet;
        }
    }

    //just a bit cosmetics..irrelevant
    private static void displayTable(int pot) {
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

        if (playerRank < computerRank) {
            return true;
        } else if (playerRank > computerRank) {
            return false;
        } else {
            // If both players have the same rank, we need to evaluate further
            // For simplicity, we'll just compare the highest card for now.
            String highestPlayerCard = Collections.max(allPlayerCards);
            String highestComputerCard = Collections.max(allComputerCards);
            return highestPlayerCard.compareTo(highestComputerCard) > 0;
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