package blackjack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;

public class BlackjackGame {
    // related to soft total added to 17 when ace is 11
    private static final boolean DEALER_HITS_SOFT_17 = true;
    private static final double WIN_MULTIPLIER = 1.5;

    private final Deck deck;
    private final List<Participant> players = new ArrayList<>(3); // 1 human, 2 bot players
    private final Participant dealer;

    private final Map<Participant, BotStrategy> botBrains = new HashMap<>();
    public int turnIndex = 0;
    private boolean roundOver = false;
    private String resultBanner = "";

    public BlackjackGame(String humanName) {
        this(new Deck(), humanName);
    }

    public BlackjackGame(Deck deck, String humanName) {
        this.deck = deck;
        players.add(new Participant(humanName, true, false));
        Participant bot1 = new Participant("Bot-1", false, false);
        Participant bot2 = new Participant("Bot-2", false, false);
        players.add(bot1);
        players.add(bot2);

        // each bot has a different 'personality'
        botBrains.put(bot1, BotStrategy.hitUnder(16));
        botBrains.put(bot2, BotStrategy.hitUnder(15));

        dealer = new Participant("Dealer", false, true);
    }

    // for new game
    public void resetForNewGame() {
        for (Participant participant : players) {
            participant.resetForNewGame();
        }
        dealer.resetForNewGame();
        roundOver = false;
        resultBanner = "";
        turnIndex = 0;
    }


    // Round life cycle, each player makes a bet
    public void startNewRound(int humanBet, int bot1Bet, int bot2Bet) {
        deck.resetAndShuffle();
        resultBanner = "";
        roundOver = false;
        turnIndex = 0;

        // clear and set bets
        for (Participant participant : players) {
            participant.clearForNextRound();
        }
        dealer.clearForNextRound();
        players.get(0).setBet(clampNonNegative(humanBet));
        players.get(1).setBet(clampNonNegative(bot1Bet));
        players.get(2).setBet(clampNonNegative(bot2Bet));

        // initial deal, 2 to everyone, dealer's second is face down
        // Human action when it's player's turn while round ongoing
        // Deal a single face-down card into dealer's hand
        for (int i = 0; i < 2; i++) {
            for (Participant player : players) {
                if (!tryDealUp(player.getHand())) return;
            }
            if (i == 0) {
                if (!tryDealUp(dealer.getHand())) return;
            } else {
                if (!tryDealFaceDownToDealer()) return;
            }
        }

        // natural blackjack shortcut (rare edge case with this many players)
        boolean anyNatural = players.stream().anyMatch(p -> p.getHand().isBlackjack())
                || dealer.getHand().isBlackjack();
        if (anyNatural) {
            revealDealerHole();
            finishRound();
            return;
        }
    }

    // Human action when it'saveState player'saveState turn while round ongoing
    public void humanHit() {
        if (roundOver || turnIndex != 0) return;
        if (!tryDealUp(players.get(0).getHand())) return;
        int best = players.get(0).getHand().getBestTotal();
        if (best >= 21) { // 21 or bust
            advanceToNextTurn();
        }
    }

    public void humanStand() {
        if (roundOver || turnIndex != 0) return;
        advanceToNextTurn();
    }

    private void advanceToNextTurn() {
        turnIndex++;
        // play bots
        while (!roundOver && turnIndex < players.size()) {
            Participant bot = players.get(turnIndex);
            playBotTurn(bot);
            turnIndex++;
        }
        // dealer turn
        if (!roundOver) {
            revealDealerHole();
            dealerTurn();
            finishRound();
        }
    }

    private void playBotTurn(Participant bot) {
        BotStrategy brain = botBrains.get(bot);
        if (brain == null) return;
        while (true) {
            if (bot.getHand().isBust()) break;
            BotStrategy.Action action = brain.decide(bot.getHand(), dealerUpCard());
            if (action == BotStrategy.Action.HIT) {
                if (!tryDealUp(bot.getHand())) return; // stops on empty deck
            } else {
                break;
            }
        }
    }

    public Card dealerUpCard() {
        // first card is up
        List<Card> dealerCards = dealer.getHand().getCards();
        if (dealerCards.isEmpty()) throw new IllegalStateException("Dealer has no cards yet");
        return dealerCards.get(0);
    }

    public void revealDealerHole() {
        // reveal (flip) the first face-down dealer card
        Hand src = dealer.getHand();
        Hand temp = new Hand();
        boolean flipped = false;
        for (Card card : src.getCards()) {
            if (!flipped && !card.isFaceUp()) {
                temp.add(card.flipped());
                flipped = true;
            } else {
                temp.add(card);
            }
        }
        src.clear();
        for (Card card : temp.getCards()) src.add(card);

    }

    public void dealerTurn() {
        Hand hand = dealer.getHand();
        while (true) {
            int best = hand.getBestTotal();
            boolean soft = hand.isSoft();
            if (best > 21) break;
            if (best > 17) break;
            if (best < 17) {
                if (!tryDealUp(hand)) break;
                continue;
            }
            if (best == 17 && DEALER_HITS_SOFT_17 && soft) {
                if (!tryDealUp(hand)) break;
                continue;
            }
            break;
        }
    }

    public void finishRound() {
        roundOver = true;

        // Simple banner summary with stringbuilder
        StringBuilder sb = new StringBuilder();
        int dealerBest = dealer.getHand().getBestTotal();
        boolean dealerBust = dealer.getHand().isBust();

        for (Participant player : players) {
            int bet = player.getBet();
            int playerBest = player.getHand().getBestTotal();
            boolean playerBust = player.getHand().isBust();

            if (player.getHand().isBlackjack() && !dealer.getHand().isBlackjack()) {
                player.win((int) Math.round(bet * WIN_MULTIPLIER));
                sb.append(player.getName()).append(": Blackjack! +").append((int) Math.round(bet * WIN_MULTIPLIER)).append("\n");
            } else if (dealer.getHand().isBlackjack() && !player.getHand().isBlackjack()) {
                player.lose(bet);
                sb.append(player.getName()).append(": Dealer blackjack. -").append(bet).append("\n");
            } else if (playerBust) {
                player.lose(bet);
                sb.append(player.getName()).append(": Bust ").append(playerBest).append(". -").append(bet).append("\n");
            } else if (dealerBust) {
                player.win(bet);
                sb.append(player.getName()).append(": Dealer busts ").append(dealerBest).append(". +").append(bet).append("\n");
            } else {
                if (playerBest > dealerBest) {
                    player.win(bet);
                    sb.append(player.getName()).append(": ").append(playerBest).append(" > ").append(dealerBest).append(" +").append(bet).append("\n");
                } else if (playerBest < dealerBest) {
                    player.lose(bet);
                    sb.append(player.getName()).append(": ").append(playerBest).append(" < ").append(dealerBest).append(" -").append(bet).append("\n");
                } else { // tie, just follow logic as push has no operation
                    player.push();
                    sb.append(player.getName()).append(": Push ").append(playerBest).append("\n");
                }
            }
        }
        resultBanner = sb.toString().trim();
        if (getHuman().getBankroll() > 1000) {  // only save if profited
            saveHighScore(getHuman().getName(), getHuman().getBankroll());
        }
    }

    // These helpers encase deal() calls in a try-catch block for more error proofing
    // Deal one face-up card into a hand. If deck is empty, end the round gracefully. 
    public boolean tryDealUp(Hand hand) {
        try {
            hand.add(deck.deal());
            return true;
        } catch (NoSuchElementException ex) {
            resultBanner = "Deck is empty. Round ended.";
            roundOver = true;
            return false;
        }
    }

    // Deal a single face-down card into dealer'saveState hand. 
    private boolean tryDealFaceDownToDealer() {
        try {
            dealer.getHand().add(deck.dealFaceDown());
            return true;
        } catch (NoSuchElementException ex) {
            resultBanner = "Deck is empty. Round ended.";
            roundOver = true;
            return false;
        }
    }

    // Allow load from controller
    public static BlackjackGame fromJsonSave(String json, String humanName) {
        Gson gson = new Gson();
        SaveState saveState = gson.fromJson(json, SaveState.class);


        if (saveState.banks == null || saveState.banks.size() != 4)
            throw new IllegalArgumentException("Bad banks array");
        if (saveState.bets == null || saveState.bets.size() != 3) throw new IllegalArgumentException("Bad bets array");


        Deck deck = Deck.fromChars(saveState.deck);
        BlackjackGame game = new BlackjackGame(deck, humanName);

        // Load Hands from save state
        replaceHand(game.getHuman().getHand(), Hand.fromChars(saveState.humanHand));
        replaceHand(game.getBot1().getHand(), Hand.fromChars(saveState.bot1Hand));
        replaceHand(game.getBot2().getHand(), Hand.fromChars(saveState.bot2Hand));
        replaceHand(game.getDealer().getHand(), Hand.fromChars(saveState.dealerHand));

        // Hole visibility
        if (saveState.hideHole) {
            makeDealerSecondCardFaceDown(game.getDealer().getHand());
        }

        // Banks and bets
        game.getHuman().setBankroll(saveState.banks.get(0));
        game.getBot1().setBankroll(saveState.banks.get(1));
        game.getBot2().setBankroll(saveState.banks.get(2));
        game.getDealer().setBankroll(saveState.banks.get(3));

        game.getHuman().setBet(saveState.bets.get(0));
        game.getBot1().setBet(saveState.bets.get(1));
        game.getBot2().setBet(saveState.bets.get(2));

        game.turnIndex = saveState.turn;
        game.roundOver = saveState.over;
        game.resultBanner = ""; // Fresh blank slate

        return game;
    }

    // Allows Save of current game to resume later
    public String toJsonSave() {
        SaveState saveState = new SaveState();

        saveState.deck = deck.toChars();
        saveState.humanHand = getHuman().getHand().toChars();
        saveState.bot1Hand = getBot1().getHand().toChars();
        saveState.bot2Hand = getBot2().getHand().toChars();
        saveState.dealerHand = getDealer().getHand().toChars();

        saveState.hideHole = hasDealerHoleHidden();   // reuse helper

        saveState.banks = List.of(
                getHuman().getBankroll(),
                getBot1().getBankroll(),
                getBot2().getBankroll(),
                getDealer().getBankroll()
        );
        saveState.bets = List.of(
                getHuman().getBet(),
                getBot1().getBet(),
                getBot2().getBet()
        );

        saveState.turn = getTurnIndex();
        saveState.over = isRoundOver();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(saveState);
    }

    // Small helpers for Json save state
    private static void replaceHand(Hand target, Hand src) {
        target.clear();
        for (Card card : src.getCards()) target.add(card);
    }

    private static void makeDealerSecondCardFaceDown(Hand dealerHand) {
        var list = new ArrayList<>(dealerHand.getCards());
        if (list.size() >= 2 && list.get(1).isFaceUp()) {
            list.set(1, list.get(1).flipped());
            dealerHand.clear();
            for (Card card : list) dealerHand.add(card);
        }

    }

    // True if the dealer currently has a hidden (face-down) hole card.
    public boolean hasDealerHoleHidden() {
        List<Card> dealerCards = dealer.getHand().getCards();
        // If the dealer has at least two cards and the second one is face-down
        return dealerCards.size() >= 2 && !dealerCards.get(1).isFaceUp();
    }

    // method needed to save high score for new saving system
    public static void saveHighScore(String username, int score) {
        Path path = Paths.get("data", "high_scores.txt");
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try {
            if (Files.exists(path)) lines = Files.readAllLines(path);
            List<String> newLines = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(username + ":blackjack:")) {
                    String[] parts = line.split(":");
                    List<Integer> scores = new ArrayList<>();
                    for (int i = 2; i < parts.length; i++) {
                        try { scores.add(Integer.parseInt(parts[i])); } catch (Exception ignored) {}
                    }
                    scores.add(0, score);
                    scores.sort(Collections.reverseOrder());
                    while (scores.size() > 5) scores.remove(scores.size() - 1);

                    StringBuilder sb = new StringBuilder(username + ":blackjack");
                    for (int s : scores) sb.append(":").append(s);
                    newLines.add(sb.toString());
                    found = true;
                } else {
                    newLines.add(line);
                }
            }

            if (!found) {
                newLines.add(username + ":blackjack:" + score + ":0:0:0:0");
            }

            Files.write(path, newLines);
        } catch (IOException e) {
            System.out.println("Failed to save blackjack score: " + e);
        }
    }

    // UI getters
    public Participant getHuman() {
        return players.get(0);
    }

    public Participant getBot1() {
        return players.get(1);
    }

    public Participant getBot2() {
        return players.get(2);
    }

    public Participant getDealer() {
        return dealer;
    }

    public List<Participant> getPlayers() {
        return Collections.unmodifiableList(players);
    }


    public boolean isHumansTurn() {
        return !roundOver && turnIndex == 0;
    }

    private static int clampNonNegative(int betAmount) {
        return Math.max(0, betAmount);
    } // validate bet

    public boolean isRoundOver() {
        return roundOver;
    }

    public String getResultBanner() {
        return resultBanner;
    }

    public int getTurnIndex() {
        return turnIndex;
    } // 0 = human, 1 = bot1, 2 = bot2, 3 = dealer/done
}