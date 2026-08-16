package csc.paradise.piper.scrapefall.models;

import javafx.scene.layout.AnchorPane;

public class TestPane extends AnchorPane implements Cloneable{
    @Override
    public TestPane clone() {
        try {
            return (TestPane) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
