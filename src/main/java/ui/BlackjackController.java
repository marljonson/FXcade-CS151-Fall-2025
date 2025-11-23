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
    
    // wire these fx ids in blackjack.fxml
    @FXML private HBox dealerCards;
    @FXML private HBox playerCards;
    @FXML private HBox bot1Cards;
    @FXML private HBox bot2Cards;

    @FXML private Label statusLabel;
    @FXML private Label bankrollLabel;
    @FXML private Label playerTotalLabel;
    @FXML private Label dealerTotalLabel;

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

}
