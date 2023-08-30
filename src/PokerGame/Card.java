package PokerGame;

public class Card {

    private int rank;
    // 2 -> 14 (j=11; Q=12; K=13; A = 14)
    private String suit;
    private String name;

    public Card (int rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        if (rank <= 10) {
            name = rank + suit;
        }
        else {
            String cardChar = switch (rank) {
                case 11 -> "J";
                case 12 -> "Q";
                case 13 -> "K";
                default -> "A";
            };
            name = cardChar + suit;
        }
    }

    public int getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getName() {
        return name;
    }
}
