package csc.paradise.piper.scrapefall.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

public class ScrapedCardController {


    @FXML
    public AnchorPane pricePane;
    @FXML
    public Button removeButton;

    @FXML
    public void onRemove(MouseEvent mouseEvent) {
        ((TilePane) removeButton.getParent().getParent()).getChildren().remove(pricePane);
    }
}
