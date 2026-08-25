package csc.paradise.piper.scrapefall.models;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class ManaCostMap {

    public static final Map<String, ManaSymbolType> SYMBOLS = new HashMap<>();

    private static final String[] NUM_GLYPHS = {
            "\uE605","\uE606","\uE607","\uE608","\uE609","\uE60A","\uE60B","\uE60C","\uE60D","\uE60E",
            "\uE60F","\uE610","\uE611","\uE612","\uE613","\uE614","\uE62A","\uE62B","\uE62C","\uE62D","\uE62E"
    };

    static {
        // --- regular glyphs
        SYMBOLS.put("W", ManaSymbolType.single("\uE600", ManaColors.WHITE));
        SYMBOLS.put("U", ManaSymbolType.single("\uE601", ManaColors.BLUE));
        SYMBOLS.put("B", ManaSymbolType.single("\uE602", ManaColors.BLACK));
        SYMBOLS.put("R", ManaSymbolType.single("\uE603", ManaColors.RED));
        SYMBOLS.put("G", ManaSymbolType.single("\uE604", ManaColors.GREEN));
        SYMBOLS.put("C", ManaSymbolType.single("\uE904", ManaColors.COLORLESS));
        SYMBOLS.put("S", ManaSymbolType.single("\uE619", ManaColors.SNOW));

        // --- Generic numbers 0-20
        for (int i = 0; i <= 20; i++) {
            SYMBOLS.put(String.valueOf(i), ManaSymbolType.single(NUM_GLYPHS[i], ManaColors.GENERIC));
        }

        // --- Insane Numbers (except X ig...) (most from unsets)
        SYMBOLS.put("100",      ManaSymbolType.single("\uE900", ManaColors.GENERIC));
        SYMBOLS.put("1000000",  ManaSymbolType.single("\uE901", ManaColors.GENERIC));
        SYMBOLS.put("1/2", ManaSymbolType.single("\uE902", ManaColors.GENERIC));
        SYMBOLS.put("½", ManaSymbolType.single("\uE902", ManaColors.GENERIC)); // "½" lmao
        SYMBOLS.put("∞",        ManaSymbolType.single("\uE903", ManaColors.GENERIC));
        SYMBOLS.put("X",        ManaSymbolType.single("\uE615", ManaColors.GENERIC));
        SYMBOLS.put("Y",        ManaSymbolType.single("\uE616", ManaColors.GENERIC));
        SYMBOLS.put("Z",        ManaSymbolType.single("\uE617", ManaColors.GENERIC));

        // --- Non-mana symbols that still show up in cost/ability text
        SYMBOLS.put("T",     ManaSymbolType.icon("\uE61A", ManaColors.GENERIC)); // tap
        SYMBOLS.put("Q",     ManaSymbolType.icon("\uE61B", ManaColors.GENERIC)); // untap
        SYMBOLS.put("E",     ManaSymbolType.icon("\uE907", ManaColors.GENERIC)); // energy
        SYMBOLS.put("PW",    ManaSymbolType.icon("\uE623", ManaColors.GENERIC)); // planeswalker
        SYMBOLS.put("CHAOS", ManaSymbolType.icon("\uE61D", ManaColors.GENERIC)); // do i need to say it?
        SYMBOLS.put("A",     ManaSymbolType.icon("\uE929", ManaColors.GENERIC)); // acorn
        SYMBOLS.put("TK",    ManaSymbolType.icon("\uE9C4", ManaColors.GENERIC)); // ticket
    }

    public static ManaSymbolType resolve(String symbolText) {
        // Plain, single symbol — no combining involved
        if (!symbolText.contains("/")) {
            return SYMBOLS.get(symbolText);
        }

        String[] parts = symbolText.split("/");

        // Two-way hybrid, e.g. "W/U" or "2/W"
        if (parts.length == 2 && !parts[1].equals("P")) {
            ManaSymbolType firstSymbol = SYMBOLS.get(parts[0]);
            ManaSymbolType secondSymbol = SYMBOLS.get(parts[1]);
            if (firstSymbol == null || secondSymbol == null) return null; // what?
            return ManaSymbolType.hybrid(firstSymbol.glyph(), firstSymbol.color(), secondSymbol.glyph(), secondSymbol.color());
        }

        // Single-color Phyrexian, e.g. "W/P"
        if (parts.length == 2) { //we already know we got P on the second half of the split
            ManaSymbolType manaSymbol  = SYMBOLS.get(parts[0]);
            if (manaSymbol == null) return null;
            return ManaSymbolType.phyrexian(manaSymbol.color());
        }

        // Hybrid Phyrexian, e.g. "B/G/P"
        if (parts.length == 3 && parts[2].equals("P")) {
            ManaSymbolType firstSymbol = SYMBOLS.get(parts[0]);
            ManaSymbolType secondSymbol = SYMBOLS.get(parts[1]);
            if (firstSymbol == null || secondSymbol == null) return null; // who?
            return ManaSymbolType.hybridPhyrexian(firstSymbol.color(), secondSymbol.color());
        }

        return null; // huh?
    }

    public static ManaGlyph createGlyph(String symbol) {
        ManaSymbolType spec = SYMBOLS.get(symbol);
        if (spec == null) return null;

        switch (spec.type()) {
            case HYBRID:
            case HYBRID_PHYREXIAN:
                return ManaGlyph.half(spec.glyph(), spec.color(), spec.glyph2(), spec.color2());
            default:
                ManaGlyph glyph = new ManaGlyph(spec.glyph(), spec.color());
                if (spec.type() == ManaSymbolType.Type.PHYREXIAN) {
                    glyph.setGlyphFill(Color.BLACK);
                }
                return glyph;
        }
    }
}