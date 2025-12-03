package ui;

import blackjack.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/*
 * JavaFX controller for Blackjack screen
 * UI expects FXML with the fx:id's used below
 */
public class BlackjackController {
    private static final int DEFAULT_BET = 50;

    // wire these fx ids in blackjack.fxml
    @FXML
    private HBox dealerCards, playerCards, bot1Cards, bot2Cards;

    @FXML
    private Label statusLabel, bankrollLabel, playerTotalLabel, dealerTotalLabel;
    @FXML
    private Label bot1BankLabel, bot2BankLabel, turnLabel;


    @FXML
    private TextField betField;

    @FXML
    private Button hitButton, standButton, newRoundButton, saveButton, loadButton;


    private BlackjackGame game;
    private String username = "Player"; // we set it in Main/App using init

    // Images from resources/cards
    private static final String CARD_DIR = "/cards/";
    private static final double CARD_WIDTH = 90; // adjust later if needed (?)
    private final Map<String, Image> imageCache = new HashMap<>();

    // Call from scene loader in Main
    public void init(String username) {
        if (username != null && !username.isBlank()) this.username = username;
        this.game = new BlackjackGame(this.username);
        startRound();
    }

    // UI events: onHit, onStand, onNewRound, onSave, onLoad
    @FXML
    private void onHit() {
        game.humanHit();
        refresh();
        endIfOver();
    }

    @FXML
    private void onStand() {
        game.humanStand();
        refresh();
        endIfOver();
    }

    @FXML
    private void onNewRound() {
        startRound();
    }

    @FXML
    private void onSave() {
        try {
            String json = game.toJsonSave();
            Path path = savePath();
            Files.createDirectories(path.getParent());
            Files.writeString(path, json, StandardCharsets.UTF_8);
            statusLabel.setText("Saved to " + path.toString());
        } catch (Exception e) {
            statusLabel.setText("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void onLoad() {
        try {
            Path path = savePath();
            if (!Files.exists(path)) {
                statusLabel.setText("No save found for " + username);
                return;
            }
            String json = Files.readString(path, StandardCharsets.UTF_8);
            this.game = BlackjackGame.fromJsonSave(json, username);
            refresh();
            statusLabel.setText("Loaded from" + path.toString());
        } catch (Exception e) {
            statusLabel.setText("Load failed: " + e.getMessage());
        }
    }


    // Read bet, call startNewRound, refresh
    private void startRound() {
        int bet = parseBetOrDefault(DEFAULT_BET);
        game.startNewRound(bet, DEFAULT_BET, DEFAULT_BET); // bots bet 50 by default
        statusLabel.setText(""); // clear last round banner
        hitButton.setDisable(false);
        standButton.setDisable(false);
        newRoundButton.setDisable(true);
        refresh();
    }

    // Safely parse bet textfield and prevent negative input
    private int parseBetOrDefault(int fallback) {
        try {
            String s = (betField == null) ? null : betField.getText();
            int n = Integer.parseInt(betField.getText().trim());
            return Math.max(0, n);
        } catch (Exception e) {
            return fallback;
        }
    }

    // Allow player to end the game, implement high score submission later
    private void endIfOver() {
        if (game.isRoundOver()) {
            statusLabel.setText(game.getResultBanner());
            hitButton.setDisable(true);
            standButton.setDisable(true);
            newRoundButton.setDisable(false);
            // Possible TODO: submit high score here
        }
    }

    // Render everything based on backend
    private void refresh() {
        renderHand(playerCards, game.getHuman().getHand());
        renderHand(bot1Cards, game.getBot1().getHand());
        renderHand(bot2Cards, game.getBot2().getHand());
        renderHand(dealerCards, game.getDealer().getHand());

        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1BankLabel.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2BankLabel.setText("Bot 2: $" + game.getBot2().getBankroll());
        statusLabel.setText(game.getResultBanner());

        // totals 
        playerTotalLabel.setText(String.valueOf(game.getHuman().getHand().getBestTotal()));
        if (!game.isRoundOver() && hasHiddenDealerCard()) {
            dealerTotalLabel.setText("??"); // hide until dealer hole card revealed
        } else {
            dealerTotalLabel.setText(game.getDealer().getHand().isBust()
                    ? "BUST" : String.valueOf(game.getDealer().getHand().getBestTotal()));
        }


        // turn indicator
        String turnText = switch (game.getTurnIndex()) {
            case 0 -> "Your turn";
            case 1 -> "Bot 1's turn";
            case 2 -> "Bot 2's turn";
            default -> game.isRoundOver() ? "Round over" : "Dealer's turn";
        };

        turnLabel.setText(turnText);

        boolean isHumansTurn = !game.isRoundOver() && game.getTurnIndex() == 0;
        hitButton.setDisable(!isHumansTurn);
        standButton.setDisable(!isHumansTurn);
        newRoundButton.setDisable(isHumansTurn);
    }

    // Render hand into HBox
    private void renderHand(HBox box, Hand hand) {
        box.getChildren().clear();
        for (Card card : hand.getCards()) {
            box.getChildren().add(cardNode(card));
        }
    }

    private boolean hasHiddenDealerCard() {
        for (Card card : game.getDealer().getHand().getCards()) {
            if (!card.isFaceUp()) return true;
        }
        return false;
    }

    // Card to ImageView uses cache, show back.png if face-down
    private Node cardNode(Card card) {
        String file = card.isFaceUp() ? filenameFor(card.getRank(), card.getSuit()) : "back.png";
        Image image = loadImage(file);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(CARD_WIDTH);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    // Build file name like... 10_of_hearts.png or queen_of_spades.png
    private String filenameFor(Rank rank, Suit suit) {
        String r = switch (rank) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "jack";
            case QUEEN -> "queen";
            case KING -> "king";
            case ACE -> "ace";
        };
        String s = switch (suit) {
            case CLUBS -> "clubs";
            case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts";
            case SPADES -> "spades";
        };
        return r + "_of_" + s + ".png";
    }

    private Image loadImage(String file) {
        return imageCache.computeIfAbsent(file, f -> {
            var in = Objects.requireNonNull(
                    getClass().getResourceAsStream(CARD_DIR + f),
                    "Missing card image: " + CARD_DIR + f);
            return new Image(in);
        });
    }

    private Path savePath() {
        return Path.of("data", "saves_blackjack", username + ".json");
    }

}