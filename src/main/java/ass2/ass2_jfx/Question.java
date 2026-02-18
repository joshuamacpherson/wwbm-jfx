package ass2.ass2_jfx;

/**
 * Represents a multiple-choice question.
 *
 * A Question consists of:
 * - The question text
 * - An array of four possible answers
 * - The index of the correct answer
 *
 * This class is immutable. Once created, its values cannot be changed.
 */
public class Question {

    /** The text of the question. */
    private final String questionText;

    /** The array of possible answer choices. */
    private final String[] answers;

    /** The index of the correct answer in the answers array. */
    private final int correctIndex;

    /**
     * Constructs a new Question.
     *
     * @param questionText the text of the question
     * @param answers an array containing the possible answer choices
     * @param correctIndex the index of the correct answer in the array
     */
    public Question(String questionText, String[] answers, int correctIndex) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctIndex = correctIndex;
    }

    /**
     * Returns the question text.
     *
     * @return the question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Returns the array of answer choices.
     *
     * @return the array of answers
     */
    public String[] getAnswers() {
        return answers;
    }

    /**
     * Returns the correct answer as a String.
     *
     * @return the correct answer
     */
    public String getCorrectAnswer() {
        return answers[correctIndex];
    }

    /**
     * Checks whether the provided answer text is correct.
     *
     * @param answer the answer text to check
     * @return true if the answer matches the correct answer; false otherwise
     */
    public boolean isCorrect(String answer) {
        return answers[correctIndex].equals(answer);
    }

    /**
     * Checks whether the provided index matches the correct answer index.
     *
     * @param index the index to check
     * @return true if the index matches the correct answer index; false otherwise
     */
    public boolean isCorrect(int index) {
        return index == correctIndex;
    }

    /**
     * Returns the question text.
     * This is used by ListView<Question> to display the question.
     *
     * @return the question text
     */
    @Override
    public String toString() {
        return questionText;
    }
}