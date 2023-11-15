import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.*;

public class SpellCheckerTest {
    private SpellChecker spellChecker;

    @BeforeEach
    void init() {
        // Runs before each test, makes a new SpellChecker object BEFORE EACH test
        spellChecker = new SpellChecker();
    }

    @Test
    public void inDictionary() throws IOException {
        String wordInDict = "aardvark";
        String misSpelled = "aardwol";
        ArrayList<String> dictionary = new ArrayList<String>();
        FileInputStream fileInput = new FileInputStream("engDictionary.txt");
        Scanner scnr = new Scanner(fileInput);
        while (scnr.hasNext()) {
            dictionary.add(scnr.next().trim());
        }
        assertTrue(spellChecker.inDictionary(wordInDict, dictionary));
        assertFalse(spellChecker.inDictionary(misSpelled, dictionary));
    }
}