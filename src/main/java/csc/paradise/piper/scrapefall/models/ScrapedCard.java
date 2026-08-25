package csc.paradise.piper.scrapefall.models;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class ScrapedCard {

    String cardName;
    String cardSet;
    BigDecimal cardPrice;
    int quantity;
    String collectorNum;
    String permaLink;

    public ScrapedCard(String cardName, BigDecimal cardPrice, String cardSet, int quantity, String collectorNumber, String permaLink) {
        setCardName(cardName);
        setCardPrice(cardPrice);
        setCardSet(cardSet);
        setQuantity(quantity);
        setCollectorNum(collectorNumber);
        setPermaLink(permaLink);
    }

    public String getPermaLink() {
        return permaLink;
    }

    private void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
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

    public String getCollectorNum() {
        return collectorNum;
    }

    private void setCollectorNum(String collectorNum) {
        this.collectorNum = collectorNum;
    }

    public String getPriceAsString(){
        return NumberFormat.getCurrencyInstance().format(getCardPrice());
    }

    @Override
    public String toString() {
        return "ScrapedCard{" +
                "cardName='" + cardName + '\'' +
                ", cardSet='" + cardSet + '\'' +
                ", cardPrice=" + cardPrice +
                ", quantity=" + quantity +
                ", collectorNum='" + collectorNum + '\'' +
                '}';
    }
}
