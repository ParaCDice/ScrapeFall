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

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.*;

class DoubleCardPaneControllerTest extends ApplicationTest {

    private LoadedView<DoubleCardPaneController> doubleCardPaneControllerLoadedView;

    @Override
    public void start(Stage stage) throws IOException {
        doubleCardPaneControllerLoadedView = NodeUtils.loadFXML("double-card-pane.fxml");
        stage.setScene(new Scene(doubleCardPaneControllerLoadedView.root()));
        stage.show();
        stage.toFront();
    }

    @Test
    void fxmlLoadsWithFrontImageViewPresent() {
        assertTrue(lookup("#cardImageViewFront").tryQuery().isPresent());
    }

    @Test
    void frontFaceIsVisibleByDefaultAfterSettingCard() throws TimeoutException {
        interact(() -> doubleCardPaneControllerLoadedView.controller().setCard(TestUtils.REAL_DOUBLE_SIDED_CARD));
        WaitForAsyncUtils.waitFor(5, TimeUnit.MILLISECONDS,
                (Callable<Boolean>) () -> doubleCardPaneControllerLoadedView.controller().getCard() != null);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#cardImageViewFront", NodeMatchers.isVisible());
    }

    @Test
    void shouldSwitchBackToFrontAfterTwoClicks() throws TimeoutException {
        interact(() -> doubleCardPaneControllerLoadedView.controller().setCard(TestUtils.REAL_DOUBLE_SIDED_CARD));
        WaitForAsyncUtils.waitFor(5, TimeUnit.MILLISECONDS,
                (Callable<Boolean>) () -> doubleCardPaneControllerLoadedView.controller().getCard() != null);

        clickOn("#changeFaceButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#cardImageViewFront", NodeMatchers.isInvisible());

        clickOn("#changeFaceButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#cardImageViewFront", NodeMatchers.isVisible());
    }

    @Test
    void settingCardAgainDoesNotResetFrontFaceVisibility_KnownBehavior() throws TimeoutException {
        interact(() -> doubleCardPaneControllerLoadedView.controller().setCard(TestUtils.REAL_DOUBLE_SIDED_CARD));
        WaitForAsyncUtils.waitFor(5, TimeUnit.MILLISECONDS,
                (Callable<Boolean>) () -> doubleCardPaneControllerLoadedView.controller().getCard() != null);

        clickOn("#changeFaceButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#cardImageViewFront", NodeMatchers.isInvisible());

        interact(() -> doubleCardPaneControllerLoadedView.controller().setCard(TestUtils.REAL_DOUBLE_SIDED_CARD));
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("#cardImageViewFront", NodeMatchers.isInvisible());
    }

    @Test
    void getCardReturnsSameCachedInstanceAfterBeingSet() throws TimeoutException {
        interact(() -> doubleCardPaneControllerLoadedView.controller().setCard(TestUtils.REAL_DOUBLE_SIDED_CARD));
        WaitForAsyncUtils.waitFor(5, TimeUnit.MILLISECONDS,
                (Callable<Boolean>) () -> doubleCardPaneControllerLoadedView.controller().getCard() != null);

        assertSame(
                doubleCardPaneControllerLoadedView.controller().getCard(),
                doubleCardPaneControllerLoadedView.controller().getCard()
        );
    }
}