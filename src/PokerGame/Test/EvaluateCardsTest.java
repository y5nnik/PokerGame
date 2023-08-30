package PokerGame.Test;

import PokerGame.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluateCardsTest {

    //the methods in Main have been set to public to be able to test them


    @Test
    public void testRoyalFlushRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "H"), new Card(4, "H"));
        List<Card> computerHand = Arrays.asList(new Card(14, "H"), new Card(12, "H"));
        List<Card> communityCards = Arrays.asList(new Card(13, "H"), new Card(11, "H"), new Card(9, "S"), new Card(10, "H"), new Card(4, "H"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Royal FLush");
    }

    @Test
    public void testRoyalFlushWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "H"), new Card(4, "H"));
        List<Card> computerHand = Arrays.asList(new Card(14, "S"), new Card(12, "H"));
        List<Card> communityCards = Arrays.asList(new Card(13, "H"), new Card(11, "H"), new Card(9, "S"), new Card(10, "H"), new Card(4, "H"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Royal FLush");
    }

    @Test
    public void testStraightFlushRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "H"), new Card(4, "H"));
        List<Card> computerHand = Arrays.asList(new Card(8, "H"), new Card(12, "H"));
        List<Card> communityCards = Arrays.asList(new Card(13, "H"), new Card(11, "H"), new Card(9, "S"), new Card(10, "H"), new Card(4, "H"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Straight Flush");
    }

    @Test
    public void testStraightFlushWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(9, "S"), new Card(12, "H"));
        List<Card> communityCards = Arrays.asList(new Card(13, "H"), new Card(11, "H"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Straight Flush");
    }

    @Test
    public void testFourOfAKindRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(8, "D"), new Card(8, "C"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Four of a Kind");
    }

    @Test
    public void testFourOfAKindWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(8, "D"), new Card(9, "C"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Four of a Kind");
    }

    @Test
    public void testFullHouseRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(12, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(4, "H"));
        List<Card> communityCards = Arrays.asList(new Card(8, "D"), new Card(8, "C"), new Card(5, "S"), new Card(5, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Full House");
    }

    @Test
    public void testFullHouseWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(8, "D"), new Card(9, "C"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Full House");
    }

    @Test
    public void testFlushRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(12, "C"));
        List<Card> computerHand = Arrays.asList(new Card(9, "S"), new Card(2, "H"));
        List<Card> communityCards = Arrays.asList(new Card(8, "C"), new Card(8, "C"), new Card(5, "C"), new Card(5, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Flush");
    }

    @Test
    public void testFlushWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(8, "D"), new Card(9, "C"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Flush");
    }

    @Test
    public void testStraightRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(9, "S"), new Card(12, "H"));
        List<Card> communityCards = Arrays.asList(new Card(13, "H"), new Card(11, "H"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Straight");
    }

    @Test
    public void testStraightWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(9, "S"), new Card(12, "H"));
        List<Card> communityCards = Arrays.asList(new Card(14, "H"), new Card(11, "H"), new Card(5, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Straight");
    }

    @Test
    public void testThreeOfAKindRight() {

        List<Card> playerHand = Arrays.asList(new Card(14, "C"), new Card(14, "S"));
        List<Card> computerHand = Arrays.asList(new Card(7, "S"), new Card(14, "H"));
        List<Card> communityCards = Arrays.asList(new Card(7, "C"), new Card(14, "C"), new Card(5, "C"), new Card(3, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Three of a Kind");
    }

    @Test
    public void testThreeOfAKindWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "D"), new Card(9, "C"), new Card(9, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Three of a Kind");
    }

    @Test
    public void testTwoPairRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(12, "S"));
        List<Card> computerHand = Arrays.asList(new Card(9, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "C"), new Card(8, "C"), new Card(5, "C"), new Card(7, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Two Pairs");
    }

    @Test
    public void testTwoPairWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "D"), new Card(9, "C"), new Card(9, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Two Pairs");
    }

    @Test
    public void testPairRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(12, "S"));
        List<Card> computerHand = Arrays.asList(new Card(9, "S"), new Card(2, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "C"), new Card(3, "C"), new Card(5, "C"), new Card(7, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Pair");
    }

    @Test
    public void testPairWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "D"), new Card(9, "C"), new Card(9, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "Pair");
    }


    @Test
    public void testHighCardRight() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(12, "S"));
        List<Card> computerHand = Arrays.asList(new Card(10, "S"), new Card(2, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "C"), new Card(3, "C"), new Card(5, "C"), new Card(7, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "High Card");
    }

    @Test
    public void testHighCardWrong() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(9, "D"), new Card(9, "C"), new Card(9, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertNotEquals(Main.getWinningHand(), "High Card");
    }

    @Test
    public void testDifferentPairs() {

        List<Card> playerHand = Arrays.asList(new Card(2, "C"), new Card(5, "S"));
        List<Card> computerHand = Arrays.asList(new Card(8, "S"), new Card(8, "H"));
        List<Card> communityCards = Arrays.asList(new Card(2, "D"), new Card(9, "C"), new Card(12, "S"), new Card(10, "H"), new Card(4, "S"));

        assertTrue(Main.determineWinner(playerHand, communityCards, computerHand)); // Assuming it returns true if a player wins
        assertEquals(Main.getWinningHand(), "Pair");
    }



}
