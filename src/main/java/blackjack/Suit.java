package blackjack;

public enum Suit {
    CLUBS('C'), DIAMONDS('D'), HEARTS('H'), SPADES('S');

    private final char suitChar;

    Suit(char suitChar){ this.suitChar = suitChar; }
    public char getSuitChar(){ return suitChar; }

    public static Suit fromChar(char c){
        switch (c) {
            case 'C': return CLUBS;
            case 'D': return DIAMONDS;
            case 'H': return HEARTS;
            case 'S': return SPADES;
            default: throw new IllegalArgumentException("Invalid suit character: " + c);
                
        }
    }
}
