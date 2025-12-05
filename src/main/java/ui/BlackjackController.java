package ui;

import blackjack.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlackjackController {

    private static final int STARTING_MONEY = 1000;
    private static final int DEFAULT_BET = 50;

    @FXML private VBox root;
    @FXML private HBox dealerCards, playerCards, bot1Cards, bot2Cards;
    @FXML private Label statusLabel, bankrollLabel, playerTotalLabel, dealerTotalLabel;
    @FXML private Label bot1BankLabel, bot2BankLabel, turnLabel;
    @FXML private TextField betField;
    @FXML private Button hitButton, standButton, newRoundButton, saveButton, loadButton;

    private BlackjackGame game;
    private String username;
    private final Map<String, Image> imageCache = new HashMap<>();
    private static final String CARD_DIR = "/cards/";
    private static final double CARD_WIDTH = 90;

    // Called from Main.java when launching Blackjack
    public void initializeGame(String username, Stage stage, Runnable backToMenu) {
        this.username = username;
        this.game = new BlackjackGame(username);
        game.getHuman().setBankroll(STARTING_MONEY);

        // Add toolbar like Snake does
        var toolbar = createToolbar(stage, backToMenu);
        root.getChildren().add(0, toolbar);

        refreshUI();
        updateTurnDisplay();
    }

    @FXML private void onHit() {
        if (!game.isHumansTurn()) return;
        game.humanHit();
        refreshUI();
        if (game.getHuman().getHand().getBestTotal() >= 21) {
            advanceToNextPlayer();
        }
    }

    @FXML private void onStand() {
        if (!game.isHumansTurn()) return;
        advanceToNextPlayer();
    }

    @FXML private void onNewRound() {
        int bet = parseBet();
        if (bet <= 0 || bet > game.getHuman().getBankroll()) {
            statusLabel.setText("Invalid bet! Max: $" + game.getHuman().getBankroll());
            return;
        }
        game.startNewRound(bet, DEFAULT_BET, DEFAULT_BET);
        refreshUI();
    }

    @FXML private void onSave() {
        String saveString = game.toJsonSave();
        TextInputDialog dialog = new TextInputDialog(saveString);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Copy this save code:");
        dialog.setContentText("Save Code:");
        dialog.getEditor().setEditable(false);
        dialog.getEditor().selectAll();
        dialog.showAndWait();
        statusLabel.setText("Game saved! Copied to clipboard.");
    }

    @FXML private void onLoad() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Paste your save code:");
        dialog.setContentText("Save Code:");
        dialog.showAndWait().ifPresent(code -> {
            try {
                this.game = BlackjackGame.fromJsonSave(code, username);
                statusLabel.setText("Game loaded successfully!");
                refreshUI();
            } catch (Exception e) {
                statusLabel.setText("Invalid save code!");
            }
        });
    }

    private void advanceToNextPlayer() {
        // Let the game engine handle bot turns and dealer
        new Thread(() -> {
            javafx.application.Platform.runLater(this::refreshUI);
            try { Thread.sleep(800); } catch (Exception ignored) {}

            while (!game.isRoundOver() && game.getTurnIndex() < 3) {
                Participant current = game.getPlayers().get(game.getTurnIndex());
                BotStrategy brain = game.getBot1() == current ? Strategies.hitUnder(16) :
                                  game.getBot2() == current ? Strategies.hitUnder(15) : null;

                while (!current.getHand().isBust() && brain != null &&
                       brain.decide(current.getHand(), game.getDealer().getHand().getCards().get(0)) == BotStrategy.Action.HIT) {
                    game.tryDealUp(current.getHand());
                    javafx.application.Platform.runLater(this::refreshUI);
                    Thread.sleep(1000);
                }
                game.turnIndex++;
                javafx.application.Platform.runLater(this::refreshUI);
                Thread.sleep(800);
            }

            if (!game.isRoundOver()) {
                game.revealDealerHole();
                javafx.application.Platform.runLater(this::refreshUI);
                Thread.sleep(1000);
                game.dealerTurn();
                game.finishRound();
                javafx.application.Platform.runLater(() -> {
                    refreshUI();
                    saveHighScoreIfNewBest();
                });
            }
        }).start();
    }

    private void refreshUI() {
        renderHand(playerCards, game.getHuman().getHand());
        renderHand(bot1Cards, game.getBot1().getHand());
        renderHand(bot2Cards, game.getBot2().getHand());
        renderHand(dealerCards, game.getDealer().getHand());

        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1BankLabel.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2BankLabel.setText("Bot 2: $" + game.getBot2().getBankroll());

        playerTotalLabel.setText("Total: " + game.getHuman().getHand().getBestTotal());
        int dealerTotal = game.getDealer().getHand().getBestTotal();
        dealerTotalLabel.setText(game.isRoundOver() || game.hasDealerHoleHidden() ? 
            (dealerTotal > 21 ? "BUST" : String.valueOf(dealerTotal)) : "??");

        statusLabel.setText(game.getResultBanner());

        updateTurnDisplay();

        boolean humanTurn = game.isHumansTurn();
        hitButton.setDisable(!humanTurn);
        standButton.setDisable(!humanTurn);
        newRoundButton.setDisable(humanTurn || !game.isRoundOver());
    }

    private void updateTurnDisplay() {
        String turn = switch (game.getTurnIndex()) {
            case 0 -> "YOUR TURN";
            case 1 -> "Bot 1's turn...";
            case 2 -> "Bot 2's turn...";
            default -> game.isRoundOver() ? "Round Over" : "Dealer's turn...";
        };
        turnLabel.setText(turn);
    }

    private int parseBet() {
        try {
            return Integer.parseInt(betField.getText().trim());
        } catch (Exception e) {
            return DEFAULT_BET;
        }
    }

    private void renderHand(HBox box, Hand hand) {
        box.getChildren().clear();
        for (Card card : hand.getCards()) {
            box.getChildren().add(cardImageView(card));
        }
    }

    private Node cardImageView(Card card) {
        String filename = card.isFaceUp() ? filenameFor(card.getRank(), card.getSuit()) : "back.png";
        Image img = imageCache.computeIfAbsent(filename, f -> new Image(
            Objects.requireNonNull(getClass().getResourceAsStream(CARD_DIR + f))));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(CARD_WIDTH);
        iv.setPreserveRatio(true);
        return iv;
    }

    private String filenameFor(Rank rank, Suit suit) {
        String r = switch (rank) {
            case ACE -> "ace";
            case KING -> "king";
            case QUEEN -> "queen";
            case JACK -> "jack";
            case TEN -> "10";
            default -> String.valueOf(rank.getValue());
        };
        String s = switch (suit) {
            case CLUBS -> "clubs";
            case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts";
            case SPADES -> "spades";
        };
        return r + "_of_" + s + ".png";
    }

    private void saveHighScoreIfNewBest() {
        int money = game.getHuman().getBankroll();
        if (money <= STARTING_MONEY) return;

        // Same format as Snake: username:blackjack:score1:score2...
        try {
            var path = java.nio.file.Paths.get("data/high_scores.txt");
            var lines = new java.util.ArrayList<>(java.nio.file.Files.readAllLines(path));
            String prefix = username + ":blackjack:";

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(prefix)) {
                    String[] parts = lines.get(i).split(":");
                    java.util.List<Integer> scores = new java.util.ArrayList<>();
                    for (int j = 2; j < parts.length; j++) {
                        scores.add(Integer.parseInt(parts[j]));
                    }
                    scores.add(money);
                    scores.sort(java.util.Comparator.reverseOrder());
                    while (scores.size() > 5) scores.remove(scores.size()-1);

                    StringBuilder sb = new StringBuilder(prefix);
                    for (int s : scores) sb.append(s).append(":");
                    lines.set(i, sb.substring(0, sb.length()-1));
                    java.nio.file.Files.write(path, lines);
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    private HBox createToolbar(Stage stage, Runnable backToMenu) {
        Button mainMenuBtn = new Button("Main Menu");
        mainMenuBtn.setOnAction(e -> backToMenu.run());
        HBox toolbar = new HBox(10, mainMenuBtn);
        toolbar.setStyle("-fx-background-color: #555555; -fx-padding: 10;");
        toolbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return toolbar;
    }
}