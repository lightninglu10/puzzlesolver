package puzzle;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringReader;


/** Tests of Parser class.
 *  @author Patrick Lu*/
public class ParseTest {

    private StringReader reader;

    /** Set reader to contain the contents of LINES. */
    private void setUp(String... lines) {
        StringBuilder str = new StringBuilder();

        for (String line : lines) {
            str.append(line);
            str.append("\n");
        }
        reader = new StringReader(str.toString());
    }

    /** Test help message ('java solve' with no arguments). */
    @Test
    public void testEcho() {
        setUp("John is   the carpenter.   "
              + "Paul does not live in the yellow house.   ",
              "  Who is   the\tcarpenter?");

        Parser p = Parser.parse(reader);
        Solver s = new Solver();
        p.inform(s);
        s.permutes();
        s.disassociation();
        s.association();
        assertEquals("bad assertion count", 2, p.numAssertions());
        assertEquals("bad question count", 1, p.numQuestions());
        assertEquals("Fact #0", "John is the carpenter.",  p.getAssertion(0));
        assertEquals("Fact #1", "Paul does not live in the yellow house.",
                     p.getAssertion(1));
        assertEquals("Q #0", "Who is the carpenter?", p.getQuestion(0));
        assertEquals("wrong", "John is the carpenter.", p.getAnswer(s, 0));
    }

    /** Test help message ('java solve' with no arguments). */
    @Test
    public void testFox() {
        setUp("John is not the carpenter.  "
              + "The plumber lives in the blue house.",
              "John lives in the yellow house.",
              "Mary does not live in the blue house.  Tom lives around here.",
              "The architect lives around here.",
              "What do you know about John?",
              "What do you know about Mary?",
              "What do you know about Tom?");

        Parser p = Parser.parse(reader);
        Solver s = new Solver();
        p.inform(s);
        s.permutes();
        s.association();
        s.disassociation();
        assertEquals("size of combo is wrong", 1, s.combinations().size());
        assertEquals("wrong answer", "John is the architect"
                     + " and lives in the yellow house.", p.getAnswer(s, 0));
    }
/** Test help message ('java solve' with no arguments). */
    @Test
    public void testAlpha() {
        setUp("Jack lives in the blue house.  "
              + "Mary does not        live in the blue house.",
              "The mechanic lives around here.",
              "There is a red house.  The architect lives around here.",
              "The sailor lives around here.",


              "Who is the mechanic?   What do you know about Jack?",
              "What do you know about Mary?");

        Parser p = Parser.parse(reader);
        Solver s = new Solver();
        p.inform(s);
        s.permutes();
        s.association();
        s.disassociation();
        assertEquals("size of combo is wrong", 12, s.combinations().size());
        assertEquals("wrong answer", "Jack lives in the blue house.",
                     p.getAnswer55(s, 1));
        assertEquals("wrong answer", "I don't know.", p.getAnswer(s, 0));
    }
    /** Test help message ('java solve' with no arguments). */
    @Test
    public void testBeta() {
        setUp("Stefan is the geneticist.",
              "Alex is not the geneticist.",
              "The businesswoman lives around here.",
              "There is a buzzard house.",

              "Who lives in the buzzard house?",
              "What does Alex do?",
              "Who is the geneticist?");

        Parser p = Parser.parse(reader);
        Solver s = new Solver();
        p.inform(s);
        s.permutes();
        s.association();
        s.disassociation();
        assertEquals("wrong answer", "I don't know.", p.getAnswer11(s, 0));
        assertEquals("wrong answer", "Stefan is the geneticist.",
                     p.getAnswer11(s, 2));
        assertEquals("size of combo is wrong", 2, s.combinations().size());
    }
}
