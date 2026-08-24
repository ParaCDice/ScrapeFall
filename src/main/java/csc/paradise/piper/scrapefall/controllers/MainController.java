package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import csc.paradise.piper.scrapefall.models.CardUtils;
import csc.paradise.piper.scrapefall.models.NodeUtils;
import csc.paradise.piper.scrapefall.models.ScrapedCard;
import csc.paradise.piper.scrapefall.models.Scraper;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
            AnchorPane tempPane = (AnchorPane) ApplicationManager.getNodesFromFXML("card-pane.fxml");
            for (Node child : tempPane.getChildren()) {
                if (child instanceof Label labelNode) {
                    String text;
                    if(card.card_faces()!=null){
                        text = switch (labelNode.getId()) {
                            case "cardNameLabel" -> card.name();
                            case "cardCostLabel" -> card.card_faces().getFirst().mana_cost();
                            case "cardTypeLabel" -> card.card_faces().getFirst().type_line();
                            case "cardSetLabel" -> card.set_name();
                            case "cardDescLabel" -> card.card_faces().getFirst().oracle_text();
                            case null, default -> "";
                        };
                    }else{
                        text = switch (labelNode.getId()) {
                            case "cardNameLabel" -> card.name();
                            case "cardCostLabel" -> card.mana_cost();
                            case "cardTypeLabel" -> card.type();
                            case "cardSetLabel" -> card.set_name();
                            case "cardDescLabel" -> card.oracle_text();
                            case null, default -> "";
                        };
                    }
                    if (text != null) {
                        labelNode.setText(text);
                    }
                } else if (child instanceof ImageView childImageView) {
                    childImageView.setImage(CardUtils.getCardImage(card));
                } else if (child instanceof Text) {
                    if (child.getId().equalsIgnoreCase("cardID")) {
                        ((Text) child).setText(String.valueOf(card.scryfall_id()));
                    }
                }

            }
            cardPaneContainer.getChildren().add(tempPane);
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
                if (node.getId().equalsIgnoreCase("priceLabel")) {
                    try {
                        Matcher priceMatcher = pricePattern.matcher(((Label) node).getText());
                        System.out.println(priceMatcher.matches());
                        totalPrice = totalPrice.add(new BigDecimal(priceMatcher.group(1)));
                        System.out.println(totalPrice);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("WARNING: COULD NOT GET PRICE FOR CARD");
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
}
