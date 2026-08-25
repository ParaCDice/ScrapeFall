package csc.paradise.piper.scrapefall.models;

import javafx.scene.paint.Color;

public record ManaSymbolType(Type type, String glyph, Color color, String glyph2, Color color2) {

    public enum Type {SINGLE, HYBRID, PHYREXIAN, HYBRID_PHYREXIAN, ICON}

    private static final String PHY_SYMBOL = "\uE618"; // ms-h, Phyrexian symbol

    public static ManaSymbolType single(String glyph, Color color) { // all regular magic colors
        return new ManaSymbolType(Type.SINGLE, glyph, color, null, null);
    }

    public static ManaSymbolType icon(String glyph, Color color) { // icons like tap, untap, the acorn... etc!
        return new ManaSymbolType(Type.ICON, glyph, color, null, null);
    }

    public static ManaSymbolType hybrid(String glyphA, Color colorA, String glyphB, Color colorB) { //hybrid {W/G}
        return new ManaSymbolType(Type.HYBRID, glyphA, colorA, glyphB, colorB);
    }

    public static ManaSymbolType phyrexian(Color color) { // {G/P}
        return new ManaSymbolType(Type.PHYREXIAN, PHY_SYMBOL, color, null, null);
    }

    public static ManaSymbolType hybridPhyrexian(Color colorA, Color colorB) { // {W/G/P}
        return new ManaSymbolType(Type.HYBRID_PHYREXIAN, PHY_SYMBOL, colorA, PHY_SYMBOL, colorB);
    }
}