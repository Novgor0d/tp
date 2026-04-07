package linuxlingo.exam;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import linuxlingo.cli.Ui;
import linuxlingo.exam.question.Question;

/**
 * Handles all user interaction for non-PRAC questions: printing the
 * question, reading the answer, showing feedback, and (optionally)
 * recording the outcome into an {@link ExamResult}.
 */
class QuestionInteraction {

    private static final Logger LOGGER = Logger.getLogger(QuestionInteraction.class.getName());

    private final Ui ui;

    QuestionInteraction(Ui ui) {
        this.ui = Objects.requireNonNull(ui, "ui must not be null");
    }

    /**
     * Print the question header and body, then prompt the user for an answer.
     */
    private String askQuestion(Question question, int index, int total) {
        ui.println("[Q" + index + "/" + total + "] " + question.present());
        return ui.readLine("Your answer: ");
    }

    /**
     * Present a non-PRAC question as part of an exam run and record the
     * result in the given {@link ExamResult}.
     */
    void presentQuestionWithResult(Question question, int index, int total, ExamResult result) {
        Objects.requireNonNull(question, "question must not be null");
        Objects.requireNonNull(result, "result must not be null");

        String userAnswer = askQuestion(question, index, total);
        if (userAnswer == null || userAnswer.trim().equalsIgnoreCase("quit")) {
            LOGGER.log(Level.FINE, "Question skipped by user at index {0}", index);
            result.addResult(question, "", false);
            return;
        }

        boolean correct = question.checkAnswer(userAnswer);
        if (correct) {
            ui.println("✓ Correct!");
        } else {
            ui.println("✗ Incorrect.");
        }
        ui.println("Explanation: " + question.getExplanation());
        result.addResult(question, userAnswer, correct);
    }

    /**
     * Present a non-PRAC question once (without recording into an
     * {@link ExamResult}). Used by single-random-question mode.
     *
     * @return {@code true} if the answer was correct, {@code false} otherwise
     */
    boolean presentSingleQuestion(Question question, int index, int total) {
        Objects.requireNonNull(question, "question must not be null");
        if (index <= 0 || total <= 0) {
            throw new IllegalArgumentException("index and total must be positive");
        }

        String userAnswer = askQuestion(question, index, total);
        if (userAnswer == null || userAnswer.trim().equalsIgnoreCase("quit")) {
            LOGGER.log(Level.FINE, "Question skipped by user at index {0}", index);
            return false;
        }

        boolean correct = question.checkAnswer(userAnswer);
        if (correct) {
            ui.println("✓ Correct!");
        } else {
            ui.println("✗ Incorrect.");
        }
        ui.println("Explanation: " + question.getExplanation());
        return correct;
    }
}

