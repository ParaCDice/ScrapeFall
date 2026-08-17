package csc.paradise.piper.scrapefall.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.RequestOptions;
import net.dzultra.jsa.cards.Card;
import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Scraper {

    private static boolean INITIALIZED = false;

    private static Playwright scraperWright;
    private static Browser pageBrowser;

    private static void initialize() {
        scraperWright = Playwright.create();
        pageBrowser = scraperWright.webkit().launch();
        INITIALIZED = true;
    }

    public static List<ScrapedCard> oasisScraper(Card requestedCard, boolean inStock, int limit, boolean setFilter){
        if (!INITIALIZED){initialize();}
        List<ScrapedCard> cards = new ArrayList<>();
        APIRequestContext request = scraperWright.request().newContext();
        Map<String, Object> requestPayload = new HashMap<>();
        ArrayList<String> tempList = new ArrayList<>();

        tempList.add(String.valueOf(inStock).toLowerCase());
        requestPayload.put("in_stock", tempList.clone());
        tempList.clear();
        requestPayload.put("is_buylist",false);
        requestPayload.put("is_sealed",false);
        tempList.add("en");
        requestPayload.put("language",tempList.clone());
        tempList.clear();
        requestPayload.put("limit",limit);
        if(setFilter){
            tempList.add(requestedCard.set());
            requestPayload.put("magic_set",tempList.clone());
            tempList.clear();
        }
        requestPayload.put("name",requestedCard.name());
        tempList.add("magic");
        requestPayload.put("product_line",tempList.clone());
        tempList.clear();

        APIResponse inventory = request.post("https://www.oasisgamesslc.com/api/products/search/inventory", RequestOptions.create().setHeader("x-storeid","f98a01b3154fe3ede1b8f7172a1d2dce").setData(requestPayload));
        JsonObject responseJson = JsonParser.parseString(inventory.text()).getAsJsonObject();

        for (JsonElement cardElement : responseJson.get("data").getAsJsonArray()){
            JsonObject card = cardElement.getAsJsonObject();
            String name = card.get("name").getAsString();
            BigDecimal price = card.get("sell_price_max").getAsBigDecimal();
            String set = card.get("foreign_obj").getAsJsonObject().get("set_code").getAsString();
            int quantity = card.get("total_quantity").getAsInt();

            cards.add(new ScrapedCard(name,price,set,quantity));
        }

        return cards;
    }


    public static List<ScrapedCard> oasisScraper(Card requestedCard) {
        return oasisScraper(requestedCard,true,5,false);
    }

}
