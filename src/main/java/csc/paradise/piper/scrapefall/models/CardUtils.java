package csc.paradise.piper.scrapefall.models;

import csc.paradise.piper.scrapefall.ApplicationManager;
import csc.paradise.piper.scrapefall.controllers.ScrapedCardController;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.Language;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardUtils {

    public static List<AnchorPane> createScrapedCardPanes(Card inputCard) {
        List<AnchorPane> allScrapeCardPanes = new ArrayList<>();
        CardRequester cardRequester = new CardRequester(new ScryfallClient("Scrape-Fall"));
        List<ScrapedCard> cards = Scraper.oasisScraper(inputCard);

        for (ScrapedCard scrapedCard : cards) {
            Card card = cardRequester.getCardBySetAndNumber(scrapedCard.cardSet, scrapedCard.collectorNum, Language.EN);

            ApplicationManager.LoadedView<ScrapedCardController> loadedView = ApplicationManager.loadFXML("price-pane.fxml");
            ScrapedCardController ppc = loadedView.controller();

            ppc.setCard(card, scrapedCard.getPriceAsString());

            loadedView.root().setUserData(ppc);
            ppc.setPrice(scrapedCard.cardPrice);
            ppc.setHalfPermaLink(scrapedCard.permaLink);
            allScrapeCardPanes.add((AnchorPane) loadedView.root());
        }

        return allScrapeCardPanes;
    }

    public static Image getCardImage(Card card) {
        try {
            String urlString;
            if (card.card_faces() == null) {
                urlString = Objects.requireNonNull(card.image_uris()).normal().toString();
            } else {
                urlString = Objects.requireNonNull(card.card_faces().getFirst().image_uris()).normal().toString();
            }
            return new Image(urlString, true);
        } catch (NullPointerException e) {
            System.err.println("WARNING: Card (" + card.name() + ") image won't load.");
            return placeholderImage();
        }
    }

    private static Image placeholderImage() {
        IntBuffer intBuffer = IntBuffer.allocate(1);
        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
        PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(1, 1, intBuffer, pixelFormat);
        return new WritableImage(pixelBuffer);
    }

}
