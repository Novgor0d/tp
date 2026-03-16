package linuxlingo.exam.question;

import java.util.List;

/**
 * Fill In The Blank question — the user types a free-form answer that is
 * checked against a list of accepted answers.
 *
 * <p><b>Owner: D</b></p>
 *
 * <h3>Question bank format (parsed by {@code QuestionParser})</h3>
 * <pre>
 * FITB | DIFFICULTY | questionText | answer1|answer2 | (unused) | explanation
 * </pre>
 *
 * <h3>Example</h3>
 * <pre>
 * FITB | EASY | To print the current directory: ___ | pwd | | pwd prints the current working directory.
 * </pre>
 */
public class FitbQuestion extends Question {
    private final List<String> acceptedAnswers;

    public FitbQuestion(String questionText, String explanation,
                        Difficulty difficulty, List<String> acceptedAnswers) {
        super(QuestionType.FITB, difficulty, questionText, explanation);
        this.acceptedAnswers = acceptedAnswers;
    }

    @Override
    public String present() {
        return formatHeader() + " " + questionText + "\n";
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (answer == null) {
            return false;
        }
        return acceptedAnswers.contains(answer.trim());
    }

    public List<String> getAcceptedAnswers() {
        return acceptedAnswers;
    }
}
