package main.java.blackjack;

import java.util.*;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public List<Card> getCards() { return Collections.unmodifiableList(cards); }// cards in hand
    public void clear(){ cards.clear(); }                                       // clear cards from hand
    public void add(Card card){ cards.add(Objects.requireNonNull(card)); }      // add card to hand
    public int size(){ return cards.size(); }                                   // Number of cards in hand

    /*
     * Calculate total for hand, Ace may be 1 or 11
     * Unique total
     */
    public List<Integer> getTotals(){
        int sum = 0;
        int aceCount = 0;

        for(Card card : cards){
            sum += card.getValue();
            if (card.getValue() == 11) {
                aceCount++;
            }
        }

        // Adjust for Aces, also preserve insertion order for sorting
        Set<Integer> totals = new LinkedHashSet<>();
        int current = sum;
        for(int i = 0; i <= aceCount; i++){
            totals.add(current);
            current -= 10; // reduce ace from 11 to 1 prevent busting
        }
        List<Integer> sorted = new ArrayList<>(totals);
        Collections.sort(sorted);
        return sorted;
    }
}
