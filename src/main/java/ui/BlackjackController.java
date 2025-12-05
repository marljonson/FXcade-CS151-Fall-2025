package ui;

import blackjack.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
    private Button hitButton, standButton, newRoundButton;

    private MediaPlayer blackjackMusicPlayer;
    private Button musicToggleButton;

    public BlackjackController(Stage stage, Runnable backToMenu) {
        this.stage = stage;
        this.backToMenu = backToMenu;
    }

    public void start(String username) {
        showMainMenu(username);
    }

    private void showMainMenu(String username) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label("BLACKJACK");
        title.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #212529;");

        Button newGameBtn = new Button("NEW GAME");
        Button loadGameBtn = new Button("LOAD GAME");

        newGameBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 22; -fx-padding: 20 60; -fx-font-weight: bold;");
        loadGameBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 22; -fx-padding: 20 60; -fx-font-weight: bold;");

        newGameBtn.setOnAction(e -> startNewGame(username));
        loadGameBtn.setOnAction(e -> loadGame(username));

        root.getChildren().addAll(title, newGameBtn, loadGameBtn);
        addToolbar(root);

        Scene scene = new Scene(root, 900, 700);
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

        addToolbar(root);

        VBox gameArea = new VBox(20);
        gameArea.setPadding(new Insets(20));
        gameArea.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("BLACKJACK");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #212529;");

        VBox dealerArea = new VBox(10);
        dealerArea.setAlignment(Pos.CENTER);
        dealerArea.getChildren().addAll(
            new Label("DEALER") {{ setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #dc3545;"); }},
            dealerHandBox = new HBox(12) {{ setAlignment(Pos.CENTER); }},
            dealerTotal = new Label("??") {{ setStyle("-fx-font-size: 20;"); }}
        );

        HBox playersRow = new HBox(60);
        playersRow.setAlignment(Pos.CENTER);
        playersRow.getChildren().addAll(
            createPlayerArea("YOU", "#28a745"),
            createPlayerArea("BOT 1", "#17a2b8"),
            createPlayerArea("BOT 2", "#6f42c1")
        );

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

        gameArea.getChildren().addAll(title, dealerArea, playersRow, controls, statusLabel);
        root.getChildren().add(gameArea);

        hitButton.setOnAction(e -> hit());
        standButton.setOnAction(e -> stand());
        newRoundButton.setOnAction(e -> newRound());
        saveButton.setOnAction(e -> showSaveDialog());

        // Start first round — AFTER betField exists
        newRound();

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private void addToolbar(VBox root) {
        HBox toolBar = new HBox(15);
        toolBar.setPadding(new Insets(10));
        toolBar.setStyle("-fx-background-color: #555555;");
        toolBar.setAlignment(Pos.CENTER_RIGHT);

        musicToggleButton = new Button("Play Music");
        Button mainMenuButton = new Button("Main Menu");
        Button signOutButton = new Button("Sign Out");

        toolBar.getChildren().addAll(musicToggleButton, mainMenuButton, signOutButton);
        mainMenuButton.setOnAction(e -> {
            if (blackjackMusicPlayer != null) {
                blackjackMusicPlayer.stop(); // stop the game music
                musicToggleButton.setText("Play Music"); // force button text to say "Play Music"
            }
            backToMenu.run();
        });

        signOutButton.setOnAction(e -> {
            if (blackjackMusicPlayer != null) {
                blackjackMusicPlayer.stop(); // stop the game music
                musicToggleButton.setText("Play Music"); // force button text to say "Play Music"
            }
            backToMenu.run();
        });

        // Blackjack music
        try {
            var url = getClass().getResource("/audio/blackjack.mp3");
            if (url != null) {
                Media media = new Media(url.toExternalForm());
                blackjackMusicPlayer = new MediaPlayer(media);
                blackjackMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

                musicToggleButton.setOnAction(e -> {
                    if (blackjackMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        blackjackMusicPlayer.pause();
                        musicToggleButton.setText("Play Music");
                    } else {
                        blackjackMusicPlayer.play();
                        musicToggleButton.setText("Pause Music");
                    }
                });
            }
        } catch (Exception ignored) {}

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
            String text = betField.getText().trim();
            if (!text.isEmpty()) {
                bet = Integer.parseInt(text);
                if (bet < 1 || bet > game.getHuman().getBankroll()) {
                    bet = Math.min(50, game.getHuman().getBankroll());
                }
            }
        } catch (Exception ignored) {}

        game.startNewRound(bet, 50, 50);
        refresh();
        statusLabel.setText("YOUR TURN");
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
            while (!game.isRoundOver()) {
                if (game.getTurnIndex() < 3) {
                    Participant p = game.getPlayers().get(game.getTurnIndex());
                    BotStrategy brain = p == game.getBot1() ? BotStrategy.hitUnder(16)
                                      : p == game.getBot2() ? BotStrategy.hitUnder(15) : null;

                    while (brain != null && !p.getHand().isBust() &&
                           brain.decide(p.getHand(), game.dealerUpCard()) == BotStrategy.Action.HIT) {
                        game.tryDealUp(p.getHand());
                        sleep(800);
                        Platform.runLater(this::refresh);
                    }
                } else {
                    game.revealDealerHole();
                    Platform.runLater(this::refresh);
                    sleep(1000);
                    game.dealerTurn();
                    game.finishRound();
                }
                game.turnIndex++;
            }

            Platform.runLater(() -> {
                refresh();
                statusLabel.setText(game.getResultBanner().replace("\n", " • "));
                newRoundButton.setDisable(false);
                hitButton.setDisable(false);
                standButton.setDisable(false);
            });
        }).start();
    }

    private void showSaveDialog() {
        String saveCode = game.toJsonSave();
        TextInputDialog dialog = new TextInputDialog(saveCode);
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Your Save Code — Copy It!");
        dialog.getEditor().setEditable(false);
        dialog.getEditor().selectAll();
        dialog.showAndWait();
    }

    private void refresh() {
        renderHand(dealerHandBox, game.getDealer().getHand(), true);
        renderHand(playerHandBox, game.getHuman().getHand(), false);
        renderHand(bot1HandBox, game.getBot1().getHand(), !game.isRoundOver());
        renderHand(bot2HandBox, game.getBot2().getHand(), !game.isRoundOver());

        playerTotal.setText("Total: " + game.getHuman().getHand().getBestTotal());
        playerBankroll.setText("Bankroll: $" + game.getHuman().getBankroll());
        bot1Bankroll.setText("Bot 1: $" + game.getBot1().getBankroll());
        bot2Bankroll.setText("Bot 2: $" + game.getBot2().getBankroll());

        int dt = game.getDealer().getHand().getBestTotal();
        dealerTotal.setText((game.isRoundOver() || !game.hasDealerHoleHidden())
            ? (dt > 21 ? "BUST" : String.valueOf(dt))
            : "??");
    }

    private void renderHand(HBox box, Hand hand, boolean hideAll) {
        box.getChildren().clear();
        for (Card c : hand.getCards()) {
            boolean show = !hideAll && c.isFaceUp();
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

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {} }
}