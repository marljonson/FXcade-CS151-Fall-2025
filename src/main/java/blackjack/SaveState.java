package blackjack;

import java.util.List;

class SaveState {
    int version = 1;

    String deck; // Deck.toChars()
    String humanHand;
    String bot1Hand;
    String bot2Hand;
    String dealerHand;

    boolean hideHole; // true if dealer's 2nd card face down

    List<Integer> banks;    // [human, bot1, bot2, dealer]
    List<Integer> bets;    // [human, bot1, bot2]

    int turn;              // 0 to 3
    boolean over;          // roundOver

}
