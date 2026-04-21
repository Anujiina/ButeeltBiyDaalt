package flashcard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Organizes flashcards so that the cards with the worst performance appear first.
 * Cards with the lowest correct ratio are shown first.
 */
public class WorstFirstSorter implements CardOrganizer {

    /**
     * Returns cards sorted by ascending correct-answer ratio (worst first).
     * Cards never attempted are treated as 0% correct.
     *
     * @param cards the list of flashcards to organize
     * @return a new list sorted worst-first
     */
    @Override
    public List<FlashCard> organize(List<FlashCard> cards) {
        List<FlashCard> sorted = new ArrayList<>(cards);
        sorted.sort(Comparator.comparingDouble(this::correctRatio));
        return sorted;
    }

    private double correctRatio(FlashCard card) {
        if (card.getTotalAttempts() == 0) {
            return 0.0;
        }
        return (double) card.getCorrectAttempts() / card.getTotalAttempts();
    }
}
