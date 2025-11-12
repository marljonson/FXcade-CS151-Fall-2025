package main.java.blackjack;

import java.util.*;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public List<Card> getCards() { return Collections.unmodifiableList(cards); }// cards in hand
    public void clear(){ cards.clear(); }                                       // clear cards from hand
    public void add(Card card){ cards.add(Objects.requireNonNull(card)); }      // add card to hand
    public int size(){ return cards.size(); }                                   // Number of cards in hand

    
}
