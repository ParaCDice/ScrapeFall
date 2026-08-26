package csc.paradise.piper.scrapefall.controllers;

import csc.paradise.piper.scrapefall.ApplicationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.CardRequester;

public class MenuController {

    @FXML
    public Button continueButton;
    @FXML
    public ChoiceBox<String> choiceBox;
    @FXML
    public Label credits;
    public Label errorLabel;
    public Label titleLabel;

    @FXML
    protected void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList("OasisGames", "TCGPlayer"));
        choiceBox.getSelectionModel().select(0);
    }

    @FXML
    protected void onContinueClick() {
        errorLabel.setText("");
        if (choiceBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("OasisGames")) {
            ApplicationManager.setStoredChoice(choiceBox.getSelectionModel().getSelectedItem());
            ApplicationManager.setApplicationScene("main-view.fxml", choiceBox.getSelectionModel().getSelectedItem());
        } else {
            errorLabel.setText("NOT IMPLEMENTED");
        }
    }
}
