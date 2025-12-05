package blackjack;

// Migrate Strategies.java into BotStrategy.java
@FunctionalInterface
public interface BotStrategy {
    enum Action { HIT, STAND }

    Action decide(Hand ownHand, Card dealerUpCard);

    static BotStrategy hitUnder(int threshold) {
        return (hand, upCard) -> hand.getBestTotal() < threshold ? Action.HIT : Action.STAND;
    }
}