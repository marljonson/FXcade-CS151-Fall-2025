package ui;

import blackjack.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BlackjackController {
    private static final int DEFAULT_BET = 50;
    private static final Path HIGH_SCORE_PATH = Paths.get("data", "blackjack_high_scores.txt");


    // wire these fx ids in blackjack.fxml
    @FXML
    private HBox dealerCards, playerCards, bot1Cards, bot2Cards;

    @FXML
    private Label statusLabel, bankrollLabel, playerTotalLabel, dealerTotalLabel;
    @FXML
    private Label bot1BankLabel, bot2BankLabel, turnLabel;


    @FXML
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

    
    private void onNewGame() {
        game.resetForNewGame();
        startRound();
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
            backToMenu.run();
        });

        // Blackjack music
        try {
            var url = getClass().getResource("/audio/stray_sheep.mp3");
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
    }

    // Safely parse bet textfield and prevent negative input
    private int parseBetOrDefault(int fallback) {
        try {
            String s = (betField == null) ? null : betField.getText();
            int n = Integer.parseInt(s);
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
            saveHighScore(game.getHuman().getBankroll());
        }
    }

    // Render everything based on backend
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
            case ACE -> "ace"; case KING -> "king"; case QUEEN -> "queen"; case JACK -> "jack";
            case TEN -> "10"; default -> String.valueOf(r.getValue());
        };
        String suit = switch (s) {
            case CLUBS -> "clubs"; case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts"; case SPADES -> "spades";
        };
        return rank + "_of_" + suit + ".png";
    }

    private Image loadImage(String file) {
        return imageCache.computeIfAbsent(file, f -> {
            var in = Objects.requireNonNull(
                    getClass().getResourceAsStream(CARD_DIR + f),
                    "Missing card image: " + CARD_DIR + f);
            return new Image(in);
        });
    }
    private void saveHighScore(int bankroll){
        try {
            List<String> lines;
            if (Files.exists(HIGH_SCORE_PATH)){
                lines = new ArrayList<>(Files.readAllLines(HIGH_SCORE_PATH));
            } else {
                lines = new ArrayList<>();
            }

            String prefix = username + ":";

            boolean found = false;
            for(int i = 0; i < lines.size(); i++){
                String line = lines.get(i);
                if (line.startsWith(prefix)) {
                    // Existing entry = keep bigger value between old/new
                    String[] split = line.split(":");
                    int oldScore = Integer.parseInt(split[1]);
                    int best = Math.max(oldScore, bankroll);
                    lines.set(i, prefix + best);
                    found = true;
                    break;
                }
            }
            if (!found){
                // new user in the high score file
                lines.add(prefix + bankroll);
            }

            Files.createDirectories(HIGH_SCORE_PATH.getParent());
            Files.write(HIGH_SCORE_PATH, lines);
            
        } catch (Exception e) {
            System.out.println("Error writing blackjack high scores: " + e.getMessage() + "for user: " + username);
        }
    }

    private Path savePath() {
        return Path.of("data", "saves_blackjack", username + ".json");
    }

}