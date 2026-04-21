package flashcard;

/**
 * Represents a single flashcard with a question and answer.
 */
public class FlashCard {

    private final String question;
    private final String answer;
    private int totalAttempts;
    private int correctAttempts;
    private int consecutiveCorrect;
    private boolean wrongInLastRound;

    /**
     * Creates a new FlashCard.
     *
     * @param question the question side of the card
     * @param answer   the answer side of the card
     */
    public FlashCard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.totalAttempts = 0;
        this.correctAttempts = 0;
        this.consecutiveCorrect = 0;
        this.wrongInLastRound = false;
    }

    /** @return the question text */
    public String getQuestion() {
        return question;
    }

    /** @return the answer text */
    public String getAnswer() {
        return answer;
    }

    /** @return total number of attempts on this card */
    public int getTotalAttempts() {
        return totalAttempts;
    }

    /** @return number of correct attempts */
    public int getCorrectAttempts() {
        return correctAttempts;
    }

    /** @return number of consecutive correct answers */
    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
    }

    /** @return whether this card was answered wrong in the last round */
    public boolean isWrongInLastRound() {
        return wrongInLastRound;
    }

    /**
     * Records the result of an attempt on this card.
     *
     * @param correct true if the answer was correct
     */
    public void recordAttempt(boolean correct) {
        totalAttempts++;
        if (correct) {
            correctAttempts++;
            consecutiveCorrect++;
            wrongInLastRound = false;
        } else {
            consecutiveCorrect = 0;
            wrongInLastRound = true;
        }
    }

    /**
     * Resets the wrong-in-last-round flag at the start of each round.
     */
    public void resetRoundFlag() {
        wrongInLastRound = false;
    }

    @Override
    public String toString() {
        return "FlashCard{question='" + question + "', answer='" + answer + "'}";
    }
}
