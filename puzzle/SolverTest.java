package puzzle;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringReader;

/** Tests of Parser class.
 *  @author Patrick Lu*/
public class SolverTest {

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
        assertEquals("???", 2, s.combinations().get(0).size());
        assertEquals("not enough in list", 2, s.peoplelist().size());
        assertEquals("not enough in list", 2, s.occupationslist().size());
        assertEquals("not enough in list", 2, s.colorslist().size());
        assertEquals("bad triples count", 1, s.tripleslist().size());
        assertEquals("not enough combinations", 4,  s.combinations().size());
        s.disassociation();
        assertEquals("wrong number", 2, s.combinations().size());
        s.association();
        assertEquals("not right", 1, s.combinations().size());
    }

    /** Test help message ('java solve' with no arguments). */
    @Test
    public void testCharlie() {
        setUp("Sue lives around here. There is a brown house.",
              "The professor lives around here.");

        Parser p = Parser.parse(reader);
        Solver s = new Solver();
        p.inform(s);
        s.permutes();
        assertEquals("not enough perms", 6, s.allPerms(3).size());
        assertEquals("permutation too small", 1, s.combinations().size());
        assertEquals("not enough in list", 1, s.peoplelist().size());
        assertEquals("wrong size", 0, s.negativeslist().size());
        assertEquals("not enough in list", 1, s.occupationslist().size());
        assertEquals("not enough in list", 1, s.colorslist().size());
        assertEquals("bad triples count", 0, s.tripleslist().size());
        assertEquals("not enough combinations", 1,  s.combinations().size());
        s.disassociation();
        assertEquals("wrong number", 1, s.combinations().size());
        s.association();
        assertEquals("not right", 1, s.combinations().size());
    }
}
