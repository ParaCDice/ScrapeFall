package csc.paradise.piper.scrapefall.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dzultra.jsa.cards.Card;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static csc.paradise.piper.scrapefall.models.TestUtils.minimalCard;
import static org.junit.jupiter.api.Assertions.*;

class ScraperTest {

    @BeforeEach
    void resetScraperStaticState() throws Exception {
        setStaticField("INITIALIZED", false);
        setStaticField("scraperWright", null);
    }

    @AfterEach
    void cleanup() throws Exception {
        setStaticField("INITIALIZED", false);
        setStaticField("scraperWright", null);
    }

    private static void setStaticField(String fieldName, Object value) throws Exception {
        Field field = Scraper.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private JsonObject buildCardJson(String name, String priceMax, String priceMin,
                                     String setCode, int quantity, String collectorNumber,
                                     String permalink) {
        JsonObject card = new JsonObject();
        card.addProperty("name", name);
        if (priceMax != null) {
            card.addProperty("sell_price_max", new BigDecimal(priceMax));
        }
        if (priceMin != null) {
            card.addProperty("sell_price_min", new BigDecimal(priceMin));
        }
        JsonObject foreignObj = new JsonObject();
        foreignObj.addProperty("set_code", setCode);
        foreignObj.addProperty("collector_number", collectorNumber);
        card.add("foreign_obj", foreignObj);
        card.addProperty("total_quantity", quantity);
        card.addProperty("permalink", permalink);
        return card;
    }

    private JsonObject wrapInDataArray(JsonObject... cards) {
        JsonArray array = new JsonArray();
        for (JsonObject c : cards) {
            array.add(c);
        }
        JsonObject root = new JsonObject();
        root.add("data", array);
        return root;
    }

    @Test
    void oasisJsonScraper_usesPriceMaxWhenPresent() {
        JsonObject card = buildCardJson("Teferi's Puzzle Box", "25.25", "25.25", "j22", 1, "801", "5886");
        JsonObject response = wrapInDataArray(card);

        List<ScrapedCard> result = Scraper.oasisJsonScraper(response);

        assertEquals(1, result.size());
        ScrapedCard scraped = result.get(0);
        assertEquals("Teferi's Puzzle Box", scraped.getCardName());
        assertEquals(0, new BigDecimal("25.25").compareTo(scraped.getCardPrice()));
        assertEquals("j22", scraped.getCardSet());
        assertEquals(1, scraped.getQuantity());
        assertEquals("801", scraped.getCollectorNum());
        assertEquals("5886", scraped.getPermaLink());
    }

    @Test
    void oasisJsonScraper_fallsBackToPriceMinWhenMaxAbsent() {
        JsonObject card = buildCardJson("Teferi's Imp", null, "1.35", "mir", 7, "98", "35904");
        JsonObject response = wrapInDataArray(card);

        List<ScrapedCard> result = Scraper.oasisJsonScraper(response);

        assertEquals(1, result.size());
        assertEquals(0, new BigDecimal("1.35").compareTo(result.get(0).getCardPrice()));
    }

    @Test
    void oasisJsonScraper_defaultsToZeroWhenNoPricePresent() {
        JsonObject card = buildCardJson("Pieces of the Puzzle", null, null, "soi", 25, "78", "41828");
        JsonObject response = wrapInDataArray(card);

        List<ScrapedCard> result = Scraper.oasisJsonScraper(response);

        assertEquals(1, result.size());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.get(0).getCardPrice()));
    }

    @Test
    void oasisJsonScraper_parsesMultipleCards() {
        JsonObject card1 = buildCardJson("Teferi's Moat", "25.31", "1.50", "inv", 2, "279", "14942");
        JsonObject card2 = buildCardJson("Oath of Teferi", "1.25", "1.25", "cmm", 5, "934", "94116");
        JsonObject response = wrapInDataArray(card1, card2);

        List<ScrapedCard> result = Scraper.oasisJsonScraper(response);

        assertEquals(2, result.size());
        assertEquals("Teferi's Moat", result.get(0).getCardName());
        assertEquals("Oath of Teferi", result.get(1).getCardName());
    }

    @Test
    void oasisJsonScraper_emptyDataArray_returnsEmptyList() {
        JsonObject response = wrapInDataArray();

        List<ScrapedCard> result = Scraper.oasisJsonScraper(response);

        assertTrue(result.isEmpty());
    }

    @Test
    void oasisJsonScraper_missingDataField_throws() {
        JsonObject response = new JsonObject();

        assertThrows(NullPointerException.class, () -> Scraper.oasisJsonScraper(response));
    }

    @Test
    void scrapedCard_priceAsString_isFormattedCurrency() {
        JsonObject card = buildCardJson("Vexing Puzzlebox", "8.33", null, "clb", 5, "343", "9036");
        JsonObject response = wrapInDataArray(card);

        ScrapedCard result = Scraper.oasisJsonScraper(response).get(0);

        assertNotNull(result.getPriceAsString());
        assertFalse(result.getPriceAsString().isBlank());
    }

    @Test
    void oasisScraper_liveApi_returnsParsedCards() {
        Card requestedCard = minimalCard("Oath of Teferi", "cmm");

        List<ScrapedCard> result = Scraper.oasisScraper(requestedCard, true, 5, false);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}