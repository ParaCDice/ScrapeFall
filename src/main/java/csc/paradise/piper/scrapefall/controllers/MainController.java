package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import eu.hansolo.tilesfx.Tile;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.CardRequester;

public class MainController {

    @FXML
    public TilePane cardPaneContainer;
    @FXML
    public Pane test;
    @FXML
    public Button testButton;

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
        //PASS TO NEXT SCENE?
    }
}
