package flashcard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads flashcards from a text file.
 *
 * <p>File format (UTF-8 text):</p>
 * <pre>
 * # Lines starting with # are comments and are ignored
 * # Each card is defined as:
 * Q: &lt;question text&gt;
 * A: &lt;answer text&gt;
 *
 * # Blank lines between cards are allowed
 * Q: What is 2 + 2?
 * A: 4
 *
 * Q: Capital of Mongolia?
 * A: Ulaanbaatar
 * </pre>
 */
public class CardLoader {

    /**
     * Loads flashcards from the given file path.
     *
     * @param filePath path to the cards file
     * @return list of loaded flashcards
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file format is invalid
     */
    public List<FlashCard> load(String filePath) throws IOException {
        List<FlashCard> cards = new ArrayList<>();
        String currentQuestion = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip comments and blank lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("Q:")) {
                    if (currentQuestion != null) {
                        throw new IllegalArgumentException(
                            "Line " + lineNumber + ": New question before answer for: "
                            + currentQuestion);
                    }
                    currentQuestion = line.substring(2).trim();
                    if (currentQuestion.isEmpty()) {
                        throw new IllegalArgumentException(
                            "Line " + lineNumber + ": Empty question");
                    }

                } else if (line.startsWith("A:")) {
                    if (currentQuestion == null) {
                        throw new IllegalArgumentException(
                            "Line " + lineNumber + ": Answer without a preceding question");
                    }
                    String answer = line.substring(2).trim();
                    if (answer.isEmpty()) {
                        throw new IllegalArgumentException(
                            "Line " + lineNumber + ": Empty answer");
                    }
                    cards.add(new FlashCard(currentQuestion, answer));
                    currentQuestion = null;

                } else {
                    throw new IllegalArgumentException(
                        "Line " + lineNumber + ": Invalid format. Expected 'Q:' or 'A:', got: "
                        + line);
                }
            }
        }

        if (currentQuestion != null) {
            throw new IllegalArgumentException(
                "File ended with a question but no answer: " + currentQuestion);
        }

        if (cards.isEmpty()) {
            throw new IllegalArgumentException("No flashcards found in file: " + filePath);
        }

        return cards;
    }
}
