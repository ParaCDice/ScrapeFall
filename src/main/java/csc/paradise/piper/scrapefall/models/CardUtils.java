package csc.paradise.piper.scrapefall.models;

import csc.paradise.piper.scrapefall.ApplicationManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.enums.Language;
import net.dzultra.jsa.cards.enums.OrderMode;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardUtils {

    public static List<AnchorPane> getScrapedCardPanes(Card inputCard) {
        List<AnchorPane> allScrapeCardPanes = new ArrayList<>();
        CardRequester cardRequester = new CardRequester(new ScryfallClient("Scrape-Fall"));
        List<ScrapedCard> cards = Scraper.oasisScraper(inputCard);
        for (ScrapedCard scrapedCard : cards) {
            Card card = cardRequester.getCardBySetAndNumber(scrapedCard.cardSet, scrapedCard.collectorNum, Language.EN);
            AnchorPane tempPane = (AnchorPane) ApplicationManager.getNodesFromFXML("price-pane.fxml");
            for (Node child : tempPane.getChildren()) {
                if (child.getClass().equals(Label.class)) {
                    if (((Label) child).getText().contains("Set")) { //CONTAINS IS REGEX!!!!!!!!!!!
                        ((Label) child).setText(card.set_name());
                    } else if (((Label) child).getText().contains("Name")) {
                        ((Label) child).setText(card.name());
                    } else if (((Label) child).getText().contains("Price")) {
                        ((Label) child).setText(scrapedCard.getPriceAsString());
                    }
                } else if (child.getClass().equals(ImageView.class)) {
                    ImageView childImageView = (ImageView) child;
                    childImageView.setImage(CardUtils.getCardImage(card));
                }
            }
            allScrapeCardPanes.add(tempPane);
        }
        return allScrapeCardPanes;
    }

    public static Image getCardImage(Card card) {
        try {
            String urlString;
            if (card.card_faces() == null) {
                urlString = Objects.requireNonNull(card.image_uris()).small().toString();
            } else {
                urlString = Objects.requireNonNull(card.card_faces().getFirst().image_uris()).small().toString();
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

    //-------------------------------------------------------------------------- MANA STUFF ----------------------------


    private static final Map<String, String> MANA_GLYPHS = Map.ofEntries(
            Map.entry("W", "\ue600"),
            Map.entry("U", "\ue601"),
            Map.entry("B", "\ue602"),
            Map.entry("R", "\ue603"),
            Map.entry("G", "\ue604"),
            Map.entry("S", "\uE996"),
            Map.entry("T", "\uE61A"),
            Map.entry("Q", "\uE61B"),
            Map.entry("E", "\uE907"),
            Map.entry("P", "\uEA32"),
            Map.entry("CHAOS", "\uE61D"),
            Map.entry("A", "\uE929"),
            Map.entry("TK", "\uE9C4"),
            Map.entry("X", "\uE615"),
            Map.entry("Y", "\uE616"),
            Map.entry("Z", "\uE617"),
            Map.entry("0", "\uE605"),
            Map.entry("½", "\uE902"),
            Map.entry("1", "\uE606"),
            Map.entry("2", "\uE607"),
            Map.entry("3", "\uE608"),
            Map.entry("4", "\uE609"),
            Map.entry("5", "\uE60a"),
            Map.entry("6", "\uE60b"),
            Map.entry("7", "\uE60c"),
            Map.entry("8", "\uE60d"),
            Map.entry("9", "\uE60e"),
            Map.entry("10", "\uE60f"),
            Map.entry("11", "\uE610"),
            Map.entry("12", "\uE611"),
            Map.entry("13", "\uE612"),
            Map.entry("14", "\uE613"),
            Map.entry("15", "\uE614"),
            Map.entry("16", "\uE62a"),
            Map.entry("17", "\uE62b"),
            Map.entry("18", "\uE62c"),
            Map.entry("19", "\uE62d"),
            Map.entry("20", "\uE62e"),
            Map.entry("100", "\uE900"),
            Map.entry("1000000", "\uE901"),
            Map.entry("∞", "\uE903"),
            Map.entry("H", "\uE618"),
            Map.entry("L", "\uEA2D"),
            Map.entry("D", "\uEA2E")
    );

    private static Color getManaColor(String code) {
        return switch (code) {
            case "W" -> Color.web("#fffbd5");
            case "U" -> Color.web("#aae0fa");
            case "B" -> Color.web("#cbc2bf");
            case "R" -> Color.web("#f9aa8f");
            case "G" -> Color.web("#9bd3ae");
            default -> Color.GRAY;
        };
    }

    public static String toManaGlyphs(String manaCost) {
        Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(manaCost);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(MANA_GLYPHS.getOrDefault(matcher.group(1), "?"));
        }
        return sb.toString();
    }

    public static TextFlow renderOracleText(String oracleText) {
        TextFlow flow = new TextFlow();
        Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(oracleText); //OMG COST REGEX!!

        int lastEnd = 0;
        while (matcher.find()) {
            // plain text before this symbol
            if (matcher.start() > lastEnd) {
                String plain = oracleText.substring(lastEnd, matcher.start());
                Text plainText = new Text(plain);
                plainText.setFont(ApplicationManager.MANA);
                flow.getChildren().add(plainText);
            }

            // the symbol itself
            String symbolCode = matcher.group(1); // e.g. "T", "W", "2"
            String glyph = MANA_GLYPHS.getOrDefault(symbolCode, "?");
            Text symbolText = new Text(glyph);
            symbolText.setFont(ApplicationManager.MANA);
            flow.getChildren().add(symbolText);

            lastEnd = matcher.end();
        }

        // remaining plain text after the last symbol
        if (lastEnd < oracleText.length()) {
            Text plainText = new Text(oracleText.substring(lastEnd));
            plainText.setFont(ApplicationManager.UI_BOLD);
            flow.getChildren().add(plainText);
        }

        return flow;
    }

}
