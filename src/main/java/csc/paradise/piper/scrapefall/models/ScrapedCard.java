package csc.paradise.piper.scrapefall.models;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class ScrapedCard {

    String cardName;
    String cardSet;
    BigDecimal cardPrice;
    int quantity;

    public ScrapedCard(String cardName, BigDecimal cardPrice, String cardSet, int quantity) {
        setCardName(cardName);
        setCardPrice(cardPrice);
        setCardSet(cardSet);
        setQuantity(quantity);
    }

    public String getCardName() {
        return cardName;
    }

    private void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public BigDecimal getCardPrice() {
        return cardPrice;
    }

    private void setCardPrice(BigDecimal cardPrice) {
        this.cardPrice = cardPrice;
    }

    public String getCardSet() {
        return cardSet;
    }

    private void setCardSet(String cardSet) {
        this.cardSet = cardSet;
    }

    public int getQuantity() {
        return quantity;
    }

    private void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPriceAsString(){
        return NumberFormat.getCurrencyInstance().format(getCardPrice());
    }

    @Override
    public String toString() {
        return STR."ScrapedCard{cardName='\{cardName}', cardSet='\{cardSet}', cardPrice=\{cardPrice}, quantity=\{quantity}}";
    }
}
