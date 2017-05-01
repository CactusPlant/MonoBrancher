import com.sun.org.apache.xpath.internal.SourceTree;
import net.moraleboost.mecab.Lattice;
import net.moraleboost.mecab.Node;
import net.moraleboost.mecab.impl.StandardTagger;

import java.util.ArrayList;

/**
 * Created by Cactus on 4/30/2017.
 */
public class wordProcessor {
    //Returns Dictionary Form for word for to retrieve definition
    public static String dictionaryForm(String word) {
        StandardTagger tagger = new StandardTagger("");
        Lattice lattice = tagger.createLattice();
        lattice.setSentence(word);
        tagger.parse(lattice);

        String dictionaryForm = "";

        Node node = lattice.bosNode();
        while (node != null) {
            String feature = node.feature();
            if(!feature.contains("BOS/EOS")) {
                String[] featureArray = feature.split(",");
                dictionaryForm = featureArray[6];
            }

            // Conjugation exceptions that mecab doesn't fix
            if(dictionaryForm.equals("‚æ‚¢") && feature.contains("Œ`—eŽŒ")) {
                dictionaryForm = "‚¢‚¢";
            } else if(dictionaryForm.equals("‚©‚Á‚±‚æ‚¢") && feature.contains("Œ`—eŽŒ")) {
                dictionaryForm = "‚©‚Á‚±‚¢‚¢";
            }

            node = node.next();
        }

        lattice.destroy();
        tagger.destroy();

        return dictionaryForm;
    }
    //Breaks sentence up for processing
    public static ArrayList<String> breakUpSentence(String expression) {
        StandardTagger tagger = new StandardTagger("");
        Lattice lattice = tagger.createLattice();
        lattice.setSentence(expression);
        tagger.parse(lattice);

        ArrayList<String> words = new ArrayList<>();

        Node node = lattice.bosNode();
        while (node != null) {
            String surface = node.surface();
            if(!surface.isEmpty()) {
                words.add(surface);
            }
            node = node.next();
        }

        lattice.destroy();
        tagger.destroy();

        return words;
    }

    public static void main(String[] args){
    }

}
