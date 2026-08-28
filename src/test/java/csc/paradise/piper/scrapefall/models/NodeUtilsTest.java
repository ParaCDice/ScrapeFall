package csc.paradise.piper.scrapefall.models;

import csc.paradise.piper.scrapefall.NodeUtils;
import csc.paradise.piper.scrapefall.models.LoadedView;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeUtilsTest extends ApplicationTest {

    private ScrollPane scrollPane;
    private VBox scrollContent;

    @Override
    public void start(Stage stage) {
        scrollContent = new VBox();
        for (int i = 0; i < 20; i++) {
            Label label = new Label("Item " + i);
            label.setPrefHeight(50);
            label.setMinHeight(50);
            scrollContent.getChildren().add(label);
        }

        scrollPane = new ScrollPane(scrollContent);
        scrollPane.setPrefViewportHeight(200);
        scrollPane.setPrefViewportWidth(200);

        Scene scene = new Scene(scrollPane, 300, 250);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }


    @Test
    void getAllNodesTest_Happy_ReturnsAllDescendantsInDepthFirstOrder() {
        Label labelOne = new Label("One");
        Label labelTwo = new Label("Two");
        Button button = new Button("Click");

        HBox innerBox = new HBox(button);
        VBox vBox = new VBox(labelOne, labelTwo, innerBox);
        AnchorPane root = new AnchorPane(vBox);

        ArrayList<Node> result = NodeUtils.getAllNodes(root);

        assertEquals(5, result.size());
        assertEquals(vBox, result.get(0));
        assertEquals(labelOne, result.get(1));
        assertEquals(labelTwo, result.get(2));
        assertEquals(innerBox, result.get(3));
        assertEquals(button, result.get(4));
    }

    @Test
    void getAllNodesTest_Negative_ParentWithNoChildrenReturnsEmptyList() {
        AnchorPane root = new AnchorPane();

        ArrayList<Node> result = NodeUtils.getAllNodes(root);

        assertTrue(result.isEmpty());
    }


    @Test
    void isNodeVisibleTest_Happy_NodeInsideViewportIsVisible() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();

        Node firstItem = scrollContent.getChildren().get(0);
        AtomicBoolean visible = new AtomicBoolean(false);
        interact(() -> visible.set(NodeUtils.isNodeVisible(firstItem, scrollPane)));

        assertTrue(visible.get());
    }

    @Test
    void isNodeVisibleTest_Negative_NodeFarBelowViewportIsNotVisible() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();

        Node farItem = scrollContent.getChildren().get(19);
        AtomicBoolean visible = new AtomicBoolean(true);
        interact(() -> visible.set(NodeUtils.isNodeVisible(farItem, scrollPane)));

        assertFalse(visible.get());
    }


    @Test
    void loadFXMLTest_Happy_LoadsRealFXMLWithController() {
        LoadedView<?> loadedView = NodeUtils.loadFXML("double-card-pane.fxml");

        assertNotNull(loadedView.root());
        assertNotNull(loadedView.controller());
    }

    @Test
    void loadFXMLTest_Negative_MissingFileReturnsPlaceholderPane() {
        LoadedView<?> loadedView = NodeUtils.loadFXML("this-file-does-not-exist.fxml");

        assertNull(loadedView.controller());
        assertInstanceOf(Pane.class, loadedView.root());

        Pane placeholderPane = (Pane) loadedView.root();
        assertEquals(1, placeholderPane.getChildren().size());
        assertInstanceOf(Label.class, placeholderPane.getChildren().get(0));
        assertEquals("FXML DID NOT LOAD", ((Label) placeholderPane.getChildren().get(0)).getText());
    }
}