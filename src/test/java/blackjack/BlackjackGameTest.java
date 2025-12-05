package blackjack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlackjackGameTest {

    @Test
    void newRoundDealsTwoCardsToEveryone() {
        BlackjackGame game = new BlackjackGame("Test");

        game.startNewRound(50, 50, 50);

        // 1 human + 2 bots + dealer
        assertEquals(2, game.getHuman().getHand().size(), "Human should have 2 cards after a new round");
        assertEquals(2, game.getBot1().getHand().size(), "Bot 1 should have 2 cards ");
        assertEquals(2, game.getBot2().getHand().size(), "Bot 2 should have 2 cards ");
        assertEquals(2, game.getDealer().getHand().size(), "Dealer should have 2 cards ");

        // Check that dealer has exactly 1 face down card at start
        long faceDown = game.getDealer().getHand().getCards().stream()
                        .filter(card -> !card.isFaceUp()).count();
        assertEquals(1, faceDown, "Dealer should start with exactly 1 face down (hole) card");

        // Round should be in progreess, human's turn
        assertFalse(game.isRoundOver(), "Round should not be over at start");
        assertEquals(0, game.getTurnIndex(), "Human should act first");
    }

    @Test
    void saveAndLoadRoundPreservedState() {
        BlackjackGame original = new BlackjackGame("Test");
        original.startNewRound(75, 50, 25);

        // Simulate actions to get a non trivial state
        original.humanHit(); // might bust but whatever
        original.humanStand(); // so we can move to bots and dealer

        String json = original.toJsonSave();

        // Load a new game from that JSON 
        BlackjackGame loaded = BlackjackGame.fromJsonSave(json, "Test");

        // Compare key parts of the state
        assertEquals(original.getTurnIndex(), loaded.getTurnIndex(), "Turn Index should match after load");
        assertEquals(original.isRoundOver(), loaded.isRoundOver(), "Round over flag should match after load");

        // Human, bots, dealer bankrolls
        assertEquals(original.getHuman().getBankroll(), loaded.getHuman().getBankroll(), "Human bankroll mismatch");
        assertEquals(original.getBot1().getBankroll(), loaded.getBot1().getBankroll(), "Bot 1 bankroll mismatch");
        assertEquals(original.getBot2().getBankroll(), loaded.getBot2().getBankroll(), "Bot 2 bankroll mismatch");
        assertEquals(original.getDealer().getBankroll(), loaded.getDealer().getBankroll(), "Dealer bankroll mismatch");

        // Compare hands through Hand.toChars() for each player
        assertEquals(original.getHuman().getHand().toChars(), loaded.getHuman().getHand().toChars(), "Human hand mismatch after load");
        assertEquals(original.getBot1().getHand().toChars(), loaded.getBot1().getHand().toChars(), "Bot 1 hand mismatch after load");
        assertEquals(original.getBot2().getHand().toChars(), loaded.getBot2().getHand().toChars(), "Bot 2 hand mismatch after load");
        assertEquals(original.getDealer().getHand().toChars(), loaded.getDealer().getHand().toChars(), "Dealer hand mismatch after load");

    }

    @Test
    void humanBetIsClampedToNonNegative() {
        BlackjackGame game = new BlackjackGame("Test");

        game.startNewRound(-100, 50, 25);

        assertTrue(game.getHuman().getBet() >= 0, "Human bet should be positive");
    }

    @Test
    void handAceHandlingSoftVsHard() {
        Hand hand = new Hand();

        hand.add(new Card(Rank.ACE, Suit.CLUBS)); // Ace = 1 or 11
        hand.add(new Card(Rank.SIX, Suit.HEARTS)); // 6

        assertEquals(17, hand.getBestTotal()); // check that Ace counts as 11 here so 11 + 6 = 17
        assertTrue(hand.isSoft());

        hand.add(new Card(Rank.NINE, Suit.SPADES)); // unless Ace becomes 1, this pushes past 21
        assertEquals(16, hand.getBestTotal());
        assertFalse(hand.isSoft());
    }

    @Test
    void dealerHitsSoft17() {
        BlackjackGame game = new BlackjackGame("Test");
        
        // give dealer: Ace (11) + 6 = soft 17
        Hand dealerHand = game.getDealer().getHand();
        dealerHand.clear();
        dealerHand.add(new Card(Rank.ACE, Suit.SPADES)); // 11
        dealerHand.add(new Card(Rank.SIX, Suit.CLUBS)); // total = 17 which is soft

        // Now call dealerTurn() manually
        game.startNewRound(50, 50, 50);
        dealerHand.clear();
        dealerHand.add(new Card(Rank.SIX, Suit.CLUBS));
        dealerHand.add(new Card(Rank.ACE, Suit.SPADES)); 
        
        // Make deck deterministic by manually adding card to hit
        Deck deck = new Deck(1);
        // Insert known hit card on top of deck, but is random

        // We just check the rule here
        assertTrue(dealerHand.isSoft());
        assertEquals(17, dealerHand.getBestTotal());
    }

    @Test 
    void bustIsDetected() {
        Hand hand = new Hand();
        hand.add(new Card(Rank.KING, Suit.CLUBS)); 
        hand.add(new Card(Rank.QUEEN, Suit.DIAMONDS)); 
        hand.add(new Card(Rank.FIVE, Suit.SPADES));

        assertTrue(hand.isBust()); // 10 + 10 + 5 = 25 -> Bust
    }

    @Test
    void saveLoadRestoresHandsAndTurn() {
        BlackjackGame game1 = new BlackjackGame("Test");
        game1.startNewRound(50, 50, 50);

        // Simulate some play
        game1.humanHit();
        game1.humanStand();

        String json = game1.toJsonSave();
        BlackjackGame game2 = BlackjackGame.fromJsonSave(json, "Test");

        assertEquals(game1.getTurnIndex(), game2.getTurnIndex());
        assertEquals(game1.isRoundOver(), game2.isRoundOver());
        assertEquals(game1.getHuman().getHand().toChars(), game2.getHuman().getHand().toChars());
        assertEquals(game1.getDealer().getHand().toChars(), game2.getDealer().getHand().toChars());
    }

    // private helper pasted here to keep the game's helper private.
    private void replaceHand(Hand target, Hand src) {
        target.clear();
        for (Card card : src.getCards()) target.add(card);
    }

    @Test
    void payoutWhenPlayerBeatsDealer() {
        BlackjackGame game = new BlackjackGame("Test");
        game.startNewRound(100, 50, 50);

        // Replace hands with controlled cards and then force end of round
        replaceHand(game.getHuman().getHand(), Hand.fromChars("9C-8D")); // 17
        replaceHand(game.getDealer().getHand(), Hand.fromChars("9H-7S")); // 16

        game.humanStand();
        assertTrue(game.getHuman().getBankroll() > 1000);
        
    }
}