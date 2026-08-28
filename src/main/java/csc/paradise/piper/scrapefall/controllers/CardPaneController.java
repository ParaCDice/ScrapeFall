package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import csc.paradise.piper.scrapefall.models.CardUtils;
import csc.paradise.piper.scrapefall.models.ManaRenderer;
import csc.paradise.piper.scrapefall.NodeUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.CardIDType;
import net.dzultra.jsa.sets.SetRequester;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardPaneController {
    @FXML
    public AnchorPane cardPane;
    @FXML
    public Button rulingsButton;
    @FXML
    public Button changeButton;
    @FXML
    public Button scrapeButton;
    @FXML
    public Text cardID;
    @FXML
    public Label cardNameLabel;
    @FXML
    public TextFlow cardCostFlow;
    @FXML
    public Label cardTypeLabel;
    @FXML
    public Label cardSetLabel;
    @FXML
    public TextFlow cardDescFlow;
    @FXML
    public ImageView cardImageView;
    @FXML
    public Button linkButton;

    Color notLegalColor = Color.rgb(174,174,174);
    Color legalColor = Color.rgb(117,152,110);
    Color bannedColor = Color.rgb(167, 31, 42);
    Color restrictColor = Color.rgb(36, 103, 130);

    public TilePane pricePaneContainer;

    Node rulingsFxml;

    Card currentCard;

    ScryfallClient scrapeFallClient = new ScryfallClient("Scrape-Fall");

    CardRequester cardRequester = new CardRequester(scrapeFallClient);
    SetRequester setRequester = new SetRequester(scrapeFallClient);

    boolean cardImageLoaded = false;

    @FXML
    protected void initialize(){
        rulingsFxml = ApplicationManager.getNodesFromFXML("rulings-dialog.fxml");
    }


    public Card getCard(){
        if(currentCard==null) {
            currentCard = cardRequester.getCardById(cardID.getText(), CardIDType.SCRYFALL);
            return currentCard;
        }
        return currentCard;
    }

    public void setCard(Card card) {

        if(card==null){
            throw new NullPointerException("ERROR: CARD IS NULL");
        } else if(!CardUtils.isCardReal(card)){
            throw new IllegalArgumentException("ERROR: CARD ISN'T REAL");
        } else if (CardUtils.isCardRealDoubleSided(card)) {
            throw new IllegalArgumentException("ERROR: CARD IS DOUBLE-SIDED");
        }

        this.currentCard = card;

        if (card.scryfall_id() != null) {
            cardID.setText(String.valueOf(card.scryfall_id()));
        }

        // Still checking for double sided cards cause cards that have prepared spells, adventures, etc are considered double-sided when they really aren't.
        boolean hasFaces = card.card_faces() != null;

        String name = card.name();
        String cost = hasFaces ? card.card_faces().getFirst().mana_cost() + " // " + card.card_faces().getLast().mana_cost(): card.mana_cost();
        String type = hasFaces ? card.card_faces().getFirst().type_line() + " // " + card.card_faces().getLast().type_line() : card.type();
        String set = card.set_name();
        String desc = hasFaces ? card.card_faces().getFirst().oracle_text() + " // " + card.card_faces().getLast().oracle_text() : card.oracle_text();

        if (name != null) cardNameLabel.setText(name);
        if (cost != null) {
            cardCostFlow.getChildren().clear();
            cardCostFlow.getChildren().addAll(ManaRenderer.renderCardText(cost));
        }
        if (type != null) cardTypeLabel.setText(type);
        if (set != null) cardSetLabel.setText(set);
        if (desc != null) {
            cardDescFlow.getChildren().clear();
            cardDescFlow.getChildren().addAll(ManaRenderer.renderCardText(desc));
        }

        loadImage();
    }

    public void loadImage(){
        if(!cardImageLoaded) {
            cardImageLoaded=true;
            cardImageView.setImage(CardUtils.getCardImage(getCard()));
        }
    }

    private void adjustRulingsDialog(){
        List<Node> nodes = NodeUtils.getAllNodes((Parent) rulingsFxml);
        for (Node node : nodes){
            switch (node.getId()){
                //--------------------- standard
                case "standardLegalBg":{
                    Color selectedColor = switch (getCard().legalities().standard()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "standardLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().standard()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- pioneer
                case "pioneerLegalBg":{
                    Color selectedColor = switch (getCard().legalities().pioneer()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "pioneerLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().pioneer()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- modern
                case "modernLegalBg":{
                    Color selectedColor = switch (getCard().legalities().modern()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "modernLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().modern()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- legacy
                case "legacyLegalBg":{
                    Color selectedColor = switch (getCard().legalities().legacy()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "legacyLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().legacy()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- vintage
                case "vintageLegalBg":{
                    Color selectedColor = switch (getCard().legalities().vintage()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "vintageLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().vintage()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- commander
                case "commanderLegalBg":{
                    Color selectedColor = switch (getCard().legalities().commander()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "commanderLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().commander()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- oathbreaker
                case "oathbreakerLegalBg":{
                    Color selectedColor = switch (getCard().legalities().oathbreaker()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "oathbreakerLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().oathbreaker()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- alchemy
                case "alchemyLegalBg":{
                    Color selectedColor = switch (getCard().legalities().alchemy()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "alchemyLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().alchemy()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- historic
                case "historicLegalBg":{
                    Color selectedColor = switch (getCard().legalities().historic()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "historicLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().historic()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- brawl
                case "brawlLegalBg":{
                    Color selectedColor = switch (getCard().legalities().brawl()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "brawlLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().brawl()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- comp Brawl
                case "compBrawlLegalBg":{
                    Color selectedColor = switch (getCard().legalities().standardbrawl()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "compBrawlLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().standardbrawl()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- timeless
                case "timelessLegalBg":{
                    Color selectedColor = switch (getCard().legalities().timeless()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "timelessLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().timeless()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- pauper
                case "pauperLegalBg":{
                    Color selectedColor = switch (getCard().legalities().pauper()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "pauperLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().pauper()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //---------------- penny
                case "pennyLegalBg":{
                    Color selectedColor = switch (getCard().legalities().penny()){
                        case "legal" -> legalColor;
                        case "banned" -> bannedColor;
                        case "restricted" -> restrictColor;
                        case null, default -> notLegalColor;
                    };
                    ((Rectangle) node).setFill(selectedColor);
                    ((Rectangle) node).setStroke(selectedColor);
                    break;
                }
                case "pennyLegalTxt":{
                    Text legalTxt = (Text) node;
                    String text = switch (getCard().legalities().penny()){
                        case "legal" -> "LEGAL";
                        case "banned" -> "BANNED";
                        case "restricted" -> "RESTRICT.";
                        case null, default -> "NOT LEGAL";
                    };
                    legalTxt.setText(text);
                    break;
                }
                //----------------
                case null, default:{
                    break;
                }
            }
        }
    }


    public void onRulingsClicked(MouseEvent mouseEvent) {
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.setTitle("Rulings for " + getCard().name());
        dialog.getDialogPane().setPrefWidth(300);
        dialog.getDialogPane().setPrefHeight(260);
        dialog.getDialogPane().getChildren().add(rulingsFxml);
        adjustRulingsDialog();
        dialog.showAndWait()
                .filter(response -> response == ButtonType.OK);
    }

    private void findPricePaneContainer(){
        ArrayList<Node> allNodes = NodeUtils.getAllNodes(cardPane.getParent().getParent().getParent().getParent().getParent()); //UGLY I KNOW!!
        for (Node node : allNodes){
            if(node!=null) {
                if (node instanceof TilePane && node.getId().equalsIgnoreCase("pricePaneContainer")) {
                    pricePaneContainer = (TilePane) node;
                }
            }
        }
    }

    public void onLinkClicked(MouseEvent mouseEvent){
        try {
            Desktop.getDesktop().browse(getCard().scryfall_uri());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("WARNING: BROWSER WILL NOT LAUNCH.");
        }
    }

    public void onScrapeClicked(MouseEvent mouseEvent) {
        if(pricePaneContainer==null) {
            findPricePaneContainer();
        }
        pricePaneContainer.getChildren().addAll(CardUtils.createScrapedCardPanes(getCard()));
    }



}
