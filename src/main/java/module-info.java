module csc180.paradise.piper.scrapefall {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    opens csc180.paradise.piper.scrapefall to javafx.fxml;
    exports csc180.paradise.piper.scrapefall;
}