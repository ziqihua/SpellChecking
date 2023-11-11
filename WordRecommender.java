import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordRecommender {
    /**
     * An ArrayList to store all the word in the dictionary.
     */
    private ArrayList<String> dictionary = new ArrayList<>();
    /**
     * An ArrayList to store the final suggest words.
     */
    private ArrayList<String> wordSuggests = new ArrayList<>();
    /**
     * An ArrayList to store all the possible word suggestions.
     */
    private ArrayList<String> allSuggestions = new ArrayList<>();

    /**
     * Creates a new instance of the WordRecommender class.
     * Initialize the dictionary file.
     * @param dictionaryFile
     */
    public WordRecommender(String dictionaryFile) {
        openDict(dictionaryFile);
    }

    /**
     * Open a dictionary file.
     * Read its content line by line
     * Copy the internal dictionary list with the trimmed versions of the words.
     * @param file the input dictionary file's name
     * @throws IOException if there is an error while opening the file.
     */
    public void openDict(String file) {
        try {
            FileInputStream dictByteStream = new FileInputStream(file);
            Scanner scnDict = new Scanner(dictByteStream).useDelimiter("\n");
            while (scnDict.hasNext()) {
                dictionary.add(scnDict.next().trim());
            }
        } catch (IOException e) {
        }
    }

    /**
     * Generates word suggestions based on the input word, tolerance level,
     * common percent threshold, and the desired number of top suggestions.
     *
     * @param word           The input word for which suggestions are sought.
     * @param tolerance      The allowed tolerance for character differences.
     * @param commonPercent  The allowed percentage for common character sequences.
     * @param topN           The number of top suggestions to retrieve.
     * @return               An ArrayList containing word suggestions.
     */
    public ArrayList<String> getWordSuggestions(String word, int tolerance, double commonPercent, int topN) {
        allSuggestions = getAllSuggestions(word, tolerance, commonPercent);
        if (allSuggestions.size() <= topN) {
            wordSuggests = allSuggestions;
            return wordSuggests;
        }
        allSuggestions = selectionSort(allSuggestions, word, topN);
        wordFilter(allSuggestions, topN);
        return wordSuggests;
    }

    /**
     * Collect all the words that satisfy the given requirements.
     * @param word the misspelled word.
     * @param tolerance the difference in length between the misspelled word and candidate word.
     * @param commonPercent the common percentage of the misspelled word to a candidate word.
     * @return An ArrayList of all the suggested word.
     */
    public ArrayList<String> getAllSuggestions(String word, int tolerance, double commonPercent) {
        allSuggestions.clear();
        for (int i = 0; i < dictionary.size(); i++) {
            String s =  dictionary.get(i);
            int dif = Math.abs(s.length() - word.length());
            double percent = getSimilarity(word, s);
            if (dif <= tolerance && percent >= commonPercent) {
                allSuggestions.add(s);
            }
        }
        return allSuggestions;
    }

    /**
     * Perform a selection sort on the given words list
     * Find the top n words with the highest score.
     * @param list ArrayList of all suggested words.
     * @param word misspelled word.
     * @param n the number of top words to find.
     * @return An ArrayList sorted in descending order based on scores.
     */
    public ArrayList<String> selectionSort(ArrayList<String> list, String word, int n) {
        for (int i = 0; i < n; i++) {
            String s = list.get(i);
            double score = getLeftRight(s, word);
            int maxPos = i;
            for (int j = i + 1; j < list.size(); j++) {
                String s2 = list.get(j);
                double score2 = getLeftRight(s2, word);
                if (score2 > score) {
                    maxPos = j;
                    score = score2;
                }
            }
            swap(list, i, maxPos);
        }
        return list;
    }

    /**
     * Swap the word at the specified positions in the given word list.
     * @param list ArrayList of all suggested words.
     * @param i the index of the first element.
     * @param j the index of the second element.
     */
    public void swap(ArrayList<String> list, int i, int j) {
        String s1 = list.get(i);
        String s2 = list.get(j);
        list.set(i, s2);
        list.set(j, s1);
    }

    /**
     * Filter the ArrayList of suggested words to retain the top N words.
     * @param list ArrayList of all suggested words.
     * @param n the number of words to filter out.
     */
    public void wordFilter(ArrayList<String> list, int n) {
        wordSuggests.clear();
        for (int i = 0; i < n; i++) {
            wordSuggests.add(list.get(i));
        }
    }

    /**
     * Calculate the similarity in rank from both left to right and right to left directions.
     * @param word1 the first word.
     * @param word2 the second word.
     * @return the ranked score between the two words.
     */
    public double getLeftRight(String word1, String word2) {
        int leftCommon = countSame(word1, word2);
        String word1Reverse = new StringBuilder(word1).reverse().toString();
        String word2Reverse = new StringBuilder(word2).reverse().toString();
        int rightCommon = countSame(word1Reverse, word2Reverse);
        int totalCommon = leftCommon + rightCommon;
        return (double) totalCommon / 2;
    }

    /**
     * Calculate the number of overlapping characters.
     * @param word1 the first word.
     * @param word2 the second word.
     * @return the number of the same characters.
     */
    public int countSame(String word1, String word2) {
        int len = word1.length();
        int count = 0;
        if (word1.length() > word2.length()) {
            len = word2.length();
        }
        for (int i = 0; i < len; i++) {
            if (word1.charAt(i) == word2.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the similarity between two words based on the number of common characters.
     * @param word1 the first word.
     * @param word2 the second word.
     * @return the similarity score between the two words.
     */
    public double getSimilarity(String word1, String word2) {
        int numerator = commonChar(word1, word2);
        int denominator = getChars(word1, word2);
      return (double) numerator / denominator;
    }

    /**
     * Calculate the distinct number of common characters between two words.
     * @param word1 the first word.
     * @param word2 the second word.
     * @return the number of the same characters without duplicates.
     */
    public int commonChar(String word1, String word2) {
        List<Character> intersect = new ArrayList<>();
        intersect.clear();
        for (char c: word1.toCharArray()) {
            for (char i: word2.toCharArray()) {
                if (c == i && !intersect.contains(c)) {
                    intersect.add(c);
                }
            }
        }
        return intersect.size();
    }

    /**
     * Calculates the total number of distinct characters from the given two words.
     *
     * @param word1 The first word.
     * @param word2 The second word.
     * @return The count of number of distinct characters in the combination of both words.
     */
    public int getChars(String word1, String word2) {
        List<Character> chars = new ArrayList<>();
        for (char c: (word1 + word2).toCharArray()) {
            if (!chars.contains(c)) {
                chars.add(c);
            }
        }
        return chars.size();
    }

    /**
     * Retrieves the list of word suggestions generated by teh WordRecommender.
     * @return an ArrayList of suggested words.
     */
    public ArrayList<String> getSuggestedWords() {
        return wordSuggests;
    }
  }
