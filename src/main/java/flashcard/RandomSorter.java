package flashcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Organizes flashcards in a random order each round.
 */
public class RandomSorter implements CardOrganizer {

    /**
     * Returns the cards in a randomly shuffled order.
     *
     * @param cards the list of flashcards to organize
     * @return a new shuffled list of flashcards
     */
    @Override
    public List<FlashCard> organize(List<FlashCard> cards) {
        List<FlashCard> shuffled = new ArrayList<>(cards);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
