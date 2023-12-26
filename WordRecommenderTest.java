import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
public class WordRecommenderTest {
    static final WordRecommender w1 = new WordRecommender("test");
    static final WordRecommender w2 = new WordRecommender("engDictionary.txt");
    double delta = 0.001;

    @Test
    public void testCommonChar() {
        assertEquals(w1.commonChar("committe","comet"),5);
        assertEquals(w1.commonChar("co","bd"),0);
    }

    @Test
    public void testGetChars() {
        assertEquals(w1.getChars("committe","comet"),6);
        assertEquals(w1.getChars("co","bd"),4);
    }

    @Test
    public void testSimilarity() {
        double actual = w1.getSimilarity("committe","comet");
        assertEquals(actual,5.0/6.0, this.delta);
        actual = w1.getSimilarity("co","bd");
        assertEquals(actual,0, this.delta);
    }

    @Test
    public void testSwap() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Jason");
        list.add("TJ");
        list.add("Dennis");
        list.add("Wang");
        list.add("Apple");
        w1.swap(list,1,2 );
        assertEquals(list.get(1),"Dennis");
        assertEquals(list.get(2),"TJ");
    }

    @Test
    public void testWordSuggestions() {
        ArrayList<String> list;
        list = w2.getWordSuggestions("morbit",2,0.5,4);
        System.out.println(w2.getSuggestedWords());
        int actual = list.size();
        assertEquals(actual,4);
        list = w2.getWordSuggestions("morbit",2,0.8,4);
        System.out.println(w2.getSuggestedWords());
        actual = list.size();
        assertEquals(actual,1);
    }

    @Test
    public void testAllSugggest() {
        ArrayList<String> list = w2.getAllSuggestions("morbit",2,0.5);
        int expect = list.size();
        assertEquals(expect, 574);
    }

    @Test
    public void testSelectionSort() {
        ArrayList<String> list = w2.getAllSuggestions("morbit",2,0.5);
        list = w2.selectionSort(list, "morbit", 100);
        int expect = list.size();
        assertEquals(expect, 574);
        System.out.println(list.indexOf("orbit"));

        ArrayList<String> list2 = w2.getAllSuggestions("automagically",2,0.5);
        list2 = w2.selectionSort(list, "automagically", 4);
        int expect2 = list2.size();
        assertEquals(expect2, 1508);
    }

    @Test
    public void testLeftRight() {
        assertEquals(2.5, w1.getLeftRight("extrapolated","extraordinary"), this.delta);
    }

    @Test
    public void testCountSame() {
        assertEquals(w1.countSame("aghast","gross"),1);
    }

    @Test
    public void testFilterN() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Jason");
        list.add("TJ");
        list.add("Dennis");
        list.add("Wang");
        list.add("Apple");
        w2.wordFilter(list,1);
        int actual = w2.getSuggestedWords().size();
        assertEquals(actual,1);
        w2.wordFilter(list,5);
        actual = w2.getSuggestedWords().size();
        assertEquals(actual,5);
    }
}
