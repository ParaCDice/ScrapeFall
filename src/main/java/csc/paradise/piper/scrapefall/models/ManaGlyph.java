package csc.paradise.piper.scrapefall.models;

import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

public class ManaGlyph extends Control {

    //regular glyph
    private final StringProperty glyph = new SimpleStringProperty(this, "glyph", "");
    private final ObjectProperty<Color> primaryColor =
            new SimpleObjectProperty<>(this, "primaryColor", Color.web("#f9faf4"));
    private final ObjectProperty<Color> glyphFill =
            new SimpleObjectProperty<>(this, "glyphFill", Color.BLACK);
    private final DoubleProperty diameter = new SimpleDoubleProperty(this, "diameter", 18);

    //split stuff
    private final ObjectProperty<Color> secondaryColor =
            new SimpleObjectProperty<>(this, "secondaryColor", null);
    private final StringProperty secondaryGlyph =
            new SimpleStringProperty(this, "secondaryGlyph", null);
    private final DoubleProperty splitAngle = new SimpleDoubleProperty(this, "splitAngle", 45);

    //half lol
    private final BooleanProperty half = new SimpleBooleanProperty(this, "half", false);

    public ManaGlyph() {
        getStyleClass().setAll("mana-glyph");
    }

    public ManaGlyph(String glyph, Color primaryColor) {
        this();
        setGlyph(glyph);
        setPrimaryColor(primaryColor);
    }

    // Convenient constructor for half-mana symbols :D
    public static ManaGlyph half(String glyphA, Color colorA, String glyphB, Color colorB) {
        ManaGlyph mg = new ManaGlyph(glyphA, colorA);
        mg.setSecondaryGlyph(glyphB);
        mg.setSecondaryColor(colorB);
        return mg;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ManaGlyphSkin(this);
    }

    // --- default property stuff (like any other node in javafx) ---

    public BooleanProperty halfProperty() { return half; }
    public boolean isHalf() { return half.get(); }
    public void setHalf(boolean v) { half.set(v); }

    public StringProperty glyphProperty() { return glyph; }
    public String getGlyph() { return glyph.get(); }
    public void setGlyph(String v) { glyph.set(v); }

    public ObjectProperty<Color> primaryColorProperty() { return primaryColor; }
    public Color getPrimaryColor() { return primaryColor.get(); }
    public void setPrimaryColor(Color v) { primaryColor.set(v); }

    public ObjectProperty<Color> glyphFillProperty() { return glyphFill; }
    public Color getGlyphFill() { return glyphFill.get(); }
    public void setGlyphFill(Color v) { glyphFill.set(v); }

    public DoubleProperty diameterProperty() { return diameter; }
    public double getDiameter() { return diameter.get(); }
    public void setDiameter(double v) { diameter.set(v); }

    public ObjectProperty<Color> secondaryColorProperty() { return secondaryColor; }
    public Color getSecondaryColor() { return secondaryColor.get(); }
    public void setSecondaryColor(Color v) { secondaryColor.set(v); }

    public StringProperty secondaryGlyphProperty() { return secondaryGlyph; }
    public String getSecondaryGlyph() { return secondaryGlyph.get(); }
    public void setSecondaryGlyph(String v) { secondaryGlyph.set(v); }

    public DoubleProperty splitAngleProperty() { return splitAngle; }
    public double getSplitAngle() { return splitAngle.get(); }
    public void setSplitAngle(double v) { splitAngle.set(v); }

    public boolean isSplit() {
        return getSecondaryColor() != null && getSecondaryGlyph() != null;
    }



    private static String describeGlyph(String s) {
        if (s == null) return "null";
        StringBuilder codepoints = new StringBuilder();
        s.codePoints().forEach(cp -> codepoints.append(String.format("U+%04X ", cp)));
        return "\"" + s + "\" [" + codepoints.toString().trim() + "]";
    }

    private static String colorToHex(Color c) {
        if (c == null) return "null";
        return String.format("#%02X%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255),
                (int) Math.round(c.getOpacity() * 255));
    }

    @Override
    public String toString() {
        return "ManaGlyph{" +
                "glyph=" + describeGlyph(getGlyph()) +
                ", primaryColor=" + colorToHex(getPrimaryColor()) +
                ", glyphFill=" + colorToHex(getGlyphFill()) +
                ", diameter=" + getDiameter() +
                ", half=" + isHalf() +
                ", split=" + isSplit() +
                ", secondaryGlyph=" + describeGlyph(getSecondaryGlyph()) +
                ", secondaryColor=" + colorToHex(getSecondaryColor()) +
                ", splitAngle=" + getSplitAngle() +
                '}';
    }
}