package main.java.blackjack;

public final class Strategies {
    private Strategies(){}
    public static BotStrategy hitUnder(int threshold){
        return (self, dealerUp) -> self.getBestTotal() < threshold
                ? BotStrategy.Action.HIT : BotStrategy.Action.STAND;
    }
}
