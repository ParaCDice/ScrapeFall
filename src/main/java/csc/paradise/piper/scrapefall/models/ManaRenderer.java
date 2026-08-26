package csc.paradise.piper.scrapefall.models;

import javafx.scene.Node;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManaRenderer {

    static final Pattern glyphRegex = Pattern.compile("\\{([\\w,\\d,½,\\/,∞]+)\\}");

    public static List<Node> renderCardText(String cardText){
        Matcher matcher = glyphRegex.matcher(cardText);
        List<Node> allContent = new ArrayList<>();

        int startString = 0;
        while (matcher.find()) {
            String subString = cardText.substring(startString, matcher.start());
            if(!subString.isEmpty()) {
                allContent.add(new Text(subString));
            }
            ManaGlyph glyph = ManaCostMap.createGlyph(matcher.group(1));
            if (glyph != null) {
                glyph.setTranslateY(glyph.getTranslateY()-4);
                allContent.add(glyph);
            } else {
                allContent.add(new Text("{" + matcher.group(1) + "}"));
            }
            startString = matcher.end();
        }
        allContent.add(new Text(cardText.substring(startString))); //and get the rest! :p

        return allContent;
    }

}
