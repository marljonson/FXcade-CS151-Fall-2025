package blackjack;

public interface BotStrategy {
    enum Action{ HIT, STAND }
    Action decide(Hand self, Card dealerUpCard);
}
