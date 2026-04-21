package flashcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Organizes flashcards so that cards answered incorrectly in the previous round appear first, while
 * preserving the relative order within each group.
 *
 * <p>Algorithm: Cards that were wrong in the last round come first (in their original relative
 * order), followed by the rest (also in their original relative order).
 */
public class RecentMistakesFirstSorter implements CardOrganizer {

  /**
   * Organizes cards so recent mistakes appear first. The internal relative order within each group
   * is preserved.
   *
   * @param cards the list of flashcards to organize
   * @return a new list with recent-mistake cards first
   */
  @Override
  public List<FlashCard> organize(List<FlashCard> cards) {
    Stack<FlashCard> mistakes = new Stack<>();
    List<FlashCard> others = new ArrayList<>();

    for (FlashCard card : cards) {
      if (card.isWrongInLastRound()) {
        mistakes.push(card);
      } else {
        others.add(card);
      }
    }

    List<FlashCard> result = new ArrayList<>();
    while (!mistakes.isEmpty()) {
      result.add(mistakes.pop());
    }
    result.addAll(others);
    return result;
  }
}
