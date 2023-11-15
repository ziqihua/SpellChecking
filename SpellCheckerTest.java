import org.junit.Test;
import java.io.FileInputStream;
import java.io.IOException;
import static org.junit.Assert.*;

public class SpellCheckerTest {
    @Test
    public void testInDict() throws IOException {
        FileInputStream x =  new FileInputStream("engDictionary.txt");
        SpellChecker.setDictFile(x);
        SpellChecker.copyDict();
        assertTrue(SpellChecker.getDict().size() > 0); //check copy dictionary
        assertTrue(SpellChecker.inDictionary("aaron"));  // word in dictionary
        assertFalse(SpellChecker.inDictionary("morbit"));//word not in dictionary
    }
}