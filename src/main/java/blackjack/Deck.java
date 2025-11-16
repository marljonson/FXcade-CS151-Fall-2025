package main.java.blackjack;

// we use secure random as it is more unpredictable in shuffling
import java.security.SecureRandom;
import java.util.*;


/**
 * One (or more) 52-card decks with shuffle and deal helpers.
 * Immutable Card objects
 * deal() returns face-up cards
 * dealFaceDown() returns the same rank/suit but face-down
 */
public class Deck {
    private static final int CARDS_PER_DECK = 52;

    private final Deque<Card> cards = new ArrayDeque<>();
    private final int numDecks;
    private final Random rng;
    

    // No arg constructor, 1 standard deck of 52 cards
    public Deck() {
        this(1);
    }

    // Specify how many decks you want
    public Deck(int numDecks) {
        this(numDecks, new SecureRandom());
    }

    // Master constructor, #1 and #2 depend on this for input validatio
    public Deck(int numDecks, Random rng) {
        if(numDecks <= 0) throw new IllegalArgumentException("numDecks must be more than or equal to 1!");
        this.numDecks = numDecks;
        this.rng = Objects.requireNonNull(rng);
        resetAndShuffle();
    }

    // Rebuild dealer's shoe and shuffles cards
    public final void resetAndShuffle(){
        List<Card> list = new ArrayList<>(CARDS_PER_DECK * numDecks);
        for(int i = 0; i < numDecks; i++){
            for(Suit suit : Suit.values()){
                for(Rank rank : Rank.values()){
                    list.add(new Card(rank, suit));
                }
            }
        }
        Collections.shuffle(list, rng);
        cards.clear();
        for (Card card : list) cards.addLast(card);
    }

    // Cards remaining in the table
    public int remaining(){ return cards.size(); }
    public boolean isEmpty(){ return cards.isEmpty(); }

    // Deal the next card face up
    public Card deal(){
        if (cards.isEmpty()) throw new NoSuchElementException("Deck is empty");
        return cards.removeFirst();
    }



    

    

    
    
    
}
