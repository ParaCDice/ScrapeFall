package csc.paradise.piper.scrapefall.models;

import javafx.scene.Parent;

public record LoadedView<T>(Parent root, T controller) {
}
