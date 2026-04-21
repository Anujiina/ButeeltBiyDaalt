package flashcard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for the flashcard system. */
public class FlashCardTest {

  // ---------------------------------------------------------------
  // FlashCard tests
  // ---------------------------------------------------------------

  @Test
  void testFlashCardInitialState() {
    FlashCard card = new FlashCard("Q", "A");
    assertEquals("Q", card.getQuestion());
    assertEquals("A", card.getAnswer());
    assertEquals(0, card.getTotalAttempts());
    assertEquals(0, card.getCorrectAttempts());
    assertEquals(0, card.getConsecutiveCorrect());
    assertFalse(card.isWrongInLastRound());
  }

  @Test
  void testCorrectAttemptIncrementsAll() {
    FlashCard card = new FlashCard("Q", "A");
    card.recordAttempt(true);
    assertEquals(1, card.getTotalAttempts());
    assertEquals(1, card.getCorrectAttempts());
    assertEquals(1, card.getConsecutiveCorrect());
    assertFalse(card.isWrongInLastRound());
  }

  @Test
  void testWrongAttemptResetsConsecutive() {
    FlashCard card = new FlashCard("Q", "A");
    card.recordAttempt(true);
    card.recordAttempt(true);
    card.recordAttempt(false);
    assertEquals(3, card.getTotalAttempts());
    assertEquals(2, card.getCorrectAttempts());
    assertEquals(0, card.getConsecutiveCorrect());
    assertTrue(card.isWrongInLastRound());
  }

  @Test
  void testResetRoundFlag() {
    FlashCard card = new FlashCard("Q", "A");
    card.recordAttempt(false);
    assertTrue(card.isWrongInLastRound());
    card.resetRoundFlag();
    assertFalse(card.isWrongInLastRound());
  }

  // ---------------------------------------------------------------
  // RandomSorter tests
  // ---------------------------------------------------------------

  @Test
  void testRandomSorterReturnsSameSize() {
    List<FlashCard> cards = makeCards(5);
    RandomSorter sorter = new RandomSorter();
    List<FlashCard> result = sorter.organize(cards);
    assertEquals(cards.size(), result.size());
    assertTrue(result.containsAll(cards));
  }

  // ---------------------------------------------------------------
  // WorstFirstSorter tests
  // ---------------------------------------------------------------

  @Test
  void testWorstFirstSorterOrdersByRatio() {
    FlashCard good = new FlashCard("Good", "G");
    good.recordAttempt(true);
    good.recordAttempt(true);

    FlashCard bad = new FlashCard("Bad", "B");
    bad.recordAttempt(false);
    bad.recordAttempt(false);

    FlashCard mid = new FlashCard("Mid", "M");
    mid.recordAttempt(true);
    mid.recordAttempt(false);

    WorstFirstSorter sorter = new WorstFirstSorter();
    List<FlashCard> result = sorter.organize(Arrays.asList(good, mid, bad));

    assertEquals(bad, result.get(0));
    assertEquals(mid, result.get(1));
    assertEquals(good, result.get(2));
  }

  // ---------------------------------------------------------------
  // RecentMistakesFirstSorter tests
  // ---------------------------------------------------------------

  @Test
  void testRecentMistakesFirstPutsWrongCardsFirst() {
    FlashCard correct = new FlashCard("C", "c");
    FlashCard wrong = new FlashCard("W", "w");
    wrong.recordAttempt(false); // marks wrongInLastRound

    RecentMistakesFirstSorter sorter = new RecentMistakesFirstSorter();
    List<FlashCard> result = sorter.organize(Arrays.asList(correct, wrong));

    assertEquals(wrong, result.get(0));
    assertEquals(correct, result.get(1));
  }

  @Test
  void testRecentMistakesFirstPreservesRelativeOrder() {
    FlashCard w1 = new FlashCard("W1", "w1");
    FlashCard w2 = new FlashCard("W2", "w2");
    FlashCard c1 = new FlashCard("C1", "c1");
    FlashCard c2 = new FlashCard("C2", "c2");
    w1.recordAttempt(false);
    w2.recordAttempt(false);

    RecentMistakesFirstSorter sorter = new RecentMistakesFirstSorter();
    List<FlashCard> result = sorter.organize(Arrays.asList(c1, w1, c2, w2));

    // Wrong cards first, in original relative order
    assertEquals(w2, result.get(0));
    assertEquals(w1, result.get(1));
    // Correct cards after, in original relative order
    assertEquals(c1, result.get(2));
    assertEquals(c2, result.get(3));
  }

  // ---------------------------------------------------------------
  // CardLoader tests
  // ---------------------------------------------------------------

  @Test
  void testCardLoaderParsesValidFile(@TempDir Path tmp) throws IOException {
    Path file = tmp.resolve("cards.txt");
    Files.writeString(
        file,
        "# Comment\n"
            + "Q: What is 2+2?\n"
            + "A: 4\n"
            + "\n"
            + "Q: Capital of Mongolia?\n"
            + "A: Ulaanbaatar\n");

    CardLoader loader = new CardLoader();
    List<FlashCard> cards = loader.load(file.toString());

    assertEquals(2, cards.size());
    assertEquals("What is 2+2?", cards.get(0).getQuestion());
    assertEquals("4", cards.get(0).getAnswer());
    assertEquals("Capital of Mongolia?", cards.get(1).getQuestion());
    assertEquals("Ulaanbaatar", cards.get(1).getAnswer());
  }

  @Test
  void testCardLoaderThrowsOnEmptyFile(@TempDir Path tmp) throws IOException {
    Path file = tmp.resolve("empty.txt");
    Files.writeString(file, "# Only comments\n");

    CardLoader loader = new CardLoader();
    assertThrows(IllegalArgumentException.class, () -> loader.load(file.toString()));
  }

  @Test
  void testCardLoaderThrowsOnAnswerWithoutQuestion(@TempDir Path tmp) throws IOException {
    Path file = tmp.resolve("bad.txt");
    Files.writeString(file, "A: Orphan answer\n");

    CardLoader loader = new CardLoader();
    assertThrows(IllegalArgumentException.class, () -> loader.load(file.toString()));
  }

  // ---------------------------------------------------------------
  // Helper
  // ---------------------------------------------------------------

  private List<FlashCard> makeCards(int count) {
    FlashCard[] arr = new FlashCard[count];
    for (int i = 0; i < count; i++) {
      arr[i] = new FlashCard("Q" + i, "A" + i);
    }
    return Arrays.asList(arr);
  }
}
