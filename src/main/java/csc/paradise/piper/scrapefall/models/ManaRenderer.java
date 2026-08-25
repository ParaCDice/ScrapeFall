package csc.paradise.piper.scrapefall.models;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManaRenderer {

    static final Pattern glyphRegex = Pattern.compile("\\{([\\w,\\d,½,\\/,∞]+)\\}");

    public static List<Node> renderOracleText(String otext){
        Matcher matcher = glyphRegex.matcher(otext);
        List<Node> allContent = new ArrayList<>();

        int startString = 0;
        while (matcher.find()) {
            String subString = otext.substring(startString, matcher.start());
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
        allContent.add(new Text(otext.substring(startString))); //and get the rest! :p

        return allContent;
    }

    public static List<Node> renderCostText(String ctext){
        Matcher matcher = glyphRegex.matcher(ctext);
        List<Node> allContent = new ArrayList<>();
        while(matcher.find()){
            ManaGlyph glyph = ManaCostMap.createGlyph(matcher.group(1));
            if(glyph!=null) {
                allContent.add(glyph);
            }else{
                allContent.add(new Text("{"+ matcher.group(1) +"}"));
            }
        }
        return allContent;
    }

}
