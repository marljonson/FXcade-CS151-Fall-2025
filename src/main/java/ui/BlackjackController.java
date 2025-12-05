package ui;

import blackjack.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BlackjackController {

    private final Stage stage;
    private final Runnable backToMenu;
    private BlackjackGame game;

    private HBox dealerHandBox;
    private HBox playerHandBox, bot1HandBox, bot2HandBox;
    private Label playerTotal, dealerTotal;
    private Label playerBankroll, bot1Bankroll, bot2Bankroll;
    private Label statusLabel;
    private TextField betField;
    private Button newRoundButton;

    public BlackjackController(Stage stage, Runnable backToMenu) {
        this.stage = stage;
        this.backToMenu = backToMenu;
    }

    public void start(String username) {
        game = new BlackjackGame(username);
        game.getHuman().setBankroll(1000);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white;");
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("BLACKJACK");
        title.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-text-fill: #00ff88;");

        VBox dealerArea = new VBox(10);
        dealerArea.setAlignment(Pos.CENTER);
        dealerArea.getChildren().addAll(
            new Label("DEALER") {{ setStyle("-fx-font-size: 20; -fx-text-fill: #ff4444;"); }},
            dealerHandBox = new HBox(10),
            dealerTotal = new Label("??") {{ setStyle("-fx-font-size: 18;"); }}
        );

        HBox playersRow = new HBox(50);
        playersRow.setAlignment(Pos.CENTER);
        playersRow.getChildren().addAll(
            createPlayerArea("YOU", "#00ff88"),
            createPlayerArea("BOT 1", "#44ccff"),
            createPlayerArea("BOT 2", "#ff88cc")
        );

        betField = new TextField("50");
        betField.setPrefWidth(80);
        betField.setStyle("-fx-background-color: #333; -fx-text-fill: white;");

        Button hitBtn = new Button("HIT");
        Button standBtn = new Button("STAND");
        newRoundButton = new Button("NEW ROUND");

        HBox controls = new HBox(20, new Label("Bet:"), betField, hitBtn, standBtn, newRoundButton);
        controls.setAlignment(Pos.CENTER);

        statusLabel = new Label("Click NEW ROUND to start");
        statusLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #00ff88;");

        root.getChildren().addAll(title, dealerArea, playersRow, controls, statusLabel);

        hitBtn.setOnAction(e -> hit());
        standBtn.setOnAction(e -> stand());
        newRoundButton.setOnAction(e -> newRound());

        newRound(); // auto-start first round

        Scene scene = new Scene(root, 1300, 900);
        stage.setScene(scene);
        stage.setTitle("FXcade - Blackjack");
        stage.show();
    }

    private VBox createPlayerArea(String name, String color) {
        HBox handBox = new HBox(10);
        Label total = new Label("0");
        Label bankroll = new Label("$1000");

        if ("YOU".equals(name)) {
            playerHandBox = handBox;
            playerTotal = total;
            playerBankroll = bankroll;
        } else if ("BOT 1".equals(name)) {
            bot1HandBox = handBox;
            bot1Bankroll = bankroll;
        } else {
            bot2HandBox = handBox;
            bot2Bankroll = bankroll;
        }

        VBox area = new VBox(10, 
            new Label(name) {{ setStyle("-fx-font-size: 20; -fx-text-fill: " + color + "; -fx-font-weight: bold;"); }},
            handBox, total, bankroll
        );
        area.setAlignment(Pos.CENTER);
        return area;
    }

    private void newRound() {
        int bet = 50;
        try {
            bet = Integer.parseInt(betField.getText().trim());
            if (bet < 1 || bet > game.getHuman().getBankroll()) bet = 50;
        } catch (Exception ignored) {}

        game.startNewRound(bet, 50, 50);
        refresh();
        statusLabel.setText("YOUR TURN");
        newRoundButton.setDisable(true);  // disable until round ends
    }

    private void hit() {
        if (!game.isHumansTurn()) return;
        game.humanHit();
        refresh();
        if (game.getHuman().getHand().getBestTotal() >= 21) endTurn();
    }

    private void stand() {
        if (!game.isHumansTurn()) return;
        endTurn();
    }

    private void endTurn() {
        new Thread(() -> {
            while (!game.isRoundOver()) {
                if (game.getTurnIndex() < 3) {
                    Participant p = game.getPlayers().get(game.getTurnIndex());
                    BotStrategy brain = p == game.getBot1() ? BotStrategy.hitUnder(16)
                                      : p == game.getBot2() ? BotStrategy.hitUnder(15) : null;

                    while (brain != null && !p.getHand().isBust() &&
                           brain.decide(p.getHand(), game.dealerUpCard()) == BotStrategy.Action.HIT) {
                        game.tryDealUp(p.getHand());
                        sleep(800);
                        runLater(this::refresh);
                    }
                } else {
                    game.revealDealerHole();
                    runLater(this::refresh);
                    sleep(1000);
                    game.dealerTurn();
                    game.finishRound();
                }
                game.turnIndex++;
            }

            runLater(() -> {
                refresh();
                statusLabel.setText(game.getResultBanner().replace("\n", " • "));
                newRoundButton.setDisable(false);  // RE-ENABLE NEW ROUND
            });
        }).start();
    }

    private void refresh() {
        renderHand(dealerHandBox, game.getDealer().getHand(), true);
        renderHand(playerHandBox, game.getHuman().getHand(), false);
        renderHand(bot1HandBox, game.getBot1().getHand(), false);
        renderHand(bot2HandBox, game.getBot2().getHand(), false);

        playerTotal.setText("Total: " + game.getHuman().getHand().getBestTotal());
        playerBankroll.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1Bankroll.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2Bankroll.setText("Bot 2: $" + game.getBot2().getBankroll());

        int dt = game.getDealer().getHand().getBestTotal();
        dealerTotal.setText((game.isRoundOver() || !game.hasDealerHoleHidden())
            ? (dt > 21 ? "BUST" : String.valueOf(dt))
            : "??");
    }

    private void renderHand(HBox box, Hand hand, boolean hideFirst) {
        box.getChildren().clear();
        for (int i = 0; i < hand.getCards().size(); i++) {
            Card c = hand.getCards().get(i);
            boolean show = hideFirst && i == 0 ? false : c.isFaceUp();
            String name = show ? cardName(c.getRank(), c.getSuit()) : "back.png";
            Image img = new Image(getClass().getResourceAsStream("/cards/" + name));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(90);
            iv.setPreserveRatio(true);
            box.getChildren().add(iv);
        }
    }

    private String cardName(Rank r, Suit s) {
        String rank = switch (r) {
            case ACE -> "ace"; case KING -> "king"; case QUEEN -> "queen"; case JACK -> "jack";
            case TEN -> "10"; default -> String.valueOf(r.getValue());
        };
        String suit = switch (s) {
            case CLUBS -> "clubs"; case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts"; case SPADES -> "spades";
        };
        return rank + "_of_" + suit + ".png";
    }

    private void sleep(long ms) { try { Thread.sleep(ms); } catch (Exception ignored) {} }
    private void runLater(Runnable r) { javafx.application.Platform.runLater(r); }
}