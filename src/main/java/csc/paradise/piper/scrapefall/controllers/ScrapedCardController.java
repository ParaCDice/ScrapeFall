package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.models.CardUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import net.dzultra.jsa.cards.Card;

import java.math.BigDecimal;

public class ScrapedCardController {

    @FXML private Label setLabel;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private ImageView cardImageView;

    @FXML
    public AnchorPane pricePane;
    @FXML
    public Button removeButton;

    public BigDecimal price;

    public void setCard(Card card, String priceText) {
        setLabel.setText(card.set_name());
        nameLabel.setText(card.name());
        priceLabel.setText(priceText);
        cardImageView.setImage(CardUtils.getCardImage(card));
    }

    public void setPrice(BigDecimal price){
        if(price.compareTo(BigDecimal.ZERO) >= 0){
            this.price = price;
        }
    }

    public BigDecimal getPrice(){
        return this.price;
    }

    @FXML
    public void onRemove(MouseEvent mouseEvent) {
        ((TilePane) removeButton.getParent().getParent()).getChildren().remove(pricePane);
    }
}
