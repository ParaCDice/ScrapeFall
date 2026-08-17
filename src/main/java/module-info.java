module csc.paradise.piper.scrapefall {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires JavaScryfallAPI;
    requires org.apache.commons.text;
    requires playwright;
    requires jdk.compiler;
    requires java.desktop;
    requires com.google.gson;

    opens csc.paradise.piper.scrapefall to javafx.fxml;
    exports csc.paradise.piper.scrapefall;
    exports csc.paradise.piper.scrapefall.controllers;
    opens csc.paradise.piper.scrapefall.controllers to javafx.fxml;
}