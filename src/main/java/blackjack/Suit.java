package blackjack;

public enum Suit {
    CLUBS('C'), DIAMONDS('D'), HEARTS('H'), SPADES('S');

    private final char suitChar;

    Suit(char suitChar) {
        this.suitChar = suitChar;
    }

    public char getSuitChar() { return suitChar; }

    public static Suit fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'C' -> CLUBS;
            case 'D' -> DIAMONDS;
            case 'H' -> HEARTS;
            case 'S' -> SPADES;
            default -> throw new IllegalArgumentException("Invalid suit: " + c);
        };
    }
}