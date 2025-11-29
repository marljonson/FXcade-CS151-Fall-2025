package blackjack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlackjackGameTest {
    @Test
    void newRoundDealsTwoCardsToEveryone(){
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
    
}
