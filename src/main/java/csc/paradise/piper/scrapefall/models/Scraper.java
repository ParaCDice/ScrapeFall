package csc.paradise.piper.scrapefall.models;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import net.dzultra.jsa.cards.Card;
import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public static List<ScrapedCard> oasisScraper(Card requestedCard,boolean inStock) {
        if (!INITIALIZED){initialize();}
        System.out.println(requestedCard.name());
        List<ScrapedCard> cards = new ArrayList<>();
        Page page = pageBrowser.newPage();
        String url = String.format("https://www.oasisgamesslc.com/shop/magic?name=%s&in_stock=%b&is_sealed=false", StringEscapeUtils.escapeHtml4(requestedCard.name()),inStock);
        page.navigate(url);
        page.waitForLoadState(LoadState.NETWORKIDLE);

        Locator cardContainers = page.locator(".GeneralThumbnail");

        for (int cardIndex = 0; cardIndex < Math.min(5,cardContainers.count()); cardIndex++) {
            Locator cardCNth = cardContainers.nth(cardIndex);
            Locator titleCont = cardCNth.locator(".gtTitleSection");
            Locator priceContainer = cardCNth.locator(".gtInventorySection");
            Locator quantityContainer = cardCNth.locator(".gtQtyWrapper");

            String price = priceContainer.locator(".gtPriceWrapper").allTextContents().stream().map(Objects::toString).collect(Collectors.joining(""));
            int quantity = quantityContainer.allTextContents().isEmpty() ? 4 : Integer.parseInt(quantityContainer.allTextContents().stream().map(Objects::toString).collect(Collectors.joining("")).replaceAll("Qty",""));

            cards.add(new ScrapedCard(titleCont.locator(".gtTitleWrapper").textContent(),
                    new BigDecimal(price.replaceAll(",", "").replaceAll("\\$","").split(" — ")[0]),
                    titleCont.locator(".gtSetWrapper").textContent(),
                    quantity
                    ));
            System.out.println(cards.getLast());
        }

        page.close();
        return cards;
    }

    public static List<ScrapedCard> oasisScraper(Card requestedCard) {
        return oasisScraper(requestedCard,true);
    }

}
