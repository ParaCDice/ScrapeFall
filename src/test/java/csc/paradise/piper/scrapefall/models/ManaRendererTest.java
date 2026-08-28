package csc.paradise.piper.scrapefall.models;

import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

class ManaRendererTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
    }

    @Test
    void renderCardTextTest_Happy_PlainTextNoGlyphs() {
        List<Node> result = ManaRenderer.renderCardText("Flying, vigilance");

        assertEquals(1, result.size());
        assertInstanceOf(Text.class, result.get(0));
        assertEquals("Flying, vigilance", ((Text) result.get(0)).getText());
    }

    @Test
    void renderCardTextTest_Happy_SingleKnownGlyph() {
        List<Node> result = ManaRenderer.renderCardText("{1}");

        assertEquals(2, result.size());
        assertInstanceOf(ManaGlyph.class, result.get(0));
        assertInstanceOf(Text.class, result.get(1));
        assertEquals("", ((Text) result.get(1)).getText());
    }

    @Test
    void renderCardTextTest_Happy_TextSurroundingGlyph() {
        List<Node> result = ManaRenderer.renderCardText("Add {1}.");

        assertEquals(3, result.size());
        assertInstanceOf(Text.class, result.get(0));
        assertEquals("Add ", ((Text) result.get(0)).getText());
        assertInstanceOf(ManaGlyph.class, result.get(1));
        assertInstanceOf(Text.class, result.get(2));
        assertEquals(".", ((Text) result.get(2)).getText());
    }

    @Test
    void renderCardTextTest_Happy_MultipleAdjacentGlyphs() {
        List<Node> result = ManaRenderer.renderCardText("{1}{W}");

        assertEquals(3, result.size());
        assertInstanceOf(ManaGlyph.class, result.get(0));
        assertInstanceOf(ManaGlyph.class, result.get(1));
        assertInstanceOf(Text.class, result.get(2));
        assertEquals("", ((Text) result.get(2)).getText());
    }

    @Test
    void renderCardTextTest_Negative_UnknownGlyphFallsBackToLiteralText() {
        List<Node> result = ManaRenderer.renderCardText("{qqq}");

        assertEquals(2, result.size());
        assertInstanceOf(Text.class, result.get(0));
        assertEquals("{qqq}", ((Text) result.get(0)).getText());
    }

    @Test
    void renderCardTextTest_Negative_EmptyString() {
        List<Node> result = ManaRenderer.renderCardText("");

        assertEquals(1, result.size());
        assertInstanceOf(Text.class, result.get(0));
        assertEquals("", ((Text) result.get(0)).getText());
    }

    @Test
    void renderCardTextTest_Happy_GlyphTranslateYIsShiftedByFour() {
        List<Node> result = ManaRenderer.renderCardText("{1}");
        ManaGlyph glyph = (ManaGlyph) result.get(0);

        ManaGlyph freshGlyph = ManaCostMap.createGlyph("1");
        assertNotNull(freshGlyph, "Expected '1' to be a recognized glyph in ManaCostMap");
        assertEquals(freshGlyph.getTranslateY() - 4, glyph.getTranslateY());
    }

    @Test
    void glyphRegexTest_MatchesExpectedGlyphSyntax() {
        Matcher matcher = ManaRenderer.glyphRegex.matcher("{1}{W}{T}{½}{∞} plain text {X/Y}");
        int matchCount = 0;
        while (matcher.find()) {
            matchCount++;
        }
        assertEquals(6, matchCount);
    }
}