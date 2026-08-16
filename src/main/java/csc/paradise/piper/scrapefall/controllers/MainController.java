package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import csc.paradise.piper.scrapefall.models.ScrapedCard;
import csc.paradise.piper.scrapefall.models.Scraper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.CardRequester;


import java.util.List;

public class MainController {

    @FXML
    public TilePane cardPaneContainer;
    @FXML
    public Pane test;
    @FXML
    public Button testButton;
    @FXML
    public Button scrapeButton;
    @FXML
    public Button getCardsButton;
    @FXML
    public TilePane pricePaneContainer;

    @FXML
    protected void initialize(){

    }

    @FXML
    protected void getCards(){
        for (int i = 0; i < 3; i++) {
            System.out.println("hi");
            AnchorPane tempPane = (AnchorPane) ApplicationManager.getNodesFromFXML("card-pane.fxml");
            cardPaneContainer.getChildren().add(tempPane);
        }
    }

    @FXML
    protected void onScrapeClick() {
        CardRequester test = new CardRequester(new ScryfallClient("Scrape-Fall"));
        List<ScrapedCard> cards = Scraper.oasisScraper(test.getCardsByQuery("Sol Ring").cards()[1]);
        for(ScrapedCard card : cards){
            AnchorPane tempPane = (AnchorPane) ApplicationManager.getNodesFromFXML("price-pane.fxml");
            for(Node child: tempPane.getChildren()){
                if(child.getClass().equals(Label.class)){
                    if(((Label) child).getText().contains("Set")){
                        ((Label) child).setText(card.getCardSet());
                    }else if (((Label) child).getText().contains("Name")){
                        ((Label) child).setText(card.getCardName());
                    } else if (((Label) child).getText().contains("Price")) {
                        ((Label) child).setText(card.getPriceAsString());
                    }
                } else if (child.getClass().equals(ImageView.class)) {
                    ImageView childImageView = (ImageView) child;
                    //STUFF
                }
            }
            pricePaneContainer.getChildren().add(tempPane);
        }
    }

    @FXML
    protected void onExit(){
        System.exit(0);
    }
}
