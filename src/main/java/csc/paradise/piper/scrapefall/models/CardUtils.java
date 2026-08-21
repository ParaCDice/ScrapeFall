package csc.paradise.piper.scrapefall.models;

import csc.paradise.piper.scrapefall.ApplicationManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.Language;
import net.dzultra.jsa.cards.enums.OrderMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardUtils {

    public static List<AnchorPane> getScrapedCardPanes(Card inputCard){
        List<AnchorPane> allScrapeCardPanes = new ArrayList<>();
        CardRequester cardRequester = new CardRequester(new ScryfallClient("Scrape-Fall"));
        List<ScrapedCard> cards = Scraper.oasisScraper(inputCard);
        for (ScrapedCard scrapedCard : cards) {
            Card card = cardRequester.getCardBySetAndNumber(scrapedCard.cardSet,scrapedCard.collectorNum, Language.EN);
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
                        childImageView.setImage(new Image(Objects.requireNonNull(card.image_uris()).small().toURL().openStream()));
                    } catch (IOException ignored) {
                    }
                }
            }
            allScrapeCardPanes.add(tempPane);
        }
        return allScrapeCardPanes;
    }

}
