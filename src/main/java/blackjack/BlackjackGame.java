package main.java.blackjack;

import java.util.*;


public class BlackjackGame {
    // related to soft total added to 17 when ace is 11
    private static final boolean DEALER_HITS_SOFT_17 = true;

    private final Deck deck;
    private final List<Participant> players = new ArrayList<>(3); // 1 human, 2 bot players
    private final Participant dealer;
    
    private final Map<Participant,BotStrategy> botBrains = new HashMap<>();
    private int turnIndex = 0;
    private boolean roundOver = false;
    private String resultBanner = "";

    public BlackjackGame(String humanName){
        this(new Deck(), humanName);
    }

    public BlackjackGame(Deck deck, String humanName){
        this.deck = deck;
        players.add(new Participant(humanName, true, false));
        Participant bot1 = new Participant("Bot-1", false, false);
        Participant bot2 = new Participant("Bot-2", false, false);
        players.add(bot1);
        players.add(bot2);

        // each bot has a different 'personality'
        botBrains.put(bot1, Strategies.hitUnder(16)); // hit under 16
        botBrains.put(bot2, Strategies.hitUnder(15)); // tighter than bot 1 threshold

        dealer = new Participant("Dealer", false, true);
    }

    // Round life cycle, each player makes a bet
}