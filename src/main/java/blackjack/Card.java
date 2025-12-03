package blackjack;

import java.util.Objects;

// Final since Card is immutable
public final class Card {
    private final Rank rank;
    private final Suit suit;
    private final boolean faceUp; // dealer's hidden card: true - visible, false - hidden (face down)

    // assume card is visible | ie: your own hand
    public Card(Rank rank, Suit suit) {
        this(rank, suit, true);
    }

    // main constructor -- obviously.
    public Card(Rank rank, Suit suit, boolean faceUp) {
        this.rank = Objects.requireNonNull(rank);
        this.suit = Objects.requireNonNull(suit);
        this.faceUp = faceUp;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    // purely to flip a card's visibility, since card is immutable
    public Card flipped() {
        return new Card(rank, suit, !faceUp);
    }

    // Blackjack value, Ace is 11 but Hand will set it 1 if needed.
    public int getValue() {
        return rank.getValue();
    }


    // compact abbreviation of hand names, like Ace of Hearts -> AH , Ten of Clubs -> TC
    @Override
    public String toString() {
        return "" + rank.getRankChar() + suit.getSuitChar();
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Card)) return false;
        Card card = (Card) other;
        return rank == card.rank && suit == card.suit && faceUp == card.faceUp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit, faceUp);
    }

    // we check if the 'str' is exactly 2 characters and exists
    public static Card fromChars(String str) {
        if (str == null || str.length() != 2) {
            throw new IllegalArgumentException("Card string must be 2 characters: " + str);
        }
        return new Card(Rank.fromChar(str.charAt(0)), Suit.fromChar(str.charAt(1)));
    }

}
