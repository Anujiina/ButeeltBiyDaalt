package flashcard;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks and evaluates achievements during a flashcard session.
 */
public class AchievementTracker {

    /** Milliseconds threshold for the SPEEDY achievement (5 seconds). */
    private static final long SPEEDY_THRESHOLD_MS = 5000;

    /** Minimum consecutive correct answers for CONFIDENT achievement. */
    private static final int CONFIDENT_THRESHOLD = 3;

    /** Minimum total attempts for the REPEAT achievement. */
    private static final int REPEAT_THRESHOLD = 5;

    private long roundStartTime;
    private int roundAnswerCount;
    private boolean allCorrectThisRound;

    /**
     * Creates a new AchievementTracker.
     */
    public AchievementTracker() {
        this.allCorrectThisRound = true;
    }

    /**
     * Call this at the start of each round to reset round-level tracking.
     */
    public void startRound() {
        roundStartTime = System.currentTimeMillis();
        roundAnswerCount = 0;
        allCorrectThisRound = true;
    }

    /**
     * Records one answer in the current round.
     *
     * @param correct whether the answer was correct
     */
    public void recordAnswer(boolean correct) {
        roundAnswerCount++;
        if (!correct) {
            allCorrectThisRound = false;
        }
    }

    /**
     * Evaluates and returns all achievements earned after the current round ends.
     *
     * @param cards all cards in the session
     * @return list of achievement names earned this round
     */
    public List<String> evaluateRound(List<FlashCard> cards) {
        List<String> earned = new ArrayList<>();
        long roundEndTime = System.currentTimeMillis();
        long elapsed = roundEndTime - roundStartTime;

        // SPEEDY: average response time under 5 seconds
        if (roundAnswerCount > 0) {
            long avgMs = elapsed / roundAnswerCount;
            if (avgMs < SPEEDY_THRESHOLD_MS) {
                earned.add("SPEEDY - Дундаж 5 секундээс доош хугацаанд хариулсан!");
            }
        }

        // CORRECT: all cards answered correctly this round
        if (allCorrectThisRound && roundAnswerCount > 0) {
            earned.add("CORRECT - Энэ тойрогт бүх карт зөв хариулсан!");
        }

        // REPEAT: any card answered more than 5 times total
        for (FlashCard card : cards) {
            if (card.getTotalAttempts() > REPEAT_THRESHOLD) {
                earned.add("REPEAT - '" + card.getQuestion() + "' картад 5-аас олон удаа хариулсан!");
                break;
            }
        }

        // CONFIDENT: any card answered correctly at least 3 times consecutively
        for (FlashCard card : cards) {
            if (card.getConsecutiveCorrect() >= CONFIDENT_THRESHOLD) {
                earned.add("CONFIDENT - '" + card.getQuestion() + "' картад дор хаяж 3 удаа зөв хариулсан!");
                break;
            }
        }

        return earned;
    }
}
