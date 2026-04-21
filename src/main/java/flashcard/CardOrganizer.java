package flashcard;

import java.util.List;

/**
 * Interface for organizing (sorting/ordering) flashcards.
 */
public interface CardOrganizer {

    /**
     * Organizes the given list of flashcards and returns them in the desired order.
     *
     * @param cards the list of flashcards to organize
     * @return a new list of flashcards in the organized order
     */
    List<FlashCard> organize(List<FlashCard> cards);
}
