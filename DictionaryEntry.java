/* Represents a single dictionary entry consisting of a word, its definition, and an example usage. */
public class DictionaryEntry {

        private String word;
        private String definition;
        private String example;

        // Creates a dictionary entry using values parsed from the source file
        public DictionaryEntry(String word, String definition, String example){
                this.word = word;
                this.definition = definition;
                this.example = example;
        }

        //
        public String getWord(){
                return word;
        }

        //
        public String getDefinition(){
                return definition;
        }
        
        //
        public String getExample(){
                return example; 
        }

}