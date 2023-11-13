import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SpellChecker {
    private static Scanner scnr = new Scanner(System.in);
    private static FileInputStream userFile;
    private static FileOutputStream checkedOutput;
    private static PrintWriter outWriter;
    private static String dictName;
    private static boolean correctKey;
    private static ArrayList<String> copiedDict = new ArrayList<>();
    private static final int tolerance = 2;
    private static final double commonPercent  = 0.5;
    private static final int topN = 4;

    public SpellChecker() {
        // TODO: You can modify the body of this constructor,
        // or you can leave it blank. You must keep the signature, however.
    }

    public static void start() {
        openDict();
        openFile();
        Scanner fileScnr = new Scanner(userFile);
        while (fileScnr.hasNextLine()) {
            Scanner lineScanner = new Scanner(fileScnr.nextLine());
            while (lineScanner.hasNext()) {
                String wordInFile = lineScanner.next();
                if (inDictionary(wordInFile)) {
                    outWriter.print(wordInFile + " ");
                } else {
                    System.out.printf(Util.MISSPELL_NOTIFICATION, wordInFile);
                    WordRecommender wordRecommender = new WordRecommender(dictName);
                    ArrayList<String> wordSuggest = wordRecommender.getWordSuggestions(wordInFile, tolerance, commonPercent, topN);
                    prompt(wordSuggest);
                    correctKey = false;
                    while (!correctKey) {
                        modifyWord(wordInFile, wordSuggest);
                    }
                }
            }
            outWriter.println();
        }
        fileScnr.close();
        outWriter.close();
    }

    public static void openDict () {
        boolean validInput = false;
        do {
            System.out.printf(Util.DICTIONARY_PROMPT);
            String userDictInput = scnr.next();
            try {
                FileInputStream userDict= new FileInputStream(userDictInput);
                Scanner scnDict = new Scanner(userDict).useDelimiter("\n");
                copyDict(scnDict);
                validInput = true;
            } catch (IOException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
                validInput = false;
            } finally {
                dictName = userDictInput;
            }
        } while (!validInput);
        System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, dictName);
    }

    public static void openFile () {
        boolean validInput = false;
        String userFileInput;
        do {
            System.out.printf(Util.FILENAME_PROMPT);
            userFileInput = scnr.next();
            try {
                userFile= new FileInputStream(userFileInput);
                validInput = true;
            } catch (IOException e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
                validInput = false;
            }
        } while (!validInput);

        String outputFileName = userFileInput.substring(0,userFileInput.length()-4) + "_chk.txt";

        try {
            checkedOutput = new FileOutputStream(outputFileName);
        } catch (IOException e) {
            System.out.printf("Error creating checkedOutput file: " + e.getMessage());
        }

        outWriter = new PrintWriter(checkedOutput);
        System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, userFileInput, outputFileName);
    }

    public static void copyDict(Scanner scn) {
        while (scn.hasNext()) {
            copiedDict.add(scn.next().trim());
        }
    }

    public static boolean inDictionary (String word) {
        return copiedDict.contains(word);
    }

    public static void prompt (ArrayList<String> list) {
        int len = list.size();
        if (len > 0) {
            System.out.printf(Util.FOLLOWING_SUGGESTIONS);
            for (int i = 0; i < len; i++) {
                System.out.printf(Util.SUGGESTION_ENTRY, i+1, list.get(i));
            }System.out.printf(Util.THREE_OPTION_PROMPT);
        } else {
            System.out.printf(Util.NO_SUGGESTIONS);
            System.out.printf(Util.TWO_OPTION_PROMPT);
        }
    }

    public static void modifyWord (String word, ArrayList<String> wordSuggest) {
        String userInputKey = scnr.next();
        if (userInputKey.equals("r")) {
            System.out.printf(Util.AUTOMATIC_REPLACEMENT_PROMPT);
            boolean correctNum = false;
            while (!correctNum) {
                int userChoice = scnr.nextInt();
                if (userChoice <= wordSuggest.size()) {
                    outWriter.print(wordSuggest.get(userChoice-1) + " ");
                    correctNum = true;
                    correctKey = true;
                } else {
                    System.out.printf(Util.INVALID_RESPONSE);
                }}
        } else if (userInputKey.equals("a")) {
            outWriter.print(word + " ");
            correctKey = true;
        } else if (userInputKey.equals("t")) {
            System.out.printf(Util.MANUAL_REPLACEMENT_PROMPT);
            String typedWord = scnr.next();
            outWriter.print(typedWord + " ");
            correctKey = true;
        } else {
            System.out.printf(Util.INVALID_RESPONSE);
        }
    }
}