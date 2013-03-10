package puzzle;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.FileNotFoundException;

/** The Puzzle Solver.
 * @author Patrick Lu
 */
public class Solve {

    /** Solve the puzzle given in ARGS[0], if given.  Otherwise, print
     *  a help message. */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        if (args.length > 1) {
            System.err.println("Error: too many arguments");
            usage();
            System.exit(1);
        }

        File inputFileName = new File(args[0]);
        Reader input;

        try {
            input = new FileReader(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Error: file %s not found", inputFileName);
            System.exit(1);
            return;
        }

        try {
            Parser puzzle = Parser.parse(input);
            Solver solution = new Solver();
            puzzle.inform(solution);
            solution.permutes();
            solution.association();
            solution.disassociation();
            solution.nodups();
            for (int i = 0; i < puzzle.numAssertions(); i += 1) {
                System.out.println(Integer.toString(i + 1) + ". "
                                   + puzzle.getAssertion(i));
            }
            System.out.println();
            if (solution.impossible()) {
                System.out.println("That's impossible.");
            } else {
                for (int i = 0; i < puzzle.numQuestions(); i += 1) {
                    if (solution.combinations().size() == 1) {
                        System.out.println("Q: "
                                           + puzzle.getQuestion(i));
                        System.out.println("A: "
                                           + puzzle.getAnswer(solution, i));
                    } else {
                        System.out.println("Q: " + puzzle.getQuestion(i));
                        System.out.println("A: "
                                           + puzzle.getAnswer11(solution, i));
                    }
                }
            }
        } catch (PuzzleException e) {
            System.out.println("Error: bad puzzle");
            System.exit(1);
            return;
        }
    }
    /** Usage method. */
    private static void usage() {
        System.out.println("Input a list of assertions and questions to"
                           + "get the solver to work.");
    }
}
