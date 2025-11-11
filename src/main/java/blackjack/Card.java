package main.java.blackjack;

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

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public boolean isFaceUp() { return faceUp; }

    
    

    
}
