package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import csc.paradise.piper.scrapefall.models.ScrapedCard;
import csc.paradise.piper.scrapefall.models.Scraper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardList;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.OrderMode;
import net.dzultra.jsa.exceptions.CardSearchException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainController {

    @FXML
    public TilePane cardPaneContainer;
    @FXML
    public Pane test;
    @FXML
    public Button scrapeButton;
    @FXML
    public Button getCardsButton;
    @FXML
    public TilePane pricePaneContainer;
    @FXML
    public Button selectCardButton;
    @FXML
    public Label cardsErrorLabel;
    @FXML
    public Label scrapeErrorLabel;
    @FXML
    public TextField queryField;

    CardRequester cardRequester;

    static Card selectedCard;

    private String query = "";

    @FXML
    protected void initialize() {
        cardRequester = new CardRequester(new ScryfallClient("Scrape-Fall"));
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
                if (child.getClass().equals(Label.class)) {
                    if (((Label) child).getText().contains("Set")) {
                        ((Label) child).setText(card.set_name());
                    } else if (((Label) child).getText().contains("Name")) {
                        ((Label) child).setText(card.name());
                    } else if (((Label) child).getText().contains("Cost")) {
                        ((Label) child).setText(card.mana_cost());
                    } else if (((Label) child).getText().contains("Description")) {
                        ((Label) child).setText(card.oracle_text());
                    } else if (((Label) child).getText().contains("Type")) {
                        ((Label) child).setText(card.type_line());
                    }
                } else if (child.getClass().equals(ImageView.class)) {
                    ImageView childImageView = (ImageView) child;
                    try {
                        childImageView.setImage(new Image(Objects.requireNonNull(card.image_uris()).normal().toURL().openStream()));
                        // TODO: Double sided cards don't work LOL
                    } catch (Exception ignored) {
                        System.out.println("aw no img");
                    }
                }

            }
            cardPaneContainer.getChildren().add(tempPane);
        }
    }


    public static void setSelectedCard(Card newCard) {
        selectedCard = newCard;
    }

    @FXML
    protected void onScrapeClick() {
        List<ScrapedCard> cards = Scraper.oasisScraper(cardRequester.getCardsByQuery("Bloom Tender", null, OrderMode.NAME, null).cards()[0]);
        for (ScrapedCard scrapedCard : cards) {
            Card card = cardRequester.getCardByName(scrapedCard.getCardName(), scrapedCard.getCardSet(), true);
            AnchorPane tempPane = (AnchorPane) ApplicationManager.getNodesFromFXML("price-pane.fxml");
            for (Node child : tempPane.getChildren()) {
                if (child.getClass().equals(Label.class)) {
                    if (((Label) child).getText().contains("Set")) {
                        ((Label) child).setText(card.set_name());
                    } else if (((Label) child).getText().contains("Name")) {
                        ((Label) child).setText(card.name());
                    } else if (((Label) child).getText().contains("Price")) {
                        ((Label) child).setText(scrapedCard.getPriceAsString());
                    }
                } else if (child.getClass().equals(ImageView.class)) {
                    ImageView childImageView = (ImageView) child;
                    try {
                        childImageView.setImage(new Image(Objects.requireNonNull(card.image_uris()).png().toURL().openStream()));
                    } catch (IOException ignored) {
                    }
                }
            }
            pricePaneContainer.getChildren().add(tempPane);
        }
    }

    @FXML
    protected void onExit() {
        System.exit(0);
    }

    public void onQueryFieldTextChanged(KeyEvent inputMethodEvent) {
        query = queryField.getText();
    }
}
