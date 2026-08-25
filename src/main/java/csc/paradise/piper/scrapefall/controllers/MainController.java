package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import csc.paradise.piper.scrapefall.models.CardUtils;
import csc.paradise.piper.scrapefall.models.NodeUtils;
import csc.paradise.piper.scrapefall.models.ScrapedCard;
import csc.paradise.piper.scrapefall.models.Scraper;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardList;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.CardIDType;
import net.dzultra.jsa.cards.enums.OrderMode;
import net.dzultra.jsa.exceptions.CardSearchException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    @FXML
    private TilePane cardPaneContainer;
    @FXML
    private ScrollPane cardScrollPane;
    @FXML
    private Button scrapeButton;
    @FXML
    private Button getCardsButton;
    @FXML
    private TilePane pricePaneContainer;
    @FXML
    private Label totalPriceLabelBackDrop;
    @FXML
    private Label totalPriceLabel;
    private int currentPricePaneContCount = 0;
    private int lastPricePaneContCount = 0;
    @FXML
    private Label cardsErrorLabel;
    @FXML
    private Label scrapeErrorLabel;
    @FXML
    private TextField queryField;

    static CardRequester cardRequester;

    private String query = "";

    private final Pattern pricePattern = Pattern.compile("\\$([0-9]+.[0-9]{2})");

    @FXML
    protected void initialize() {
        cardRequester = new CardRequester(new ScryfallClient("Scrape-Fall"));

        cardScrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            checkVisibleNodes();
        });

        frameLoop();
    }

    @FXML
    protected void getCards() {
        cardsErrorLabel.setText("");
        if (query.isEmpty()) {
            cardsErrorLabel.setText("Please enter a query");
            return;
        }
        CardList cards;
        try {
            cards = cardRequester.getCardsByQuery(query, null, OrderMode.NAME, null);
        } catch (CardSearchException cardSearchException) {
            cardsErrorLabel.setText("No cards matching query");
            return;
        }

        cardPaneContainer.getChildren().clear();

        for (Card card : cards.cards()) {
            ApplicationManager.LoadedView<CardPaneController> loadedView = ApplicationManager.loadFXML("card-pane.fxml");
            CardPaneController cpc = loadedView.controller();

            cpc.setCard(card);
            loadedView.root().setUserData(loadedView.controller());
            cardPaneContainer.getChildren().add(loadedView.root());
        }
    }



    public void frameLoop() {
        new AnimationTimer() {
            long lastUpdate = -1;
            double deltaTime;


            private void update() {
                currentPricePaneContCount = pricePaneContainer.getChildren().toArray().length;

                if(lastPricePaneContCount!=currentPricePaneContCount){
                    float totalPrice = getTotalPriceOfScrapedCardsInContainer().floatValue();
                    String totalPriceString = NumberFormat.getCurrencyInstance().format(totalPrice);
                    totalPriceLabelBackDrop.setText(totalPriceString);
                    totalPriceLabel.setText(totalPriceString);
                }

                lastPricePaneContCount = currentPricePaneContCount;
            }

            @Override
            public void handle(long now) {
                if (lastUpdate < 0) {
                    lastUpdate = now;
                    return;
                }

                deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                update();
                lastUpdate = now;
            }
        }.start();
    }


    private BigDecimal getTotalPriceOfScrapedCardsInContainer(){
        ArrayList<Node> descendents = NodeUtils.getAllNodes(pricePaneContainer);
        BigDecimal totalPrice = new BigDecimal(0);

        for(Node node : descendents){
            try {
                if (node.getId().equalsIgnoreCase("pricePane")) {
                    try {
                        BigDecimal price = ((ScrapedCardController) node.getUserData()).getPrice();
                        totalPrice = totalPrice.add(price);
                    } catch (Exception e) {
                        System.err.println("WARNING: Failed on node id=" + node.getId() + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    }
                }
            }catch (NullPointerException ignored){}
        }
        return totalPrice;
    }



    @FXML
    protected void onExit() {
        System.exit(0);
    }

    public void onQueryFieldTextChanged(KeyEvent inputMethodEvent) {
        query = queryField.getText();
    }

    public void checkVisibleNodes() {
        for (Node node : cardPaneContainer.getChildren()){
            if(NodeUtils.isNodeVisible(node, cardScrollPane) && node.getUserData() instanceof CardPaneController visibleCard){
                visibleCard.loadImage();
            }
        }

    }

    public void onKeyPressed(KeyEvent keyEvent) {

        if(keyEvent.getCode()== KeyCode.ENTER){
            getCards();
        }

    }
}
