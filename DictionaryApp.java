import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

// Loads dictionary data from a file and provides a CLI-based lookup
public class DictionaryApp {
    public static void main(String[] args) {

            HashMap<String, DictionaryEntry> dictionary = new HashMap<>();

            String currentWord = null;
            String currentDefinition = null;
            String currentExample = null;

            // Parses the markdown-style dictionary file using section markers
            try (BufferedReader reader = new BufferedReader(new FileReader("dictionary@java.md"));
                Scanner sc = new Scanner(System.in)){
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Identifies dictionary section markers while parsing each line
                int markerIndex1 = line.indexOf("\\### ");
                int markerIndex2 = line.indexOf("Definition:");
                int markerIndex3 = line.indexOf("Example:");


                if (markerIndex1 != -1) {
                    // Saves the previous word once a new word marker is encountered
                    if(currentWord != null){
                        DictionaryEntry entry = new DictionaryEntry(currentWord, currentDefinition, currentExample);
                        dictionary.put(currentWord.toLowerCase(), entry);
                    }

                    
                    currentWord = line.substring(markerIndex1 + 5).trim();
                    currentDefinition = null;
                    currentExample = null;
                } else if (markerIndex2 != -1) {
                    currentDefinition = line.substring(markerIndex2 + 11).trim();
                } else if (markerIndex3 != -1) {
                    currentExample = line.substring(markerIndex3 + 8).trim();
                }
            }

            // Adds the last parsed entry after file reading completes
            if(currentWord != null){
                DictionaryEntry entry = new DictionaryEntry(currentWord, currentDefinition, currentExample);
                dictionary.put(currentWord.toLowerCase(), entry);
            }

            while(true){
                System.out.print("Enter a word (or type 'exit'): ");
                String userWord = sc.nextLine().toLowerCase();

                if(userWord.equals("exit")){
                    System.out.println("Bye. See you again");
                    break;
                } else{
                    DictionaryEntry entry = dictionary.get(userWord);
                    if(entry != null){
                        System.out.println("WORD: " + entry.getWord());
                        System.out.println("DEF: " + entry.getDefinition()); 
                        System.out.println("EX: " + entry.getExample());
                    } else{
                        System.out.println("Word not found. Try again.");
                    }
                }
    
            }

            System.out.println("Exiting dictionary application");

        } catch (Exception e) {
            System.out.println("Oh no! Something went wrong.");
            e.printStackTrace();
        }

    }
}