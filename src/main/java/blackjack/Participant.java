package blackjack;

public class Participant {
    private final String name;
    private final boolean human;
    private final boolean dealer;
    private final Hand hand = new Hand();

    private int bankroll = 1000; // default starting money
    private int bet = 0;

    public Participant(String name, boolean human, boolean dealer) {
        this.name = name;
        this.human = human;
        this.dealer = dealer;
    }

    // player related getters
    public String getName() { return name; }
    public boolean isHuman() { return human; }
    public boolean isDealer() { return dealer; }
    public Hand getHand() { return hand; }

    // game related methods
    public int getBankroll() { return bankroll; }
    public int getBet() { return bet; }
    public void setBet(int amount) { this.bet = amount; }
    public void win(int amount){ bankroll += amount; }
    public void lose(int amount){ bankroll -= amount; }
    public void push() {} // no change in case of tie

    public void clearForNextRound(){
        hand.clear();
        bet = 0;
    }
    

    
    
}
