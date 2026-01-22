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

        // ===== Phase 1: Data Structures & Temporary State =====

        // Stores all dictionary entries in memory.
        // Key is lowercased word for case-insensitive lookup.
        HashMap<String, DictionaryEntry> dictionary = new HashMap<>();

        // Temporary variables used while parsing the file.
        // These hold data for the "currently-being-built" entry.
        String currentWord = null;
        String currentDefinition = null;
        String currentExample = null;

        // ===== Phase 2: Resource Setup (File + Input) =====
        // BufferedReader -> reads dictionary file
        // Scanner        -> handles all user input
        try (BufferedReader reader = new BufferedReader(new FileReader("dictionary@java.md"));
                Scanner sc = new Scanner(System.in)) {

            // ===== Phase 3: File Parsing =====
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Marker detection:
                // WORD marker defines entry boundaries
                // DEF / EX markers define content inside an entry
                int markerIndex1 = line.indexOf("\\### ");
                int markerIndex2 = line.indexOf("Definition:");
                int markerIndex3 = line.indexOf("Example:");

                // WORD marker encountered → previous entry ends
                if (markerIndex1 != -1) {
                    // Commit the previous entry before starting a new one
                    if (currentWord != null) {
                        DictionaryEntry entry = new DictionaryEntry(currentWord, currentDefinition, currentExample);
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

            // End-of-file commit:
            // The last word will not be followed by another WORD marker,
            // so it must be saved explicitly after the loop.
            if (currentWord != null) {
                DictionaryEntry entry = new DictionaryEntry(currentWord, currentDefinition, currentExample);
                dictionary.put(currentWord.toLowerCase(), entry);
            }

            // ===== Phase 4: User Greeting =====

            System.out.print("Enter your name: ");
            String name = sc.nextLine();
            
            // ANSI formatting for bold greeting (terminal-dependent)
            System.out.println("\u001B[1mHi, " + name + "\u001B[0m");

            // ===== Phase 5: Main Menu Loop =====

            // Controls overall navigation of the application.
            // Runs until the user explicitly exits.
            while (true) {
                System.out.println("\u001B[1;4mMain Menu\u001B[0m");
                System.out.println("For Search option, enter 's'");
                System.out.println("For full dictionary, enter 'f'");
                System.out.println("To exit, enter 'exit'");
                System.out.print("=>");
                String menuOption = sc.nextLine().toLowerCase();

                // ----- Search Mode -----
                if (menuOption.equals("s")) {

                    // Search loop allows repeated lookups
                    // until user returns to menu or exits app.
                    while (true) {
                        System.out.print("Enter a word (or type 'menu'/'exit'): ");
                        String userWord = sc.nextLine().toLowerCase();

                        // Return to main menu
                        if(userWord.equals("menu")){
                            break;
                        }

                        // Exit application completely
                        if (userWord.equals("exit")) {
                            System.out.println("Bye. See you again. Exiting dictionary application");
                            return;
                        }

                        // Dictionary lookup (O(1) average time)
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
                
                // ----- Full Dictionary Mode -----
                else if (menuOption.equals("f")) {
                    System.out.println("FULL DICTIONARY");

                    // Display-only mode:
                    // Shows all words without additional prompts.
                    int count = 1;
                    for(DictionaryEntry entry : dictionary.values()){
                        System.out.println(count + "." + entry.getWord());
                        System.out.println(entry.getDefinition());
                        System.out.println(entry.getExample());
                        System.out.println();
                        System.out.println("------------------");
                        count++;
                    }
                } 
                
                // ----- Exit from Main Menu -----
                else if (menuOption.equals("exit")) {
                    System.out.println("Bye. See you again");
                    break;
                } 
                
                // ----- Invalid Menu Input -----
                else {
                    System.out.println("Invalid input. Try again.");
                }
            }

            System.out.println("Exiting dictionary application");

        } catch (Exception e) {
            // Generic error handler for file I/O or unexpected runtime issues
            System.out.println("Oh no! Something went wrong.");
            e.printStackTrace();
        }

    }
}