import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SpellChecker {
    private static WordRecommender wordRecommender;
    private static ArrayList<String> wordSuggest;
    private static ArrayList<Integer> wordSuggestNumber;
    private static Scanner scnr = new Scanner(System.in);
    private static Scanner filescnr;
    private static Scanner scnDict; //scan the word in the dictionary
    private static boolean validInput = false;
    private static boolean correctKey;
    private static String userDictInput;
    private static String userFileName;
    private static String outputFileName;
    private static FileInputStream userDict;
    private static FileInputStream userFile;
    private static FileInputStream providedDict;
    private static ArrayList<String> copiedDict;
    private static FileOutputStream checkedOutput;
    private static PrintWriter outWriter;
    private static String dictName;
    private static int tolerance = 2;
    private static double commonPercent  = 0.5;
    private static int topN = 4;

    public SpellChecker() {
        // TODO: You can modify the body of this constructor,
        // or you can leave it blank. You must keep the signature, however.
    }

    public static void start() {
        openDict();
        copyDict(dictName);
        openFile();
        filescnr = new Scanner(userFile);
        while (filescnr.hasNext()) {
            String wordInFile = filescnr.next();
            if (inDictionary(wordInFile)) {
                outWriter.print(wordInFile + " ");
            } else {
                System.out.printf(Util.MISSPELL_NOTIFICATION, wordInFile);
                wordRecommender = new WordRecommender(dictName);
                wordSuggest = wordRecommender.getWordSuggestions(wordInFile, tolerance,commonPercent,topN);
                prompt(wordSuggest);
                correctKey = false;
                while (!correctKey) {
                    modifyWord(wordInFile);
                }
            }
        }
        closeFile();
    }

    public static void openDict () {
        do {
            System.out.println(Util.DICTIONARY_PROMPT);
            userDictInput = scnr.next();
            try {
                userDict= new FileInputStream(userDictInput);
                validInput = true;
            } catch (IOException e) {
                System.out.println(Util.FILE_OPENING_ERROR);
                validInput = false;
            } finally {
                dictName = userDictInput;
            }
        } while (!validInput);
        System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, dictName);
    }

    public static void openFile () {
        do {
            System.out.println(Util.FILENAME_PROMPT);
            String userFileInput = scnr.next();
            try {
                userFile= new FileInputStream(userFileInput);
                validInput = true;
            } catch (IOException e) {
                System.out.println(Util.FILE_OPENING_ERROR);
                validInput = false;
            } finally {
                userFileName = userFileInput;
            }
        } while (!validInput);

        outputFileName = userFileName + "_chk.txt";
        try {
            checkedOutput = new FileOutputStream(outputFileName);
        } catch (IOException e) {
            System.out.println("Error creating checkedOutput file: " + e.getMessage());
        }
        outWriter = new PrintWriter(checkedOutput);
        System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, userFileName, outputFileName);
    }

    public static void copyDict (String file) {
        copiedDict = new ArrayList<String>();
        try {
            providedDict = new FileInputStream(file);
        } catch (IOException e) {
            System.out.println("Error copying dictionary.");
        }
        scnDict = new Scanner(providedDict).useDelimiter("\n");
        while (scnDict.hasNext()) {
            copiedDict.add(scnDict.next().trim());
        }
    }

    public static boolean inDictionary (String word) {
        return copiedDict.contains(word);
    }

    public static void prompt (ArrayList<String> list) {
        wordSuggestNumber = new ArrayList<Integer>();
        int len = list.size();
        if (len > 0) {
            System.out.println(Util.FOLLOWING_SUGGESTIONS);
            for (int i = 0; i < len; i++) {
                System.out.printf(Util.SUGGESTION_ENTRY, i+1, list.get(i));
                wordSuggestNumber.add(i+1);
            }System.out.printf(Util.THREE_OPTION_PROMPT);
        } else {
            System.out.printf(Util.NO_SUGGESTIONS);
            System.out.printf(Util.TWO_OPTION_PROMPT);
        }
    }

    public static void modifyWord (String word) {
        String userInputKey = scnr.next();
        if (userInputKey.equals("r")) {
            System.out.printf(Util.AUTOMATIC_REPLACEMENT_PROMPT);
            boolean correctNum = false;
            while (!correctNum) {
                int userChoice = scnr.nextInt();
                if (wordSuggestNumber.contains(userChoice)) {
                    outWriter.print(wordSuggest.get(userChoice-1) + " ");
                    correctNum = true;
                    correctKey = true;
                } else {
                    System.out.println(Util.INVALID_RESPONSE);
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

    public static void closeFile() {
        filescnr.close();
        scnDict.close();
        outWriter.close();
    }
}