package ui;

import blackjack.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class BlackjackController implements Initializable {

    private static final int STARTING_MONEY = 1000;
    private static final int DEFAULT_BET = 50;

    @FXML private VBox root;
    @FXML private HBox dealerCards, playerCards, bot1Cards, bot2Cards;
    @FXML private Label statusLabel, bankrollLabel, playerTotalLabel, dealerTotalLabel;
    @FXML private Label bot1BankLabel, bot2BankLabel, turnLabel;
    @FXML private TextField betField;
    @FXML private Button hitButton, standButton, newRoundButton, saveButton, loadButton;

    private BlackjackGame game;
    private String username = "Player";
    private final Map<String, Image> imageCache = new HashMap<>();
    private static final String CARD_DIR = "/cards/";
    private static final double CARD_WIDTH = 90;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // FXML loaded — disable buttons until game starts
        hitButton.setDisable(true);
        standButton.setDisable(true);
        newRoundButton.setDisable(false);
        betField.setText(String.valueOf(DEFAULT_BET));
        statusLabel.setText("Click New Round to start!");
        turnLabel.setText("Ready to play");
    }

    // Called from Main.java
    public void init(String username) {
        this.username = username != null && !username.isBlank() ? username : "Player";
        this.game = new BlackjackGame(this.username);
        game.getHuman().setBankroll(STARTING_MONEY);
        bankrollLabel.setText("Bankroll: $" + STARTING_MONEY);
        bot1BankLabel.setText("Bot 1: $" + STARTING_MONEY);
        bot2BankLabel.setText("Bot 2: $" + STARTING_MONEY);

        // Start first round
        onNewRound();
    }

    @FXML private void onHit() {
        if (game == null || !game.isHumansTurn()) return;
        game.humanHit();
        refreshUI();
        if (game.getHuman().getHand().getBestTotal() >= 21) {
            advanceToNextPlayer();
        }
    }

    @FXML private void onStand() {
        if (game == null || !game.isHumansTurn()) return;
        advanceToNextPlayer();
    }

    @FXML private void onNewRound() {
        if (game == null) return;
        int bet = parseBet();
        if (bet <= 0 || bet > game.getHuman().getBankroll()) {
            statusLabel.setText("Invalid bet! Max: $" + game.getHuman().getBankroll());
            return;
        }
        game.startNewRound(bet, DEFAULT_BET, DEFAULT_BET);
        refreshUI();
        statusLabel.setText("New round started! Your turn.");
    }

    @FXML private void onSave() {
        if (game == null) return;
        String saveString = game.toJsonSave();
        TextInputDialog dialog = new TextInputDialog(saveString);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Copy this save code:");
        dialog.getEditor().setEditable(false);
        dialog.getEditor().selectAll();
        dialog.showAndWait();
        statusLabel.setText("Game saved!");
    }

    @FXML private void onLoad() {
        if (game == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Paste your save code:");
        dialog.showAndWait().ifPresent(code -> {
            try {
                this.game = BlackjackGame.fromJsonSave(code, username);
                refreshUI();
                statusLabel.setText("Loaded!");
            } catch (Exception e) {
                statusLabel.setText("Invalid save!");
            }
        });
    }

    private void advanceToNextPlayer() {
        if (game == null) return;
        new Thread(() -> {
            try {
                Thread.sleep(800);
                javafx.application.Platform.runLater(this::refreshUI);

                while (!game.isRoundOver() && game.getTurnIndex() < 3) {
                    Participant current = game.getPlayers().get(game.getTurnIndex());
                    BotStrategy brain = game.getBot1() == current ? BotStrategy.hitUnder(16) :
                                        game.getBot2() == current ? BotStrategy.hitUnder(15) : null;

                    while (!current.getHand().isBust() && brain != null &&
                           brain.decide(current.getHand(), game.dealerUpCard()) == BotStrategy.Action.HIT) {
                        game.tryDealUp(current.getHand());
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(this::refreshUI);
                    }
                    game.turnIndex++;
                    Thread.sleep(800);
                    javafx.application.Platform.runLater(this::refreshUI);
                }

                if (!game.isRoundOver()) {
                    game.revealDealerHole();
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::refreshUI);
                    game.dealerTurn();
                    game.finishRound();
                    javafx.application.Platform.runLater(() -> {
                        refreshUI();
                        statusLabel.setText(game.getResultBanner());
                        newRoundButton.setDisable(false);
                    });
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void refreshUI() {
        if (game == null) return;

        // Clear and render hands
        renderHand(playerCards, game.getHuman().getHand());
        renderHand(bot1Cards, game.getBot1().getHand());
        renderHand(bot2Cards, game.getBot2().getHand());
        renderHand(dealerCards, game.getDealer().getHand());

        // Update labels
        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1BankLabel.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2BankLabel.setText("Bot 2: $" + game.getBot2().getBankroll());
        playerTotalLabel.setText("Total: " + game.getHuman().getHand().getBestTotal());

        int dealerTotal = game.getDealer().getHand().getBestTotal();
        if (game.isRoundOver() || !game.hasDealerHoleHidden()) {
            dealerTotalLabel.setText(dealerTotal > 21 ? "BUST" : String.valueOf(dealerTotal));
        } else {
            dealerTotalLabel.setText("??");
        }

        statusLabel.setText(game.getResultBanner().isEmpty() ? "Your turn" : game.getResultBanner());

        // Update turn
        updateTurnDisplay();

        // Button states
        boolean humanTurn = game.isHumansTurn();
        hitButton.setDisable(!humanTurn);
        standButton.setDisable(!humanTurn);
        newRoundButton.setDisable(humanTurn || !game.isRoundOver());
    }

    private void updateTurnDisplay() {
        if (game == null) return;
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
        if (box == null || hand == null) return;
        box.getChildren().clear();
        for (Card card : hand.getCards()) {
            box.getChildren().add(cardImageView(card));
        }
    }

    private Node cardImageView(Card card) {
        String filename = card.isFaceUp() ? filenameFor(card.getRank(), card.getSuit()) : "back.png";
        Image img = imageCache.computeIfAbsent(filename, f -> {
            var stream = getClass().getResourceAsStream(CARD_DIR + f);
            if (stream == null) {
                System.err.println("Missing image: " + CARD_DIR + f);
                return null;
            }
            return new Image(stream);
        });
        if (img == null) return new Label("Missing: " + filename); // Debug fallback

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