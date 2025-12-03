package blackjack;

import java.util.*;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }// cards in hand

    public void clear() {
        cards.clear();
    }                                       // clear cards from hand

    public void add(Card card) {
        cards.add(Objects.requireNonNull(card));
    }      // add card to hand

    public int size() {
        return cards.size();
    }                                   // Number of cards in hand

    /*
     * Calculate total for hand, Ace may be 1 or 11
     * Unique total
     */
    public List<Integer> getTotals() {
        int sum = 0;
        int aceCount = 0;

        for (Card card : cards) {
            sum += card.getValue();
            if (card.getValue() == 11) {
                aceCount++;
            }
        }

        // Adjust for Aces, also preserve insertion order for sorting
        Set<Integer> totals = new LinkedHashSet<>();
        int current = sum;
        for (int i = 0; i <= aceCount; i++) {
            totals.add(current);
            current -= 10; // reduce ace from 11 to 1 prevent busting
        }
        List<Integer> sorted = new ArrayList<>(totals);
        Collections.sort(sorted);
        return sorted;
    }

    // Return best total <= 21 or lowest total if all > 21 (bust lol)
    public int getBestTotal() {
        int best = Integer.MIN_VALUE;
        for (int total : getTotals()) {
            if (total <= 21) best = Math.max(best, total);
        }
        return best == Integer.MIN_VALUE ? Collections.min(getTotals()) : best;
    }

    // All totals over 21 = bust
    public boolean isBust() {
        return getTotals().get(0) > 21;
    }

    // Natural blackjack = 10 value + Ace card (11) = 21
    public boolean isBlackjack() {
        return size() == 2 && getBestTotal() == 21;
    }

    // Soft total: true if hand has an Ace that counts as 11 WITHOUT busting
    public boolean isSoft() {
        if (!containsAce()) return false;
        int hard = getHardTotal();

        // if any total <= 21 equals (hard total + 10), it's soft
        for (int total : getTotals()) {
            if (total <= 21 && total == hard + 10) return true;
        }
        return false;
    }

    private boolean containsAce() {
        return cards.stream().anyMatch(card -> card.getRank() == Rank.ACE);
    }

    // count Ace as 1 in a hard hand
    private int getHardTotal() {
        int sum = 0;
        for (Card card : cards) {
            sum += (card.getValue() == 11) ? 1 : card.getValue();
        }
        return sum;
    }

    // Separate hands using - (ie AH-TC-7S)
    public String toChars() {
        return String.join("-", cards.stream()
                .map(Card::toString)
                .toArray(String[]::new));
    }

    // Parse a hand using the str produced in toChars(), empty is a blank, Remember str is a denote for the card hand you have
    public static Hand fromChars(String str) {
        Hand hand = new Hand();
        if (str == null || str.isBlank()) return hand;
        for (String part : str.split("-")) {
            if (!part.isBlank()) hand.add(Card.fromChars(part.trim()));
        }
        return hand;
    }

    @Override
    public String toString() {
        return cards.toString(); // Solely for debugging: [AH, TC, 7S]
    }


}
