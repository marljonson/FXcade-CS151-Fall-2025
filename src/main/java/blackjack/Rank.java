package blackjack;

public enum Rank {
    TWO(2, '2'),
    THREE(3, '3'),
    FOUR(4, '4'),
    FIVE(5, '5'),
    SIX(6, '6'),
    SEVEN(7, '7'),
    EIGHT(8, '8'),
    NINE(9, '9'),
    TEN(10, 'T'),
    JACK(10, 'J'),
    QUEEN(10, 'Q'),
    KING(10, 'K'),
    ACE(11, 'A');

    // value denotes the score added based on the rank pulled.
    private final int value;
    private final char rankChar;

    Rank(int value, char rankChar) {
        this.value = value;
        this.rankChar = rankChar;
    }

    public int getValue() {
        return value;
    }

    public char getRankChar() {
        return rankChar;
    }

    public static Rank fromChar(char c) {
        switch (Character.toUpperCase(c)) {
            case '2':
                return TWO;
            case '3':
                return THREE;
            case '4':
                return FOUR;
            case '5':
                return FIVE;
            case '6':
                return SIX;
            case '7':
                return SEVEN;
            case '8':
                return EIGHT;
            case '9':
                return NINE;
            case 'T':
                return TEN;
            case 'J':
                return JACK;
            case 'Q':
                return QUEEN;
            case 'K':
                return KING;
            case 'A':
                return ACE;
            default:
                throw new IllegalArgumentException("Invalid rank character: " + c);
        }
    }
}