package csc.paradise.piper.scrapefall.models;

import csc.paradise.piper.scrapefall.ApplicationManager;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class ManaGlyphSkin extends SkinBase<ManaGlyph> {

    // The logic for a regular ManaGlyph


    private final StackPane root = new StackPane();

    public ManaGlyphSkin(ManaGlyph control) {
        super(control);

        getChildren().add(root);
        rebuild();

        // Rebuild whenever any relevant property changes
        control.glyphProperty().addListener((o, ov, nv) -> rebuild());
        control.primaryColorProperty().addListener((o, ov, nv) -> rebuild());
        control.glyphFillProperty().addListener((o, ov, nv) -> rebuild());
        control.diameterProperty().addListener((o, ov, nv) -> rebuild());
        control.secondaryColorProperty().addListener((o, ov, nv) -> rebuild());
        control.secondaryGlyphProperty().addListener((o, ov, nv) -> rebuild());
        control.splitAngleProperty().addListener((o, ov, nv) -> rebuild());
    }

    private void rebuild() {
        ManaGlyph c = getSkinnable();
        root.getChildren().clear();

        double diameter = c.getDiameter() / 1.5;
        double radius = diameter / 2;

        Circle base = new Circle(radius);
        base.setFill(c.getPrimaryColor());

        DropShadow shadow = new DropShadow();
        shadow.setRadius(3);
        shadow.setOffsetX(0.5);
        shadow.setOffsetY(0.5);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        base.setEffect(shadow);

        root.getChildren().add(base);

        if (c.isSplit()) {
            Circle overlay = new Circle(radius);
            overlay.setFill(c.getSecondaryColor());

            double oversize = diameter * 1.5;
            Rectangle clip = new Rectangle(-oversize / 2, -oversize / 2, oversize, oversize);
            clip.setRotate(c.getSplitAngle());
            overlay.setClip(clip);

            root.getChildren().add(overlay);

            double offset = radius * 0.45;
            double glyphSize = diameter * 0.5;

            Text symbolA = buildGlyphText(c.getGlyph(), glyphSize, c.getGlyphFill());
            symbolA.setTranslateX(-offset);
            symbolA.setTranslateY(-offset);

            Text symbolB = buildGlyphText(c.getSecondaryGlyph(), glyphSize, c.getGlyphFill());
            symbolB.setTranslateX(offset);
            symbolB.setTranslateY(offset);

            root.getChildren().addAll(symbolA, symbolB);
        } else {
            Text symbol = buildGlyphText(c.getGlyph(), diameter * 0.8, c.getGlyphFill());
            root.getChildren().add(symbol);
            root.setClip( (c.isHalf() && !c.isSplit()) ? new Rectangle(0, -radius, radius, c.getDiameter()) : null );
        }
    }

    private Text buildGlyphText(String codepoint, double targetSize, Color fill) {
        Text t = new Text(codepoint);
        t.setBoundsType(TextBoundsType.VISUAL);
        t.setFill(fill);

        // Start at the requested size, then measure and shrink to fit if needed
        double fontSize = targetSize;
        t.setFont(Font.font(ApplicationManager.MANA_FONT_FAMILY, fontSize));

        double maxDimension = Math.max(t.getLayoutBounds().getWidth(), t.getLayoutBounds().getHeight());
        if (maxDimension > targetSize) {
            double scale = targetSize / maxDimension;
            fontSize = fontSize * scale;
            t.setFont(Font.font(ApplicationManager.MANA_FONT_FAMILY, fontSize));
        }

        return t;
    }
}