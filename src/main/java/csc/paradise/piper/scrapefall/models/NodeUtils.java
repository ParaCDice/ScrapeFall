package csc.paradise.piper.scrapefall.models;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeUtils {

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    public static boolean isNodeVisible(Node node, ScrollPane scrollPane) {
        Bounds nodeBoundsInScene = node.localToScene(node.getBoundsInLocal());
        Bounds viewportBoundsInScene = scrollPane.localToScene(scrollPane.getViewportBounds());
        return viewportBoundsInScene.intersects(nodeBoundsInScene);
    }


}
