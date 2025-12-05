package blackjack;

@FunctionalInterface // This interface has exactly one abstract method
public interface BotStrategy {

    // Possible actions a bot can take in blackjack
    enum Action { HIT, STAND }

    // Decide which action the bot should take based on its own hand and the dealer's visible card
    Action decide(Hand ownHand, Card dealerUpCard);

    // Create a strategy that always hits until the hand total reaches the given threshold
    static BotStrategy hitUnder(int threshold) { // Static factory method; returns a BotStrategy
        return (hand, upCard) -> {
            // Get the best score for the bot's hand
            int total = hand.getBestTotal();
            // Determine whether the bot should hit based on the threshold
            boolean shouldHit = total < threshold;
            // Return HIT if total is below threshold, otherwise STAND
            return shouldHit ? Action.HIT : Action.STAND;
        };
    }
}