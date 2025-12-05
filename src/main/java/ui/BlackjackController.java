package ui;

import blackjack.*;
import javafx.application.Platform;
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
    private Label statusLabel, turnLabel;
    private TextField betField;
    private Button hitButton, standButton, newRoundButton;

    public BlackjackController(Stage stage, Runnable backToMenu) {
        this.stage = stage;
        this.backToMenu = backToMenu;
    }

    public void start(String username) {
        showMainMenu(username);
    }

    private void showMainMenu(String username) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("BLACKJACK");
        title.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #212529;");

        Button newGameBtn = new Button("NEW GAME");
        Button loadGameBtn = new Button("LOAD GAME");

        newGameBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 20; -fx-padding: 15 40;");
        loadGameBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 20; -fx-padding: 15 40;");

        newGameBtn.setOnAction(e -> startNewGame(username));
        loadGameBtn.setOnAction(e -> loadGame(username));

        root.getChildren().addAll(title, newGameBtn, loadGameBtn);

        addToolbar(root);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("FXcade - Blackjack");
        stage.show();
    }

    private void startNewGame(String username) {
        game = new BlackjackGame(username);
        game.getHuman().setBankroll(1000);
        showGameScreen();
    }

    private void loadGame(String username) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Paste your save code:");
        dialog.setContentText("Save Code:");

        dialog.showAndWait().ifPresent(code -> {
            try {
                game = BlackjackGame.fromJsonSave(code, username);
                showGameScreen();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid save code!");
                alert.show();
            }
        });
    }

    private void showGameScreen() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Toolbar
        addToolbar(root);

        VBox gameArea = new VBox(20);
        gameArea.setPadding(new Insets(20));
        gameArea.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("BLACKJACK");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #212529;");

        // Dealer
        VBox dealerArea = new VBox(10);
        dealerArea.setAlignment(Pos.CENTER);
        dealerArea.getChildren().addAll(
            new Label("DEALER") {{ setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #dc3545;"); }},
            dealerHandBox = new HBox(12) {{ setAlignment(Pos.CENTER); }},
            dealerTotal = new Label("??") {{ setStyle("-fx-font-size: 20;"); }}
        );

        // Players
        HBox playersRow = new HBox(50);
        playersRow.setAlignment(Pos.CENTER);
        playersRow.getChildren().addAll(
            createPlayerArea("YOU", "#28a745"),
            createPlayerArea("BOT 1", "#17a2b8"),
            createPlayerArea("BOT 2", "#6f42c1")
        );

        // Controls
        betField = new TextField("50");
        betField.setPrefWidth(80);

        hitButton = new Button("HIT");
        standButton = new Button("STAND");
        newRoundButton = new Button("NEW ROUND");
        Button saveButton = new Button("SAVE GAME");

        hitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        standButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");
        newRoundButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16;");
        saveButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");

        HBox controls = new HBox(20, new Label("Bet:"), betField, hitButton, standButton, newRoundButton, saveButton);
        controls.setAlignment(Pos.CENTER);

        statusLabel = new Label("Click NEW ROUND to start");
        statusLabel.setStyle("-fx-font-size: 22; -fx-text-fill: #007bff; -fx-font-weight: bold;");

        turnLabel = new Label("");
        turnLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #28a745; -fx-font-weight: bold;");

        gameArea.getChildren().addAll(title, dealerArea, playersRow, controls, statusLabel, turnLabel);
        root.getChildren().add(gameArea);

        hitButton.setOnAction(e -> hit());
        standButton.setOnAction(e -> stand());
        newRoundButton.setOnAction(e -> newRound());
        saveButton.setOnAction(e -> showSaveDialog());

        newRound(); // start first round

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private void addToolbar(VBox root) {
        HBox toolBar = new HBox(15);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #555555;");
        toolBar.setAlignment(Pos.CENTER_RIGHT);

        Button musicToggleButton = new Button("Play Music");
        Button mainMenuButton = new Button("Main Menu");
        Button signOutButton = new Button("Sign Out");

        toolBar.getChildren().addAll(musicToggleButton, mainMenuButton, signOutButton);
        mainMenuButton.setOnAction(e -> backToMenu.run());
        signOutButton.setOnAction(e -> backToMenu.run());

        root.getChildren().add(0, toolBar);
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
            new Label(name) {{ setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: " + color + ";"); }},
            handBox, total, bankroll
        );
        area.setAlignment(Pos.CENTER);
        area.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
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
        statusLabel.setText("Your turn!");
        turnLabel.setText("YOUR TURN");
        hitButton.setDisable(false);
        standButton.setDisable(false);
        newRoundButton.setDisable(true);
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
        hitButton.setDisable(true);
        standButton.setDisable(true);

        new Thread(() -> {
            // Play Bot 1
            if (game.getTurnIndex() == 0) {
                playBot(game.getBot1(), BotStrategy.hitUnder(16));
                game.turnIndex = 1;
            }
            // Play Bot 2
            if (game.getTurnIndex() == 1) {
                playBot(game.getBot2(), BotStrategy.hitUnder(15));
                game.turnIndex = 2;
            }
            // Dealer turn
            if (game.getTurnIndex() == 2) {
                game.revealDealerHole();
                Platform.runLater(this::refresh);
                sleep(1000);
                game.dealerTurn();
                game.finishRound();
                game.turnIndex = 3;
            }

            Platform.runLater(() -> {
                refresh();
                statusLabel.setText(game.getResultBanner().replace("\n", " • "));
                newRoundButton.setDisable(false);
                hitButton.setDisable(true);
                standButton.setDisable(true);
            });
        }).start();
    }

    private void showSaveDialog() {
        String saveCode = game.toJsonSave();
        TextInputDialog dialog = new TextInputDialog(saveCode);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Your Save Code (Copy this):");
        dialog.getEditor().setEditable(false);
        dialog.getEditor().selectAll();
        dialog.showAndWait();
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

    private void sleep(long ms) { try { Thread.sleep(ms); } catch (Exception ignored) {} }
    private void runLater(Runnable r) { javafx.application.Platform.runLater(r); }
}