package linuxlingo.exam.question;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitbQuestionTest {
    private FitbQuestion makeQuestion() {
        return new FitbQuestion(
                "To print the current directory: ___",
                "pwd prints the current working directory.",
                Question.Difficulty.EASY,
                List.of("pwd")
        );
    }

    private FitbQuestion makeMultiAnswerQuestion() {
        return new FitbQuestion(
                "To go to the home directory: cd ___",
                "~ is a shortcut for the home directory.",
                Question.Difficulty.EASY,
                List.of("~", "/home/user")
        );
    }

    @Test
    void testPresentContainsHeader() {
        String output = makeQuestion().present();
        assertTrue(output.contains("(FITB · EASY)"));
        assertTrue(output.contains("To print the current directory"));
    }

    @Test
    void testCheckAnswerCorrect() {
        assertTrue(makeQuestion().checkAnswer("pwd"));
    }

    @Test
    void testCheckAnswerMultipleAccepted() {
        assertTrue(makeMultiAnswerQuestion().checkAnswer("~"));
        assertTrue(makeMultiAnswerQuestion().checkAnswer("/home/user"));
    }

    @Test
    void testCheckAnswerWrong() {
        assertFalse(makeQuestion().checkAnswer("ls"));
        assertFalse(makeQuestion().checkAnswer("cd"));
    }

    @Test
    void testCheckAnswerNull() {
        assertFalse(makeQuestion().checkAnswer(null));
        assertFalse(makeQuestion().checkAnswer(""));
    }
}
