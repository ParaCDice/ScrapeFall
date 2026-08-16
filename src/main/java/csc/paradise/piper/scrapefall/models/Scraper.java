package csc.paradise.piper.scrapefall.models;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import net.dzultra.jsa.cards.Card;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class Scraper {

    private static boolean INITIALIZED = false;

    private static Playwright scraperWright;
    private static Browser pageBrowser;

    private static void initialize() {
        scraperWright = Playwright.create();
        pageBrowser = scraperWright.webkit().launch();
        INITIALIZED = true;
    }

    public static List<Card> oasisScraper(Card requestedCard) {
        if (!INITIALIZED){initialize();}
        List<Card> cards = new ArrayList<>();
        Page page = pageBrowser.newPage();
        String url = String.format("https://www.oasisgamesslc.com/shop/magic?name=%s&in_stock=false", StringEscapeUtils.escapeHtml4(requestedCard.name()));
        page.navigate(url);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        //get cards
        page.close();
        return cards;
    }

}
