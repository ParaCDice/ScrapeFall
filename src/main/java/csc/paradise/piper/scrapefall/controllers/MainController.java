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
import javafx.scene.text.Text;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardList;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.OrderMode;
import net.dzultra.jsa.exceptions.CardSearchException;

import java.io.IOException;
import java.util.ArrayList;
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

    static CardRequester cardRequester;

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
                if (child instanceof Label labelNode) {
                    String text = switch (labelNode.getId()) {
                        case "cardNameLabel" -> card.name();
                        case "cardCostLabel" -> card.mana_cost();
                        case "cardTypeLabel" -> card.type();
                        case "cardSetLabel" -> card.set_name();
                        case "cardDescLabel" -> card.oracle_text();
                        case null, default -> "";
                    };
                    if(text!=null && !text.isEmpty()) {
                        labelNode.setText(text);
                    }
                } else if (child instanceof ImageView childImageView) {
                    try {
                        childImageView.setImage(new Image(Objects.requireNonNull(card.image_uris()).normal().toURL().openStream()));
                        // TODO: Double sided cards don't work LOL
                    } catch (Exception ignored) {
                        System.out.println("aw no img");
                    }
                } else if(child instanceof Text){
                    if(child.getId().equalsIgnoreCase("cardID")){
                        ((Text) child).setText(String.valueOf(card.scryfall_id()));
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
    protected void onExit() {
        System.exit(0);
    }

    public void onQueryFieldTextChanged(KeyEvent inputMethodEvent) {
        query = queryField.getText();
    }
}
