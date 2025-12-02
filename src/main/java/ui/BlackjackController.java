// ui/BlackjackController.java
package ui;

import main.Main;
import blackjack.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BlackjackController {
    private static final int DEFAULT_BET = 50;

    // ALL @FXML fields MUST be package-private or public
    @FXML HBox dealerCards, playerCards, bot1Cards, bot2Cards;
    @FXML Label statusLabel, bankrollLabel, playerTotalLabel, dealerTotalLabel;
    @FXML Label bot1BankLabel, bot2BankLabel, turnLabel;
    @FXML TextField betField;
    @FXML Button hitButton, standButton, newRoundButton, saveButton, loadButton;

    private BlackjackGame game;
    private String username = "Player";

    private static final String CARD_DIR = "/cards/";
    private static final double CARD_WIDTH = 90;
    private final Map<String, Image> imageCache = new HashMap<>();

    // Called from Main when loading FXML
    public void init(String username){
        if (username != null && !username.isBlank()) this.username = username;
        this.game = new BlackjackGame(this.username);
        startRound();
    }

    // ALL @FXML methods MUST be package-private or public
    @FXML
    void onHit(){
        game.humanHit();
        refresh();
        endIfOver();
    }

    @FXML
    void onStand(){
        game.humanStand();
        refresh();
        endIfOver();
    }

    @FXML
    void onNewRound(){
        startRound();
    }

    @FXML
    void onSave(){
        try {
            String json = game.toJsonSave();
            Path path = savePath();
            Files.createDirectories(path.getParent());
            Files.writeString(path, json);
            statusLabel.setText("Saved to " + path.getFileName());
        } catch (Exception e) {
            statusLabel.setText("Save failed: " + e.getMessage());
        }
    }

    @FXML
    public void onLoad(){
        try {
            Path path = savePath();
            if(!Files.exists(path)){
                statusLabel.setText("No save found");
                return;
            }
            String json = Files.readString(path);
            this.game = BlackjackGame.fromJsonSave(json, username);
            refresh();
            statusLabel.setText("Loaded successfully");
        } catch (Exception e) {
            statusLabel.setText("Load failed: " + e.getMessage());
        }
    }

    private void startRound(){
        int bet = parseBetOrDefault(DEFAULT_BET);
        game.startNewRound(bet, DEFAULT_BET, DEFAULT_BET);
        statusLabel.setText("New round started!");
        hitButton.setDisable(false);
        standButton.setDisable(false);
        newRoundButton.setDisable(true);
        refresh();
    }

    private int parseBetOrDefault(int fallback){
        try {
            String s = betField.getText();
            if (s == null || s.isBlank()) return fallback;
            int n = Integer.parseInt(s.trim());
            return Math.max(1, n);
        } catch (Exception e) {
            return fallback;
        }
    }

    private void endIfOver(){
        if (game.isRoundOver()) {
            statusLabel.setText(game.getResultBanner());
            hitButton.setDisable(true);
            standButton.setDisable(true);
            newRoundButton.setDisable(false);

            // New part
            int playerBankroll = game.getHuman().getBankroll();
            Main.updateHighScoreIfNeeded("Blackjack", username, game.getHuman().getBankroll());
        }
    }

    private void refresh(){
        renderHand(playerCards, game.getHuman().getHand());
        renderHand(bot1Cards, game.getBot1().getHand());
        renderHand(bot2Cards, game.getBot2().getHand());
        renderHand(dealerCards, game.getDealer().getHand());

        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1BankLabel.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2BankLabel.setText("Bot 2: $" + game.getBot2().getBankroll());

        playerTotalLabel.setText("You: " + game.getHuman().getHand().getBestTotal());
        dealerTotalLabel.setText(game.isRoundOver() || !hasHiddenDealerCard() 
            ? "Dealer: " + (game.getDealer().getHand().isBust() ? "BUST" : game.getDealer().getHand().getBestTotal())
            : "Dealer: ??");

        turnLabel.setText(switch (game.getTurnIndex()) {
            case 0 -> "Your turn";
            case 1 -> "Bot 1's turn";
            case 2 -> "Bot 2's turn";
            default -> game.isRoundOver() ? "Round over" : "Dealer's turn";
        });

        boolean isHumansTurn = !game.isRoundOver() && game.getTurnIndex() == 0;
        hitButton.setDisable(!isHumansTurn);
        standButton.setDisable(!isHumansTurn);
    }

    private void renderHand(HBox box, Hand hand){
        box.getChildren().clear();
        for (Card card : hand.getCards()){
            box.getChildren().add(cardNode(card));
        }
    }

    private boolean hasHiddenDealerCard(){
        var cards = game.getDealer().getHand().getCards();
        return cards.size() >= 2 && !cards.get(1).isFaceUp();
    }

    private Node cardNode(Card card){
        String file = card.isFaceUp() ? filenameFor(card.getRank(), card.getSuit()) : "back.png";
        Image image = imageCache.computeIfAbsent(file, f -> {
            var url = getClass().getResource(CARD_DIR + f);
            if (url == null) throw new IllegalStateException("Missing card: " + f);
            return new Image(url.toExternalForm());
        });
        ImageView iv = new ImageView(image);
        iv.setFitWidth(CARD_WIDTH);
        iv.setPreserveRatio(true);
        return iv;
    }

    private String filenameFor(Rank rank, Suit suit) {
        String r = switch (rank) {
            case TWO -> "2"; case THREE -> "3"; case FOUR -> "4"; case FIVE -> "5";
            case SIX -> "6"; case SEVEN -> "7"; case EIGHT -> "8"; case NINE -> "9";
            case TEN -> "10"; case JACK -> "jack"; case QUEEN -> "queen";
            case KING -> "king"; case ACE -> "ace";
        };
        String s = switch (suit) {
            case CLUBS -> "clubs"; case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts"; case SPADES -> "spades";
        };
        return r + "_of_" + s + ".png";
    }

    private Path savePath() {
        return Path.of("data", "saves_blackjack", username + ".json");
    }

    // Add this method to fix buttons after loading a save
    public void refreshAfterLoad() {
        refresh();
        endIfOver();
    }
}