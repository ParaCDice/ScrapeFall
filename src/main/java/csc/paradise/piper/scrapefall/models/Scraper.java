package csc.paradise.piper.scrapefall.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import net.dzultra.jsa.cards.Card;

import java.math.BigDecimal;
import java.util.*;

public class Scraper {

    private static boolean INITIALIZED = false;

    private static Playwright scraperWright;

    private static void initialize() {
        scraperWright = Playwright.create();
        INITIALIZED = true;
    }

    private static JsonObject oasisJson(Card requestedCard, boolean inStock, int limit, boolean setFilter) {
        if (!INITIALIZED) {
            initialize();
        }
        APIRequestContext request = scraperWright.request().newContext();
        Map<String, Object> requestPayload = new HashMap<>();
        ArrayList<String> tempList = new ArrayList<>();

        tempList.add(String.valueOf(inStock).toLowerCase());
        requestPayload.put("in_stock", tempList.clone());
        tempList.clear();
        requestPayload.put("is_buylist", false);
        requestPayload.put("is_sealed", false);
        tempList.add("en");
        requestPayload.put("language", tempList.clone());
        tempList.clear();
        requestPayload.put("limit", limit);
        if (setFilter) {
            tempList.add(requestedCard.set());
            requestPayload.put("magic_set", tempList.clone());
            tempList.clear();
        }
        requestPayload.put("name", requestedCard.name());
        tempList.add("magic");
        requestPayload.put("product_line", tempList.clone());
        tempList.clear();

        APIResponse inventory = request.post("https://www.oasisgamesslc.com/api/products/search/inventory", RequestOptions.create().setHeader("x-storeid", "f98a01b3154fe3ede1b8f7172a1d2dce").setData(requestPayload));
        return JsonParser.parseString(inventory.text()).getAsJsonObject();
    }

    public static List<ScrapedCard> oasisJsonScraper(JsonObject responseJson){
        List<ScrapedCard> cards = new ArrayList<>();

        for (JsonElement cardElement : responseJson.get("data").getAsJsonArray()) {
            JsonObject card = cardElement.getAsJsonObject();
            String name = card.get("name").getAsString();
            JsonElement priceMax = card.get("sell_price_max");
            JsonElement priceMin = card.get("sell_price_min");
            BigDecimal price;
            if (priceMax != null) {
                price = card.get("sell_price_max").getAsBigDecimal();
            } else if (priceMin != null) {
                price = card.get("sell_price_min").getAsBigDecimal();
            } else {
                price = BigDecimal.ZERO;
            }
            String set = card.get("foreign_obj").getAsJsonObject().get("set_code").getAsString();
            int quantity = card.get("total_quantity").getAsInt();
            String collector_number = card.get("foreign_obj").getAsJsonObject().get("collector_number").getAsString();
            String permaLink = card.get("permalink").getAsString();

            cards.add(new ScrapedCard(name, price, set, quantity, collector_number, permaLink));
        }

        return cards;
    }

    public static List<ScrapedCard> oasisScraper(Card requestedCard, boolean inStock, int limit, boolean setFilter) {
        return oasisJsonScraper(oasisJson(requestedCard, inStock, limit, setFilter));
    }


    public static List<ScrapedCard> oasisScraper(Card requestedCard) {
        return oasisScraper(requestedCard, true, 5, false);
    }

}
