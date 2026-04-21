package flashcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages a flashcard practice session including rounds, repetitions, and achievements.
 */
public class FlashCardSession {

    private final List<FlashCard> cards;
    private final CardOrganizer organizer;
    private final int requiredRepetitions;
    private final boolean invertCards;
    private final AchievementTracker achievementTracker;
    private final Scanner scanner;

    /**
     * Creates a new flashcard session.
     *
     * @param cards               the flashcards to study
     * @param organizer           the card organizer to use
     * @param requiredRepetitions number of correct answers required per card
     * @param invertCards         if true, show answer as prompt and question as answer
     */
    public FlashCardSession(
            List<FlashCard> cards,
            CardOrganizer organizer,
            int requiredRepetitions,
            boolean invertCards) {
        this.cards = new ArrayList<>(cards);
        this.organizer = organizer;
        this.requiredRepetitions = requiredRepetitions;
        this.invertCards = invertCards;
        this.achievementTracker = new AchievementTracker();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Runs the flashcard session until all cards meet the repetition requirement.
     */
    public void run() {
        System.out.println("=== Flashcard Session Started ===");
        System.out.println("Cards: " + cards.size()
            + " | Required correct: " + requiredRepetitions
            + " | Inverted: " + invertCards);
        System.out.println("Type your answer and press Enter. Type 'quit' to exit.\n");

        int roundNumber = 0;

        while (!allCardsMastered()) {
            roundNumber++;
            System.out.println("--- Round " + roundNumber + " ---");

            // Reset round flags
            for (FlashCard card : cards) {
                card.resetRoundFlag();
            }

            // Get cards still needing repetitions
            List<FlashCard> remaining = getRemainingCards();

            // Organize them
            List<FlashCard> ordered = organizer.organize(remaining);

            // Start achievement tracking for this round
            achievementTracker.startRound();

            // Practice each card in the ordered list
            for (FlashCard card : ordered) {
                if (!practiceCard(card)) {
                    System.out.println("\nSession ended by user.");
                    printAchievements(achievementTracker.evaluateRound(cards));
                    return;
                }
            }

            // Evaluate achievements at end of round
            List<String> earned = achievementTracker.evaluateRound(cards);
            printAchievements(earned);

            System.out.println();
        }

        System.out.println("=== Congratulations! You have mastered all " + cards.size() + " cards! ===");
    }

    /**
     * Practices a single card, prompting the user and recording the result.
     *
     * @param card the card to practice
     * @return false if the user wants to quit, true otherwise
     */
    private boolean practiceCard(FlashCard card) {
        String prompt = invertCards ? card.getAnswer() : card.getQuestion();
        String correctAnswer = invertCards ? card.getQuestion() : card.getAnswer();

        System.out.println("Q: " + prompt);
        System.out.print("Your answer: ");

        if (!scanner.hasNextLine()) {
            return false;
        }
        String userAnswer = scanner.nextLine().trim();

        if (userAnswer.equalsIgnoreCase("quit")) {
            return false;
        }

        boolean correct = userAnswer.equalsIgnoreCase(correctAnswer);
        card.recordAttempt(correct);
        achievementTracker.recordAnswer(correct);

        if (correct) {
            System.out.println("✓ Correct! (" + card.getConsecutiveCorrect()
                + "/" + requiredRepetitions + " correct)\n");
        } else {
            System.out.println("✗ Wrong. Correct answer: " + correctAnswer + "\n");
        }

        return true;
    }

    /**
     * Returns cards that have not yet met the repetition requirement.
     *
     * @return list of cards still needing practice
     */
    private List<FlashCard> getRemainingCards() {
        List<FlashCard> remaining = new ArrayList<>();
        for (FlashCard card : cards) {
            if (card.getConsecutiveCorrect() < requiredRepetitions) {
                remaining.add(card);
            }
        }
        return remaining;
    }

    /**
     * Checks if all cards have met the required consecutive correct answers.
     *
     * @return true if all cards are mastered
     */
    private boolean allCardsMastered() {
        for (FlashCard card : cards) {
            if (card.getConsecutiveCorrect() < requiredRepetitions) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prints any earned achievements.
     *
     * @param achievements list of achievement descriptions
     */
    private void printAchievements(List<String> achievements) {
        if (!achievements.isEmpty()) {
            System.out.println("🏆 Achievements earned this round:");
            for (String achievement : achievements) {
                System.out.println("  ★ " + achievement);
            }
        }
    }
}
