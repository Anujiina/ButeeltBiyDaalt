package flashcard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Runs an interactive study session for a chosen deck using plain terminal I/O.
 */
public class StudySession {

    private final CliMenu menu;
    private final Deck deck;
    private final CardOrganizer organizer;
    private final int requiredRepetitions;
    private final boolean invertCards;
    private final AchievementTracker achievementTracker;

    /**
     * Creates a StudySession.
     *
     * @param menu                the CLI menu helper
     * @param deck                the deck to study
     * @param organizer           the card ordering strategy
     * @param requiredRepetitions consecutive correct answers required per card
     * @param invertCards         if true, swap question and answer
     */
    public StudySession(CliMenu menu, Deck deck, CardOrganizer organizer,
            int requiredRepetitions, boolean invertCards) {
        this.menu = menu;
        this.deck = deck;
        this.organizer = organizer;
        this.requiredRepetitions = requiredRepetitions;
        this.invertCards = invertCards;
        this.achievementTracker = new AchievementTracker();
    }

    /**
     * Runs the study session until all cards are mastered or the user quits.
     */
    public void run() {
        List<FlashCard> allCards = deck.getCards();
        if (allCards.isEmpty()) {
            menu.message("This deck has no cards! Add some cards first.");
            return;
        }

        System.out.println();
        System.out.println("  Studying: " + deck.getName());
        System.out.println("  Cards: " + allCards.size()
            + "  |  Required correct: " + requiredRepetitions
            + "  |  Inverted: " + invertCards);
        System.out.println("  (Press Enter with empty answer to quit session)");

        int round = 0;

        while (!allCardsMastered(allCards)) {
            round++;
            System.out.println();
            System.out.println("  ====== Round " + round + " ======");

            for (FlashCard card : allCards) {
                card.resetRoundFlag();
            }

            List<FlashCard> remaining = getRemainingCards(allCards);
            List<FlashCard> ordered = organizer.organize(remaining);
            achievementTracker.startRound();

            for (int i = 0; i < ordered.size(); i++) {
                FlashCard card = ordered.get(i);
                String prompt = invertCards ? card.getAnswer() : card.getQuestion();
                String correctAnswer = invertCards ? card.getQuestion() : card.getAnswer();

                System.out.println();
                System.out.println("  Card " + (i + 1) + "/" + ordered.size());
                System.out.println("  Q: " + prompt);
                System.out.print("  Answer: ");

                String userAnswer = menu.readLine();
                if (userAnswer == null || userAnswer.isEmpty()) {
                    System.out.println();
                    System.out.println("  Session ended.");
                    printAchievements(achievementTracker.evaluateRound(allCards));
                    return;
                }

                boolean correct = userAnswer.equalsIgnoreCase(correctAnswer);
                card.recordAttempt(correct);
                achievementTracker.recordAnswer(correct);

                if (correct) {
                    System.out.println("  ✓ Correct! ("
                        + card.getConsecutiveCorrect() + "/" + requiredRepetitions + ")");
                } else {
                    System.out.println("  ✗ Wrong. Correct answer: " + correctAnswer);
                }
            }

            printAchievements(achievementTracker.evaluateRound(allCards));
        }

        System.out.println();
        System.out.println("  *** Congratulations! You mastered all "
            + allCards.size() + " cards in \"" + deck.getName() + "\"! ***");
    }

    private void printAchievements(List<String> achievements) {
        if (!achievements.isEmpty()) {
            System.out.println();
            System.out.println("  >>> Achievements earned:");
            for (String a : achievements) {
                System.out.println("  ★  " + a);
            }
        }
    }

    private List<FlashCard> getRemainingCards(List<FlashCard> all) {
        return all.stream()
            .filter(c -> c.getConsecutiveCorrect() < requiredRepetitions)
            .collect(Collectors.toList());
    }

    private boolean allCardsMastered(List<FlashCard> all) {
        return all.stream().allMatch(c -> c.getConsecutiveCorrect() >= requiredRepetitions);
    }
}
