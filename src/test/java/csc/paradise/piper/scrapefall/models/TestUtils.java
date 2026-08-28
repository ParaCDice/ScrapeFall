package csc.paradise.piper.scrapefall.models;

import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardRequester;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    private static final ScryfallClient CLIENT = new ScryfallClient("ScrapeFallTest/0.0.1");

    public static final Card SINGLE_SIDED_CARD = new CardRequester(CLIENT).getCardByName("Demonic Tutor", "CMM", true);
    public static final Card FAKE_DOUBLE_SIDED_CARD = new CardRequester(CLIENT).getCardByName("Emeritus of Woe", "SOS", true);
    public static final Card REAL_DOUBLE_SIDED_CARD = new CardRequester(CLIENT).getCardByName("Birgi, God of Storytelling", "KHM", true);

    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final URI DEFAULT_URI = URI.create("");

    public static final Card BAD_CARD = new Card(
            // -------- Core Card Fields --------
            null,
            DEFAULT_UUID,
            "",
            null,
            null,
            null, null, null, null, null,
            "",
            "",
            null,
            DEFAULT_URI,
            DEFAULT_URI,
            DEFAULT_URI,
            DEFAULT_URI,

            // -------- Gameplay Fields --------
            null,
            0.0f,
            List.of(""),
            null, null, null, null, null, null,
            List.of(""),
            new Card.Legalities("","","","","","","","","","","","","","","","","","","","",""),
            null, null, null,
            "",
            null, null, null, null,
            true,
            null,
            "",

            // -------- Print Fields --------
            null,
            List.of(DEFAULT_UUID),
            null,
            true,
            "",
            DEFAULT_UUID,
            "",
            null,
            true,
            List.of(""),
            null, null, null,
            "",
            true,
            List.of(""),
            true,
            null,
            "",
            null,
            true,
            new Card.Prices(null,null,null,null,null,null,null),
            null, null, null,
            true,
            null, null,
            "",
            new Card.RelatedURIs(null,null,null,null),
            new Date(),
            true,
            DEFAULT_URI,
            "",
            DEFAULT_URI,
            "",
            DEFAULT_URI,
            "",
            DEFAULT_UUID,
            true,
            true,
            true,
            null, null, null, null,
            // -------- Card Face Field --------
            List.of(new Card.CardFace(null,null,null,null,null,null,null,null,null,null,null,
                    "",
                    "",
                    "",
                    null,null,null,null,null,null,null,null,null
                    ))
    );

    static Card minimalCard(String name, String setCode) {
        return new Card(// -------- Core Card Fields --------
                null,
                DEFAULT_UUID,
                "",
                null,
                null,
                null, null, null, null, null,
                "",
                "",
                null,
                DEFAULT_URI,
                DEFAULT_URI,
                DEFAULT_URI,
                DEFAULT_URI,

                // -------- Gameplay Fields --------
                null,
                0.0f,
                List.of(""),
                null, null, null, null, null, null,
                List.of(""),
                new Card.Legalities("","","","","","","","","","","","","","","","","","","","",""),
                null, null, null,
                name,
                null, null, null, null,
                true,
                null,
                "",

                // -------- Print Fields --------
                null,
                List.of(DEFAULT_UUID),
                null,
                true,
                "",
                DEFAULT_UUID,
                "",
                null,
                true,
                List.of(""),
                null, null, null,
                "",
                true,
                List.of(""),
                true,
                null,
                "",
                null,
                true,
                new Card.Prices(null,null,null,null,null,null,null),
                null, null, null,
                true,
                null, null,
                "",
                new Card.RelatedURIs(null,null,null,null),
                new Date(),
                true,
                DEFAULT_URI,
                setCode,
                DEFAULT_URI,
                "",
                DEFAULT_URI,
                "",
                DEFAULT_UUID,
                true,
                true,
                true,
                null, null, null, null,
                // -------- Card Face Field --------
                List.of(new Card.CardFace(null,null,null,null,null,null,null,null,null,null,null,
                        "",
                        "",
                        "",
                        null,null,null,null,null,null,null,null,null
                )));
    }

}
