package ui;

import blackjack.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;

public class BlackjackController {

    @FXML private HBox dealerCards, playerCards, bot1Cards, bot2Cards;
    @FXML private Label statusLabel, bankrollLabel, playerTotalLabel, dealerTotalLabel;
    @FXML private Label turnLabel;
    @FXML private TextField betField;
    @FXML private Button hitButton, standButton, newRoundButton;

    private BlackjackGame game;

    public void init(String username) {
        game = new BlackjackGame(username != null && !username.isBlank() ? username : "Player");
        game.getHuman().setBankroll(1000);

        bankrollLabel.setText("Bankroll: $1000");

        newRound(); // start first round right away
    }

    @FXML
    private void newRound() {
        int bet = 50;
        try {
            String txt = betField.getText().trim();
            if (!txt.isEmpty()) {
                bet = Integer.parseInt(txt);
                if (bet < 1 || bet > game.getHuman().getBankroll()) {
                    bet = Math.min(50, game.getHuman().getBankroll());
                }
            }
        } catch (Exception ignored) {
            bet = 50;
        }
        betField.setText(String.valueOf(bet));

        game.startNewRound(bet, 50, 50);
        refresh();

        statusLabel.setText("Your turn!");
        turnLabel.setText("YOUR TURN");
        hitButton.setDisable(false);
        standButton.setDisable(false);
        newRoundButton.setDisable(true);
    }

    @FXML
    private void hit() {
        game.humanHit();
        refresh();
        if (game.getHuman().getHand().getBestTotal() >= 21) {
            endTurn();
        }
    }

    @FXML
    private void stand() {
        endTurn();
    }

    private void endTurn() {
        hitButton.setDisable(true);
        standButton.setDisable(true);

        new Thread(() -> {
            // bots play
            while (game.getTurnIndex() < 3 && !game.isRoundOver()) {
                Participant p = game.getPlayers().get(game.getTurnIndex());
                BotStrategy brain = p == game.getBot1() ? BotStrategy.hitUnder(16)
                                  : p == game.getBot2() ? BotStrategy.hitUnder(15) : null;

                while (brain != null && !p.getHand().isBust() &&
                       brain.decide(p.getHand(), game.dealerUpCard()) == BotStrategy.Action.HIT) {
                    game.tryDealUp(p.getHand());
                    sleep(800);
                    runLater(this::refresh);
                }
                game.turnIndex++;
            }

            // dealer plays
            if (!game.isRoundOver()) {
                game.revealDealerHole();
                runLater(this::refresh);
                sleep(1000);
                game.dealerTurn();
                game.finishRound();
                runLater(() -> {
                    refresh();
                    statusLabel.setText(game.getResultBanner().replace("\n", " • "));
                    newRoundButton.setDisable(false);
                });
            }
        }).start();
    }

    private void refresh() {
        render(dealerCards, game.getDealer().getHand());
        render(playerCards, game.getHuman().getHand());
        render(bot1Cards, game.getBot1().getHand());
        render(bot2Cards, game.getBot2().getHand());

        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        playerTotalLabel.setText("You: " + game.getHuman().getHand().getBestTotal());

        int dealerTotal = game.getDealer().getHand().getBestTotal();
        dealerTotalLabel.setText((game.isRoundOver() || !game.hasDealerHoleHidden())
                ? (dealerTotal > 21 ? "BUST" : String.valueOf(dealerTotal))
                : "??");

        boolean myTurn = game.isHumansTurn();
        hitButton.setDisable(!myTurn);
        standButton.setDisable(!myTurn);
        newRoundButton.setDisable(myTurn || !game.isRoundOver());
    }

    private void render(HBox box, Hand hand) {
        box.getChildren().clear();
        for (Card c : hand.getCards()) {
            String name = c.isFaceUp() ? cardName(c.getRank(), c.getSuit()) : "back.png";
            Image img = new Image(getClass().getResourceAsStream("/cards/" + name));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(90);
            iv.setPreserveRatio(true);
            box.getChildren().add(iv);
        }
    }

    private String cardName(Rank r, Suit s) {
        String rank = switch (r) {
            case ACE -> "ace";
            case KING -> "king";
            case QUEEN -> "queen";
            case JACK -> "jack";
            case TEN -> "10";
            default -> String.valueOf(r.getValue());
        };
        String suit = switch (s) {
            case CLUBS -> "clubs";
            case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts";
            case SPADES -> "spades";
        };
        return rank + "_of_" + suit + ".png";
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }
    private void runLater(Runnable r) {
        javafx.application.Platform.runLater(r);
    }
}