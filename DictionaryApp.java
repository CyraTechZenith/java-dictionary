import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

/*
 * DictionaryApp
 * --------------
 * Console-based dictionary application.
 *
 * v1.1 features:
 * - Loads dictionary data from a markdown-style file
 * - Stores entries in memory for fast lookup
 * - Provides menu-driven interaction:
 *   - Search word
 *   - View full dictionary
 *   - Exit
 *
 * Design note:
 * File parsing happens ONCE at startup.
 * All user interaction works on in-memory data only.
 */

public class DictionaryApp {

    public static void main(String[] args) {

        // ===== Phase 1: Load Dictionary Data =====
        HashMap<String, DictionaryEntry> dictionary;

        try {
            dictionary = loadDictionaryData("dictionary@java.md");
        } catch (Exception e) {
            System.out.println("Failed to load dictionary data.");
            e.printStackTrace();
            return;
        }

        // ===== Phase 2: User Interaction =====
        try (Scanner sc = new Scanner(System.in)) {

            greetUser(sc);
            runMainMenu(dictionary, sc);

            System.out.println("Exiting dictionary application");
        }
    }

    // ===== Method: Load Dictionary =====
    // Reads the dictionary file once and builds the in-memory HashMap
    private static HashMap<String, DictionaryEntry> loadDictionaryData(String filename) throws Exception {

        // Stores all dictionary entries in memory.
        // Key is lowercased word for case-insensitive lookup.
        HashMap<String, DictionaryEntry> dictionary = new HashMap<>();

        // Temporary variables used while parsing the file.
        String currentWord = null;
        String currentDefinition = null;
        String currentExample = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Marker detection:
                int markerIndex1 = line.indexOf("\\### ");
                int markerIndex2 = line.indexOf("Definition:");
                int markerIndex3 = line.indexOf("Example:");

                // WORD marker encountered → previous entry ends
                if (markerIndex1 != -1) {

                    // Commit the previous entry before starting a new one
                    if (currentWord != null) {
                        DictionaryEntry entry =
                                new DictionaryEntry(currentWord, currentDefinition, currentExample);
                        dictionary.put(currentWord.toLowerCase(), entry);
                    }

                    // Start a new entry
                    currentWord = line.substring(markerIndex1 + 5).trim();
                    currentDefinition = null;
                    currentExample = null;
                }
                // Definition line belongs to the current word
                else if (markerIndex2 != -1) {
                    currentDefinition = line.substring(markerIndex2 + 11).trim();
                }
                // Example line belongs to the current word
                else if (markerIndex3 != -1) {
                    currentExample = line.substring(markerIndex3 + 8).trim();
                }
            }

            // End-of-file commit
            if (currentWord != null) {
                DictionaryEntry entry =
                        new DictionaryEntry(currentWord, currentDefinition, currentExample);
                dictionary.put(currentWord.toLowerCase(), entry);
            }
        }

        return dictionary;
    }

    // ===== Method: Greet User =====
    private static void greetUser(Scanner sc) {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        // ANSI formatting for bold greeting (terminal-dependent)
        System.out.println("\u001B[1mHi, " + name + "\u001B[0m");
    }

    // ===== Method: Main Menu Controller =====
    private static void runMainMenu(HashMap<String, DictionaryEntry> dictionary, Scanner sc) {

        // Controls overall navigation of the application.
        while (true) {
            printMenu();
            String menuOption = sc.nextLine().toLowerCase();

            if (menuOption.equals("s")) {
                handleSearch(dictionary, sc);
            } else if (menuOption.equals("f")) {
                printFullDictionary(dictionary);
            } else if (menuOption.equals("exit")) {
                System.out.println("Bye. See you again");
                break;
            } else {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    // ===== Method: Print Menu =====
    private static void printMenu() {
        System.out.println("\u001B[1;4mMain Menu\u001B[0m");
        System.out.println("For Search option, enter 's'");
        System.out.println("For full dictionary, enter 'f'");
        System.out.println("To exit, enter 'exit'");
        System.out.print("=> ");
    }

    // ===== Method: Search Mode =====
    private static void handleSearch(HashMap<String, DictionaryEntry> dictionary, Scanner sc) {

        // Search loop allows repeated lookups
        while (true) {
            System.out.print("Enter a word (or type 'menu'/'exit'): ");
            String userWord = sc.nextLine().toLowerCase();

            if (userWord.equals("menu")) {
                return; // back to main menu
            }

            if (userWord.equals("exit")) {
                System.out.println("Bye. See you again.\nExiting dictionary application");
                System.exit(0);
            }

            DictionaryEntry entry = dictionary.get(userWord);
            if (entry != null) {
                System.out.println("WORD: " + entry.getWord());
                System.out.println("DEF: " + entry.getDefinition());
                System.out.println("EX: " + entry.getExample());
            } else {
                System.out.println("Word not found. Try again.");
            }
        }
    }

    // ===== Method: Full Dictionary View =====
    private static void printFullDictionary(HashMap<String, DictionaryEntry> dictionary) {

        System.out.println("FULL DICTIONARY");

        int count = 1;
        for (DictionaryEntry entry : dictionary.values()) {
            System.out.println(count + ". " + entry.getWord());
            System.out.println(entry.getDefinition());
            System.out.println(entry.getExample());
            System.out.println();
            System.out.println("------------------");
            count++;
        }
    }
}
