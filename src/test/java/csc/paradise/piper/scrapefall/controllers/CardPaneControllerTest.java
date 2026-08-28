package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.NodeUtils;
import csc.paradise.piper.scrapefall.models.LoadedView;
import csc.paradise.piper.scrapefall.models.TestUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.*;

class CardPaneControllerTest extends ApplicationTest {


    private LoadedView<CardPaneController> cardPaneLoadedView;

    @Override
    public void start(Stage stage) throws IOException {
        cardPaneLoadedView = NodeUtils.loadFXML("card-pane.fxml");
        stage.setScene(new Scene(cardPaneLoadedView.root()));
        stage.show();
        stage.toFront();
    }

    @Test
    void setAndGetCardTest_Happy_One() {
        interact(() -> cardPaneLoadedView.controller().setCard(TestUtils.SINGLE_SIDED_CARD));
        assertEquals(TestUtils.SINGLE_SIDED_CARD, cardPaneLoadedView.controller().getCard());
    }

    @Test
    void setAndGetCardTest_Happy_Two() {
        interact(() -> cardPaneLoadedView.controller().setCard(TestUtils.FAKE_DOUBLE_SIDED_CARD));
        assertEquals(TestUtils.FAKE_DOUBLE_SIDED_CARD, cardPaneLoadedView.controller().getCard());
    }

    @Test
    void setAndGetCardTest_Negative_DoubleSidedCard() {
        AtomicBoolean thrown = new AtomicBoolean(false);
        interact(() -> {
            try {
                cardPaneLoadedView.controller().setCard(TestUtils.REAL_DOUBLE_SIDED_CARD);
            }catch (IllegalArgumentException ignored){
                thrown.set(true);
            }
        });
        assertTrue(thrown.get());
    }

    @Test
    void setAndGetCardTest_Negative_InvalidSet() {
        AtomicBoolean thrown = new AtomicBoolean(false);
            interact(() -> {
                try {
                    cardPaneLoadedView.controller().setCard(TestUtils.BAD_CARD);
                }catch (IllegalArgumentException ignored){
                    thrown.set(true);
                }
            });
        assertTrue(thrown.get());
    }

    @Test
    void setAndGetCardTest_Negative_NullSet() {
        AtomicBoolean thrown = new AtomicBoolean(false);
        interact(() -> {
            try {
                cardPaneLoadedView.controller().setCard(null);
            }catch (NullPointerException ignored){
                thrown.set(true);
            }
        });
        assertTrue(thrown.get());
    }

    @Test
    public void shouldOpenRulingsTest() throws TimeoutException {
        interact(() -> cardPaneLoadedView.controller().setCard(TestUtils.SINGLE_SIDED_CARD));
        WaitForAsyncUtils.waitFor(5, TimeUnit.MILLISECONDS,
                (Callable<Boolean>) () -> cardPaneLoadedView.controller().getCard() != null);
        clickOn("#rulingsButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".dialog-pane", NodeMatchers.isVisible());
    }
}