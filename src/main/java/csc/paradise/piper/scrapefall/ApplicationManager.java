package csc.paradise.piper.scrapefall;

import com.microsoft.playwright.Browser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationManager extends Application {

    static final double DEFAULT_WINDOW_WIDTH = 600;
    static final double DEFAULT_WINDOW_HEIGHT = 800;

    static Stage appStage;

    static String storedChoice;

    static boolean browserClosed = true;

    static Browser scrapeBrowser;

    public static final String MANA_FONT_FAMILY;

    static {
        Font loaded = Font.loadFont(
                ApplicationManager.class.getResourceAsStream("/csc/paradise/piper/scrapefall/assets/fonts/mana.ttf"),
                16
        );
        MANA_FONT_FAMILY = (loaded != null) ? loaded.getFamily() : "Serif"; // fallback family
    }

    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        stage.setResizable(false);
        stage.setFullScreen(false);

        setApplicationScene("menu-view.fxml","Menu");
    }



    public static void setScrapeBrowser(Browser setBrowser){
        scrapeBrowser = setBrowser;
        browserClosed = scrapeBrowser.isConnected();
    }

    @Override
    public void stop(){
        if(!browserClosed && scrapeBrowser!=null){
            scrapeBrowser.close();
            browserClosed=true;
        }
    }

    public static void setApplicationScene(String fxmlPath, String sceneTitle){
        Scene scene = getSceneFromFXML(fxmlPath);
        appStage.setTitle(sceneTitle);
        appStage.setScene(scene);
        appStage.show();
    }

    public static void setStoredChoice(String choice){
        storedChoice = choice;
    }

    public static String getStoredChoice(){
        return storedChoice;
    }

    public static Node getNodesFromFXML(String fxmlPath){
        try {
            FXMLLoader loader = new FXMLLoader(ApplicationManager.class.getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            return new Pane(new Label("FXML DIDN'T LOAD"));
        }
    }

    private static Scene getSceneFromFXML(String fxmlPath){
        try {
            return new Scene((new FXMLLoader(ApplicationManager.class.getResource(fxmlPath))).load());
        } catch (IOException e) {
            return new Scene(new Pane(new Label("FXML DIDN'T LOAD")),DEFAULT_WINDOW_WIDTH,DEFAULT_WINDOW_HEIGHT);
        }
    }

    public record LoadedView<T>(Parent root, T controller) {
    }


    public static <T> LoadedView<T> loadFXML(String fxmlPath){
        try{
            FXMLLoader loader = new FXMLLoader(ApplicationManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            return new LoadedView<>(root, controller);

        } catch (IOException e) {
            throw new RuntimeException("failed to load fxml: " + fxmlPath, e);
        }
    }
}
