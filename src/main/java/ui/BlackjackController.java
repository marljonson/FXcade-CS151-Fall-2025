package ui;

import blackjack.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlackjackController {

    private static final int STARTING_MONEY = 1000;
    private static final int DEFAULT_BET = 50;

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

    // Called from Main.java
    public void init(String username) {
        this.username = username != null && !username.isBlank() ? username : "Player";
        this.game = new BlackjackGame(this.username);
        game.getHuman().setBankroll(STARTING_MONEY);

        // Update bankroll labels
        bankrollLabel.setText("Bankroll: $" + STARTING_MONEY);
        bot1BankLabel.setText("Bot 1: $" + STARTING_MONEY);
        bot2BankLabel.setText("Bot 2: $" + STARTING_MONEY);

        // Start first round automatically
        onNewRound();
        refresh(); // draws the cards on screen; enables/disables buttons
    }

    @FXML
    private void onHit() {
        if (!game.isHumansTurn()) return;
        game.humanHit();
        refresh();
        if (game.getHuman().getHand().getBestTotal() >= 21) {
            playBotsAndDealer();
        }
    }

    @FXML
    private void onStand() {
        if (!game.isHumansTurn()) return;
        playBotsAndDealer();
    }

    @FXML
    private void onNewRound() {
        int bet = DEFAULT_BET;
        try {
            bet = Integer.parseInt(betField.getText().trim());
            if (bet < 1 || bet > game.getHuman().getBankroll()) bet = DEFAULT_BET;
        } catch (Exception ignored) {}

        game.startNewRound(bet, DEFAULT_BET, DEFAULT_BET);
        refresh();
        statusLabel.setText("Your turn!");
        turnLabel.setText("YOUR TURN");
    }

    private void playBotsAndDealer() {
        new Thread(() -> {
            try { Thread.sleep(600); } catch (Exception ignored) {}
            javafx.application.Platform.runLater(this::refresh);

            // Bots play
            while (game.getTurnIndex() < 3 && !game.isRoundOver()) {
                Participant p = game.getPlayers().get(game.getTurnIndex());
                BotStrategy brain = game.getBot1() == p ? BotStrategy.hitUnder(16)
                                   : game.getBot2() == p ? BotStrategy.hitUnder(15) : null;

                while (brain != null && !p.getHand().isBust() &&
                       brain.decide(p.getHand(), game.dealerUpCard()) == BotStrategy.Action.HIT) {
                    game.tryDealUp(p.getHand());
                    try { Thread.sleep(800); } catch (Exception ignored) {}
                    javafx.application.Platform.runLater(this::refresh);
                }
                game.turnIndex++;
                javafx.application.Platform.runLater(this::refresh);
            }

            // Dealer plays
            if (!game.isRoundOver()) {
                game.revealDealerHole();
                javafx.application.Platform.runLater(this::refresh);
                try { Thread.sleep(1000); } catch (Exception ignored) {}
                game.dealerTurn();
                game.finishRound();
                javafx.application.Platform.runLater(() -> {
                    refresh();
                    statusLabel.setText(game.getResultBanner().replace("\n", " • "));
                    turnLabel.setText("Round Over");
                });
            }
        }).start();
    }

    private void refresh() {
        renderHand(playerCards, game.getHuman().getHand());
        renderHand(bot1Cards, game.getBot1().getHand());
        renderHand(bot2Cards, game.getBot2().getHand());
        renderHand(dealerCards, game.getDealer().getHand());

        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1BankLabel.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2BankLabel.setText("Bot 2: $" + game.getBot2().getBankroll());

        playerTotalLabel.setText("You: " + game.getHuman().getHand().getBestTotal());

        if (game.isRoundOver() || !game.hasDealerHoleHidden()) {
            int total = game.getDealer().getHand().getBestTotal();
            dealerTotalLabel.setText(total > 21 ? "BUST" : String.valueOf(total));
        } else {
            dealerTotalLabel.setText("??");
        }

        boolean myTurn = game.isHumansTurn();
        hitButton.setDisable(!myTurn);
        standButton.setDisable(!myTurn);
        newRoundButton.setDisable(myTurn);
    }

    private void renderHand(HBox box, Hand hand) {
        box.getChildren().clear();
        for (Card card : hand.getCards()) {
            String filename = card.isFaceUp() ? filenameFor(card.getRank(), card.getSuit()) : "back.png";
            Image img = imageCache.computeIfAbsent(filename, f -> {
                var stream = getClass().getResourceAsStream(CARD_DIR + f);
                return stream != null ? new Image(stream) : null;
            });
            if (img == null) continue;

            ImageView iv = new ImageView(img);
            iv.setFitWidth(CARD_WIDTH);
            iv.setPreserveRatio(true);
            box.getChildren().add(iv);
        }
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

    private void bankrollLabelText(String text) {
        if (bankrollLabel != null) bankrollLabel.setText(text);
    }
}