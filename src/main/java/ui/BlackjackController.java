package ui;

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
/*
* JavaFX controller for Blackjack screen
* UI expects FXML with the fx:id's used below
*/
public class BlackjackController {
    private static final int DEFAULT_BET = 50;

    // wire these fx ids in blackjack.fxml
    @FXML private HBox dealerCards;
    @FXML private HBox playerCards;
    @FXML private HBox bot1Cards;
    @FXML private HBox bot2Cards;

    @FXML private Label statusLabel;
    @FXML private Label bankrollLabel;
    @FXML private Label playerTotalLabel;
    @FXML private Label dealerTotalLabel;
    @FXML private Label bot1BankLabel;
    @FXML private Label bot2BankLabel;
    @FXML private Label turnLabel;


    @FXML private TextField betField;

    @FXML private Button hitButton;
    @FXML private Button standButton;
    @FXML private Button newRoundButton;
    @FXML private Button saveButton;
    @FXML private Button loadButton;

    private BlackjackGame game;
    private String username = "Player"; // we set it in Main/App using init

    // Images from resources/cards
    private static final String CARD_DIR = "/cards/";
    private static final double CARD_WIDTH = 90; // adjust later if needed (?)
    private final Map<String, Image> imageCache = new HashMap<>();

    // Call from scene loader in Main
    public void init(String username){
        if (username != null && !username.isBlank()) this.username = username;
        this.game = new BlackjackGame(this.username);
        startRound();
    }

    // TODO: UI events: onStand, onNewRound, onSave, onLoad
    @FXML
    private void onHit(){
        game.humanHit();
        refresh();
        endIfOver();
    }

    // TODO: Internal event helpers to link with blackjackgame

    // Read bet, call startNewRound, refresh
    private void startRound(){
        int bet = parseBetOrDefault(DEFAULT_BET);
        game.startNewRound(bet, DEFAULT_BET, DEFAULT_BET); // bots bet 50 by default
        refresh();
    }

    // Safely parse bet testfield and prevent negative input
    private int parseBetOrDefault(int fallback){
        try {
            int n = Integer.parseInt(betField.getText().trim());
            return Math.max(0, n);
        } catch (Exception e) {
            return fallback;
        }
    }

    // Allow player to end the game, implement high score submission later
    private void endIfOver(){
        if (game.isRoundOver()) {
            statusLabel.setText(game.getResultBanner());
            hitButton.setDisable(true);
            standButton.setDisable(true);
            newRoundButton.setDisable(false);
            // Possible TODO: submit high score here
        }
    }
    
    // Render everything based on backend
    private void refresh(){
        renderHand(playerCards, game.getHuman().getHand());
        renderHand(bot1Cards, game.getBot1().getHand());
        renderHand(bot2Cards, game.getBot2().getHand());
        renderHand(dealerCards, game.getDealer().getHand());

        bankrollLabel.setText("Bankroll: $" + game.getHuman().getBankroll());
        statusLabel.setText(game.getResultBanner());

        // totals 
        playerTotalLabel.setText(String.valueOf(game.getHuman().getHand().getBestTotal()));
        dealerTotalLabel.setText(game.getDealer().getHand().isBust()
            ? "BUST" : String.valueOf(game.getDealer().getHand().getBestTotal()));
        boolean isHumansTurn = !game.isRoundOver() && game.getTurnIndex() == 0;
        hitButton.setDisable(!isHumansTurn);
        standButton.setDisable(!isHumansTurn);
        newRoundButton.setDisable(isHumansTurn);
    }

    // Render hand into HBox
    private void renderHand(HBox box, Hand hand){
        box.getChildren().clear();
        for (Card card : hand.getCards()){
            box.getChildren().add(cardNode(card));
        }
    }
    
    // Card to ImageView uses cache, show back.png if face-down
    private Node cardNode(Card card){
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
            case TWO -> "2";   case THREE -> "3";  case FOUR -> "4";   case FIVE -> "5";
            case SIX -> "6";   case SEVEN -> "7";  case EIGHT -> "8";  case NINE -> "9";
            case TEN -> "10";  case JACK -> "jack"; case QUEEN -> "queen";
            case KING -> "king"; case ACE -> "ace";
        };
        String s = switch (suit) {
            case CLUBS -> "clubs";
            case DIAMONDS -> "diamonds";
            case HEARTS -> "hearts";
            case SPADES -> "spades";
        };
        return r + "_of_" + s + ".png";
    }

    private Image loadImage(String file){
        return imageCache.computeIfAbsent(file, f ->{
            var in = Objects.requireNonNull(
                getClass().getResourceAsStream(CARD_DIR + f),
                "Missing card image: " + CARD_DIR + f);
            return new Image(in);
        });
    }


}
