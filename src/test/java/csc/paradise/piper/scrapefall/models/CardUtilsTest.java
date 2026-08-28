package csc.paradise.piper.scrapefall.models;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class CardUtilsTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        // dont mind me :)
    }


    @Test
    void isCardRealTest_Happy() {
        assertTrue(CardUtils.isCardReal(TestUtils.REAL_DOUBLE_SIDED_CARD));
    }

    @Test
    void isCardRealTest_Negative_BadCard() {
        assertFalse(CardUtils.isCardReal(TestUtils.BAD_CARD));
    }


    @Test
    void isCardRealDoubleSidedTest_Happy() {
        assertTrue(CardUtils.isCardRealDoubleSided(TestUtils.REAL_DOUBLE_SIDED_CARD));
    }

    @Test
    void isCardRealDoubleSidedTest_Negative_SingleSided() {
        assertFalse(CardUtils.isCardRealDoubleSided(TestUtils.SINGLE_SIDED_CARD));
    }

    @Test
    void isCardRealDoubleSidedTest_Negative_FakeDoubleSided() {
        assertFalse(CardUtils.isCardRealDoubleSided(TestUtils.FAKE_DOUBLE_SIDED_CARD));
    }

    @Test
    void isCardRealDoubleSidedTest_Negative_BadCard_Offline() {
        assertFalse(CardUtils.isCardRealDoubleSided(TestUtils.BAD_CARD));
    }

    @Test
    void getCardImageTest_Happy() {
        Image image = CardUtils.getCardImage(TestUtils.REAL_DOUBLE_SIDED_CARD);
        assertNotNull(image);
    }

    @Test
    void getCardImageTest_Negative_FallsBackToPlaceholder_Offline() {
        Image image = CardUtils.getCardImage(TestUtils.BAD_CARD);
        assertNotNull(image);
        assertEquals(1, image.getWidth());
        assertEquals(1, image.getHeight());
    }


    @Test
    void getDoubleCardImageTest_Happy_Front() {
        Image image = CardUtils.getDoubleCardImage(TestUtils.REAL_DOUBLE_SIDED_CARD, true);
        assertNotNull(image);
    }

    @Test
    void getDoubleCardImageTest_Happy_Back() {
        Image image = CardUtils.getDoubleCardImage(TestUtils.REAL_DOUBLE_SIDED_CARD, false);
        assertNotNull(image);
    }

    @Test
    void getDoubleCardImageTest_Negative_FallsBackToPlaceholder_Offline() {
        Image image = CardUtils.getDoubleCardImage(TestUtils.BAD_CARD, true);
        assertNotNull(image);
        assertEquals(1, image.getWidth());
        assertEquals(1, image.getHeight());
    }
}