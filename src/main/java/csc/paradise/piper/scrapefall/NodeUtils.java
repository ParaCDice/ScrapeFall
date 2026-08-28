package csc.paradise.piper.scrapefall;

import csc.paradise.piper.scrapefall.models.LoadedView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

    public static <T> LoadedView<T> loadFXML(String fxmlPath){
        try{
            URL url = NodeUtils.class.getResource(fxmlPath);
            if (url == null) {
                url = locateFxmlOnDisk(fxmlPath);
            }
            if (url == null) {
                throw new IOException("Could not resolve FXML resource: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            T controller = loader.getController();

            return new LoadedView<>(root, controller);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("WARNING: FXML for " + fxmlPath + " did not load.");
            return new LoadedView<>((new Pane(new Label("FXML DID NOT LOAD"))), null );
        }
    }

    private static URL locateFxmlOnDisk(String fxmlPath) {
        String relative = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;

        String[] candidateDirs = {
                "target/classes/",
                "target/test-classes/",
                "ScrapeFall/target/classes/",
                "ScrapeFall/target/test-classes/"
        };

        for (String dir : candidateDirs) {
            File f = new File(dir + relative);
            if (f.exists()) {
                try {
                    return f.toURI().toURL();
                } catch (MalformedURLException ignored) {
                    // try next candidate
                }
            }
        }
        return null;
    }


}
