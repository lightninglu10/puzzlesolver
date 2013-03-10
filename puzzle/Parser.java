package puzzle;

import java.io.Reader;

import java.util.Scanner;

import java.util.regex.Pattern;

import java.util.ArrayList;

import java.util.regex.MatchResult;

import static puzzle.EntityType.*;

/** A sequence of Assertions and Questions parsed from a given file.
 *  @author Patrick Lu*/
class Parser {

    /** A new Parser, containing no assertions or questions. */
    private Parser() {
    }
    /** Assertions. */
    private ArrayList<String> asserts;

    /** Questions. */
    private ArrayList<String> quest;

    /** List of people. */
    private ArrayList<NamedEntity> people =
        new ArrayList<NamedEntity>();

    /** List of occupations. */
    private ArrayList<NamedEntity> occupation =
        new ArrayList<NamedEntity>();

    /** List of colors. */
    private ArrayList<NamedEntity> colors =
        new ArrayList<NamedEntity>();

    /** Patterns used to parse assertions. */
    private Pattern sentence1 =
        Pattern.compile("\\G([A-Z][a-z]+)"
                        + "\\slives\\sin\\s"
                        + "the\\s([a-z]+)\\shouse\\.");
    /** Pattern. */
    private Pattern sentence2 =
        Pattern.compile("\\GThe\\s([a-z]+)"
                        + "\\slives\\sin\\s"
                        + "the\\s([a-z]+)\\shouse\\.");
    /** Pattern. */
    private Pattern sentence3 =
        Pattern.compile("\\GThere\\sis\\sa\\s"
                        + "([a-z]+)\\shouse\\.");
    /** Pattern. */
    private Pattern sentence4 =
        Pattern.compile("\\GThe\\s([a-z]+)"
                        + "\\sdoes\\snot\\s"
                        + "live\\sin\\s"
                        + "the\\s([a-z]+)\\shouse\\.");
    /** Pattern. */
    private Pattern sentence5 =
        Pattern.compile("\\G([A-Z][a-z]+)"
                        + "\\sdoes\\snot\\s"
                        + "live\\sin\\s"
                        + "the\\s([a-z]+)\\shouse\\.");
    /** Pattern. */
    private Pattern sentence6 =
        Pattern.compile("\\GThe\\s([a-z]+)"
                        + " lives\\saround\\shere\\.");
    /** Pattern. */
    private Pattern sentence7 =
        Pattern.compile("\\G([A-Z][a-z]+)"
                        + " lives\\saround\\shere\\.");
    /** Pattern. */
    private Pattern isoccupation =
        Pattern.compile("\\G([A-Z][a-z]+)\\s"
                        + "is\\s(not)?\\s*the\\s([a-z]+)\\.");

    /** Array for easier access of assertions. */
    private Pattern[] patterns = {sentence1, sentence2, sentence3, sentence4,
                                  sentence5, sentence6,
                                  sentence7, isoccupation};

    /** Patterns of questions. */
    private Pattern whoisoccupation =
        Pattern.compile("\\GWho is the ([a-z]+)\\?");

    /** Pattern. */
    private Pattern colorlives =
        Pattern.compile("\\GWho lives in the ([a-z]+) house\\?");

    /** Pattern. */
    private Pattern namedo =
        Pattern.compile("\\GWhat does ([A-Z][a-z]+) do\\?");

    /** Pattern. */
    private Pattern colordo =
        Pattern.compile("\\GWhat does the occupant of the ([a-z]+)"
                        + "\\shouse do\\?");

    /** Pattern. */
    private Pattern wherelive =
        Pattern.compile("Where does (the)?\\s*([A-Z]?[a-z]+) live\\?");

    /** Pattern. */
    private Pattern knowaboutp =
        Pattern.compile("What do you know about (the)?\\s*([A-Z]?[a-z]+)\\?");

    /** Pattern. */
    private Pattern knowaboutc =
        Pattern.compile("What do you know about the ([a-z]+) house\\?");

    /** List of question patterns for easy access. */
    private Pattern[] qlist = {whoisoccupation, colorlives, namedo, colordo,
                               wherelive, knowaboutp, knowaboutc};

    /** List of Keywords. */
    private String[] keywords = {"who", "what", "where", "the", "is", "not",
                                 "house", "lives", "in", "does", "not",
                                 "name", "occupant", "occupation", "color",
                                 "there", "a", "do", "you", "know", "about",
                                 "of", "live"};


    /** Returns a Parser that contains assertions and questions from
     *  READER. */
    static Parser parse(Reader reader) {
        Scanner inp = new Scanner(reader);
        Parser work = new Parser();
        Pattern question = Pattern.compile("\\G([^\\?\\.])+\\?\\s*");
        Pattern assertion = Pattern.compile("\\G([^\\?\\.])+\\.\\s*");
        ArrayList<String> q1 = new ArrayList<String>();
        ArrayList<String> a1 = new ArrayList<String>();
        while (inp.hasNext()) {
            boolean matched = false;
            if (inp.findInLine(question) != null) {
                MatchResult questions = inp.match();
                q1.add(questions.group(0));
                matched = true;
            }
            if (inp.findInLine(assertion) != null) {
                MatchResult assertions = inp.match();
                a1.add(assertions.group(0));
                matched = true;
            }
            if (!matched) {
                inp.nextLine();
            }
        }
        work.asserts = a1;
        work.quest = q1;
        return work;
    }

    /** Transfer all information conveyed by the asserations I have
     *  read to SOLVER. */
    void inform(Solver solver) {
        for (int i = 0; i < numAssertions(); i += 1) {
            inform(solver, i);
        }
        solver.obtain(asserts, people, occupation, colors);
    }

    /** Returns the number of assertions I have parsed. */
    int numAssertions() {
        return asserts.size();
    }

    /** Returns the text of assertion number K (numbering from 0), with extra
     *  spaces removed. */
    String getAssertion(int k) {
        return asserts.get(k).replaceAll("\\s+", " ").trim();
    }

    /** Checks if the list contains the element then adds it in.
     * @param lists arraylist
     * @param element string */
    void cont(ArrayList<NamedEntity> lists, String element) {
        for (int i = 0; i < keywords.length; i += 1) {
            if (element == keywords[i]) {
                throw new PuzzleException("Keyword");
            }
        }
        if (lists == people) {
            if (!lists.contains(new NamedEntity(element, PERSON))) {
                lists.add(new NamedEntity(element, PERSON));
            }
        }
        if (lists == occupation) {
            if (!lists.contains(new NamedEntity(element, OCCUPATION))) {
                lists.add(new NamedEntity(element, OCCUPATION));
            }
        }
        if (lists == colors) {
            if (!lists.contains(new NamedEntity(element, COLOR))) {
                lists.add(new NamedEntity(element, COLOR));
            }
        }
    }

    /** Inform SOLVER of the information in assertion K. */
    void inform(Solver solver, int k) {
        String h = getAssertion(k); Scanner s = new Scanner(h);
        boolean match = false;
        for (int i = 0; i < patterns.length; i += 1) {
            if (s.findInLine(patterns[i]) != null) {
                match = true;
                MatchResult mat = s.match();
                if (i == 0) {
                    solver.associate(new NamedEntity(mat.group(1), PERSON),
                                     new NamedEntity(mat.group(2), COLOR));
                    cont(people, mat.group(1)); cont(colors, mat.group(2));
                } else if (i == 1) {
                    solver.associate(new NamedEntity(mat.group(1), OCCUPATION),
                                     new NamedEntity(mat.group(2), COLOR));
                    cont(occupation, mat.group(1)); cont(colors, mat.group(2));
                } else if (i == 2) {
                    solver.exists(new NamedEntity(mat.group(1), COLOR));
                    cont(colors, mat.group(1));
                } else if (i == 3) {
                    solver.disassociate(new NamedEntity(mat.group(1),
                                                        OCCUPATION),
                                        new NamedEntity(mat.group(2), COLOR));
                    cont(occupation, mat.group(1)); cont(colors, mat.group(2));
                } else if (i == 4) {
                    solver.disassociate(new NamedEntity(mat.group(1), PERSON),
                                        new NamedEntity(mat.group(2), COLOR));
                    cont(people, mat.group(1)); cont(colors, mat.group(2));
                } else if (i == 5) {
                    solver.exists(new NamedEntity(mat.group(1), OCCUPATION));
                    cont(occupation, mat.group(1));
                } else if (i == 6) {
                    solver.exists(new NamedEntity(mat.group(1), PERSON));
                    cont(people, mat.group(1));
                } else if (i == 7) {
                    if (mat.group(2) == null) {
                        solver.associate(new NamedEntity(mat.group(1), PERSON),
                                         new NamedEntity(mat.group(3),
                                                         OCCUPATION));
                        cont(people, mat.group(1));
                        cont(occupation, mat.group(3));
                    } else {
                        solver.disassociate(new NamedEntity(mat.group(1),
                                                            PERSON),
                                            new NamedEntity(mat.group(3),
                                                            OCCUPATION));
                        cont(people, mat.group(1));
                        cont(occupation, mat.group(3));
                    }
                }
            }
        }
        if (!match) {
            throw new PuzzleException("Not the right structure.");
        }
    }

    /** Returns the number of questions I have parsed. */
    int numQuestions() {
        return quest.size();
    }

    /** Return the text of question number K (numbering from 0), with extra
     *  spaces removed. */
    String getQuestion(int k) {
        return quest.get(k).replaceAll("\\s+", " ").trim();
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER. */
    String getAnswer(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        for (int a = 0; a < 3; a += 1) {
            if (s.findInLine(qlist[a]) != null) {
                MatchResult mat = s.match(); int count = 0;
                NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
                if (a == 0) {
                    for (int i = 0; i < res.size(); i += 1) {
                        for (int j = 0; j < res.get(i).size(); j += 1) {
                            if (mat.group(1).equals(
                                    res.get(i).get(j)[1].getName())) {
                                count += 1; result = res.get(i).get(j)[0];
                            }
                        }
                    }
                    if (count == 1 && !result.isAnonymous()) {
                        return result.getName() + " is the " + mat.group(1)
                            + ".";
                    } else {
                        return "I don't know.";
                    }
                }
                if (a == 1) {
                    for (int i = 0; i < res.size(); i += 1) {
                        for (int j = 0; j < res.get(i).size(); j += 1) {
                            if (mat.group(1).equals(
                                    res.get(i).get(j)[2].getName())) {
                                count += 1;
                                result = res.get(i).get(j)[0];
                            }
                        }
                    }
                    if (count == 1 && !result.isAnonymous()) {
                        return result.getName() + " lives in the "
                            + mat.group(1) + " house.";
                    } else {
                        return "I don't know.";
                    }
                }
            }
        }
        return getAnswer2(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer2(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        if (s.findInLine(qlist[2]) != null) {
            MatchResult mat = s.match(); int count = 0;
            NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            for (int i = 0; i < res.size(); i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1).equals(
                            res.get(i).get(j)[0].getName())) {
                        count += 1;
                        result = res.get(i).get(j)[1];
                    }
                }
            }
            if (count == 1 && !result.isAnonymous()) {
                return mat.group(1) + " is the " + result.getName()
                    + ".";
            } else {
                return "I don't know.";
            }
        }
        return getAnswer3(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer3(Solver solver, int k) {
        String h = getQuestion(k); Scanner s = new Scanner(h);
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        for (int a = 3; a < 5; a += 1) {
            if (s.findInLine(qlist[a]) != null) {
                MatchResult mat = s.match(); int count = 0;
                NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
                if (a == 3) {
                    for (int i = 0; i < res.size(); i += 1) {
                        for (int j = 0; j < res.get(i).size(); j += 1) {
                            if (mat.group(1).equals(
                                    res.get(i).get(j)[2].getName())) {
                                count += 1;
                                result = res.get(i).get(j)[1];
                            }
                        }
                    }
                    if (count == 1 && !result.isAnonymous()) {
                        return "The " + result.getName() + " lives in the "
                            + mat.group(1) + " house.";
                    } else {
                        return "I don't know.";
                    }
                }
                if (a == 4) {
                    for (int i = 0; i < res.size(); i += 1) {
                        for (int j = 0; j < res.get(i).size(); j += 1) {
                            if (mat.group(1) != null) {
                                if (mat.group(2).equals(
                                        res.get(i).get(j)[1].getName())) {
                                    count += 1;
                                    result = res.get(i).get(j)[2];
                                }
                            } else if (mat.group(2).equals(
                                           res.get(i).get(j)[0].getName())) {
                                count += 1;
                                result = res.get(i).get(j)[2];
                            }
                        }
                    }
                    if (count == 1 && !result.isAnonymous()) {
                        if (mat.group(1) != null) {
                            return "The " + mat.group(2) + " lives in the "
                                + result.getName() + " house.";
                        } else {
                            return mat.group(2) + " lives in the "
                                + result.getName() + " house.";
                        }
                    } else {
                        return "I don't know.";
                    }
                }
            }
        }
        return getAnswer4(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer4(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        if (s.findInLine(qlist[5]) != null) {
            MatchResult mat = s.match(); int count = 0;
            NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            NamedEntity re = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            for (int j = 0; j < res.get(0).size(); j += 1) {
                if (mat.group(1) != null) {
                    if (mat.group(2).equals(
                        res.get(0).get(j)[1].getName())) {
                        result = res.get(0).get(j)[2];
                        count += 1; re = res.get(0).get(j)[0];
                    }
                } else if (mat.group(2).equals(
                           res.get(0).get(j)[0].getName())) {
                    count += 1; result = res.get(0).get(j)[2];
                    re = res.get(0).get(j)[1];
                }
            }
            if (count == 1 && (!result.isAnonymous()
                               || !re.isAnonymous())) {
                if (!result.isAnonymous() && !re.isAnonymous()) {
                    if (mat.group(1) != null) {
                        return re.getName() + " is the " + mat.group(2)
                            + " and lives in the " + result.getName()
                            + " house.";
                    } else {
                        return mat.group(2) + " is the " + re.getName()
                            + " and lives in the " + result.getName()
                            + " house.";
                    }
                } else if (result.isAnonymous() && !re.isAnonymous()) {
                    if (mat.group(1) != null) {
                        return re.getName() + " is the "
                            + mat.group(2) + ".";
                    } else {
                        return mat.group(2) + " is the "
                            + re.getName() + ".";
                    }
                } else if (!result.isAnonymous() && re.isAnonymous()) {
                    if (mat.group(1) != null) {
                        return "The " + mat.group(2) + " lives in the "
                            + result.getName() + " house.";
                    } else {
                        return mat.group(2) + " lives in the "
                            + result.getName() + " house.";
                    }
                }
            } else {
                return "Nothing.";
            }
        }
        return getAnswer6(solver, k);
    }

/** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer6(Solver solver, int k) {
        String h = getQuestion(k); Scanner s = new Scanner(h);
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        for (int a = 6; a < 7; a += 1) {
            if (s.findInLine(qlist[a]) != null) {
                MatchResult mat = s.match();
                NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
                NamedEntity re = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
                if (a == 6) {
                    int count = 0;
                    for (int i = 0; i < res.size(); i += 1) {
                        for (int j = 0; j < res.get(i).size(); j += 1) {
                            if (mat.group(1).equals(
                                    res.get(i).get(j)[2].getName())) {
                                count += 1; result =
                                                res.get(i).get(j)[1];
                                re = res.get(i).get(j)[0];
                            }
                        }
                    }
                    if (count == 1 || count == 2 && (!result.isAnonymous()
                                                     || !re.isAnonymous())) {
                        if (!result.isAnonymous() && !re.isAnonymous()) {
                            return re.getName() + " is the " + result.getName()
                                + " and lives in the " + mat.group(1)
                                + " house.";
                        }
                        if (result.isAnonymous() && !re.isAnonymous()) {
                            return "The " + result.getName() + " lives in "
                                + " the " + mat.group(1) + " house.";
                        }
                        if (!result.isAnonymous() && re.isAnonymous()) {
                            return re.getName() + " lives in the "
                                + mat.group(1) + " house.";
                        }
                    } else {
                        return "Nothing.";
                    }
                }
            }
        }
        return "Impossible.";
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER. */
    String getAnswer11(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        if (s.findInLine(qlist[0]) != null) {
            MatchResult mat = s.match();
            NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            int count = 0; int two = 0;
            for (int i = 0; i < res.size() - 1; i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1).equals(
                        res.get(i).get(j)[1].getName())) {
                        if (res.size() == 2) {
                            if (res.get(i).get(j)[1].equals(
                                res.get(i + 1).get(j)[1])) {
                                two = 1;
                            }
                        }
                        if (res.get(i).get(j)[0].equals(
                            res.get(i + 1).get(j)[0])) {
                            count += 1; result = res.get(i).get(j)[0];
                        }
                    }
                }
            }
            if (((count == res.size() - 1 && res.size() != 2)
                 || (two == 1 && res.size() == 2))
                && !result.isAnonymous()
                && !result.equals(new NamedEntity("a", PERSON))) {
                return result.getName() + " is the " + mat.group(1)
                    + ".";
            } else {
                return "I don't know.";
            }
        }
        return getAnswer111(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer111(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        if (s.findInLine(qlist[1]) != null) {
            MatchResult mat = s.match();
            NamedEntity result = new NamedEntity("a", PERSON);
            int count = 0; int two = 0;
            for (int i = 0; i < res.size() - 1; i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1).equals(
                                            res.get(i).get(j)[2].getName())) {
                        if (res.size() == 2) {
                            if (res.get(i).get(j)[2].equals(
                                res.get(i + 1).get(j)[2])) {
                                two = 1;
                            }
                        }
                        if (res.get(i).get(j)[0].equals(
                            res.get(i + 1).get(j)[0])) {
                            count += 1; result = res.get(i).get(j)[0];
                        }
                    }
                }
            }
            if ((count == res.size() - 1 && res.size() != 2)
                || (two == 1 && res.size() == 2)
                && !result.isAnonymous()) {
                return result.getName() + " lives in the "
                    + mat.group(1) + " house.";
            } else {
                return "I don't know.";
            }
        }
        return getAnswer22(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer22(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        if (s.findInLine(qlist[2]) != null) {
            MatchResult mat = s.match(); int count = 0; int two = 0;
            NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            for (int i = 0; i < res.size() - 1; i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1).equals(
                            res.get(i).get(j)[0].getName())) {
                        if (res.size() == 2) {
                            if (res.get(i).get(j)[0].equals(
                                res.get(i + 1).get(j)[0])) {
                                two = 1;
                            }
                        }
                        if (res.get(i).get(j)[1].equals(
                            res.get(i + 1).get(j)[1])) {
                            count += 1; result = res.get(i).get(j)[1];
                        }
                    }
                }
            }
            if ((count == res.size() - 1 && res.size() != 2)
                || (two == 1 && res.size() == 2)
                && !result.isAnonymous()) {
                return mat.group(1) + " is the " + result.getName()
                    + ".";
            } else {
                return "I don't know.";
            }
        }
        return getAnswer33(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer33(Solver solver, int k) {
        String h = getQuestion(k); Scanner s = new Scanner(h);
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        if (s.findInLine(qlist[3]) != null) {
            MatchResult mat = s.match(); int count = 0; int two = 0;
            NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            for (int i = 0; i < res.size() - 1; i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1).equals(
                        res.get(i).get(j)[2].getName())) {
                        if (res.size() == 2) {
                            if (res.get(i).get(j)[2].equals(
                                res.get(i + 1).get(j)[2])) {
                                two = 1;
                            }
                        }
                        if (res.get(i).get(j)[1].equals(
                            res.get(i + 1).get(j)[1])) {
                            count += 1; result = res.get(i).get(j)[1];
                        }
                    }
                }
            }
            if ((count == res.size() - 1 && res.size() != 2)
                || (two == 1 && res.size() == 2)
                && !result.isAnonymous()) {
                return "The " + result.getName() + " lives in the "
                    + mat.group(1) + " house.";
            } else {
                return "I don't know.";
            }
        }
        return getAnswer44(solver, k);
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer44(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        int count = 0; int counts = 0; int two = 0;
        NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
        NamedEntity re = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
        if (s.findInLine(qlist[4]) != null) {
            MatchResult mat = s.match();
            for (int i = 0; i < res.size() - 1; i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1) != null) {
                        if (mat.group(2).equals(
                            res.get(i).get(j)[1].getName())) {
                            if (res.size() == 2) {
                                if (res.get(i).get(j)[1].equals(
                                    res.get(i + 1).get(j)[1])) {
                                    two = 1;
                                }
                            }
                        }
                        if (res.get(i).get(j)[2].equals(
                            res.get(i + 1).get(j)[2])) {
                            count += 1; result = res.get(i).get(j)[2];
                        }
                    } else if (mat.group(2).equals(
                               res.get(i).get(j)[0].getName())) {
                        if (res.size() == 2) {
                            if (res.get(i).get(j)[0].equals(
                                res.get(i + 1).get(j)[0])) {
                                two = 1;
                            }
                        }
                        if (res.get(i).get(j)[2].equals(
                            res.get(i + 1).get(j)[2])) {
                            count += 1; result = res.get(i).get(j)[2];
                        }
                    }
                }
            }
            if ((count == res.size() - 1 && res.size() != 2)
                || (two == 1 && res.size() == 2)
                && !result.isAnonymous()) {
                if (mat.group(1) != null) {
                    return "The " + mat.group(2) + " lives in the "
                        + result.getName() + " house.";
                } else {
                    return mat.group(2) + " lives in the "
                        + result.getName() + " house.";
                }
            } else {
                return "I don't know.";
            }
        }
        return getAnswer55(solver, k);
    }
    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer55(Solver solver, int k) {
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations();
        String h = getQuestion(k); Scanner s = new Scanner(h);
        int count = 0; int counts = 0; int two = 0;
        NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
        NamedEntity re = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
        if (s.findInLine(qlist[5]) != null) {
            MatchResult mat = s.match();
            for (int i = 0; i < res.size() - 1; i += 1) {
                for (int j = 0; j < res.get(0).size(); j += 1) {
                    if (mat.group(1) != null) {
                        if (mat.group(2).equals(
                            res.get(0).get(j)[1].getName())) {
                            if (res.size() == 2) {
                                if (res.get(i).get(j)[1].equals(
                                    res.get(i + 1).get(j)[1])) {
                                    two = 1;
                                }
                            }
                            if (res.get(i).get(j)[2].equals(
                                res.get(i + 1).get(j)[2])) {
                                count += 1; result = res.get(0).get(j)[2];
                            }
                            if (res.get(i).get(j)[0].equals(
                                res.get(i + 1).get(j)[0])) {
                                counts += 1; re = res.get(0).get(j)[0];
                            }
                        }
                    } else {
                        if (mat.group(2).equals(
                            res.get(0).get(j)[0].getName())) {
                            if (res.size() == 2) {
                                if (res.get(i).get(j)[0].equals(
                                    res.get(i + 1).get(j)[0])) {
                                    two = 1;
                                }
                            }
                            if (res.get(i).get(j)[2].equals(
                                res.get(i + 1).get(j)[2])) {
                                count += 1; result = res.get(0).get(j)[2];
                            }
                            if (res.get(i).get(j)[1].equals(
                                res.get(i + 1).get(j)[1])) {
                                counts += 1; re = res.get(0).get(j)[1];
                            }
                        }
                    }
                }
            }
            return getAnswer555(counts, count, re, result,
                                res.size(), mat, two);
        }
        return getAnswer66(solver, k);
    }

 /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param counts counter
     * @param count counter
     * @param re namedentity
     * @param result namedentity
     * @param size size of list
     * @param mat matchresult
     * @param two list of two check*/
    String getAnswer555(int counts, int count,
                        NamedEntity re, NamedEntity result, int size,
                        MatchResult mat, int two) {
        if (!result.isAnonymous() && !re.isAnonymous()
            && (((count == size - 1 && size != 2)
                && (counts == size - 1 && size != 2))
                || (two == 1 && size == 2))) {
            if (mat.group(1) != null) {
                return re.getName() + " is the " + mat.group(2)
                    + " and lives in the " + result.getName()
                    + " house.";
            } else {
                return mat.group(2) + " is the " + re.getName()
                    + " and lives in the " + result.getName()
                    + " house.";
            }
        } else if (!re.isAnonymous() && ((counts == size - 1 && size != 2)
                                         || (two == 1 && size == 2))) {
            if (mat.group(1) != null) {
                return re.getName() + " is the "
                    + mat.group(2) + ".";
            } else {
                return mat.group(2) + " is the "
                    + re.getName() + ".";
            }
        } else if (!result.isAnonymous() && ((count == size - 1 && size != 2)
                                             || (two == 1 && size == 2))) {
            if (mat.group(1) != null) {
                return "The " + mat.group(2) + " lives in the "
                    + result.getName() + " house.";
            } else {
                return mat.group(2) + " lives in the "
                    + result.getName() + " house.";
            }
        }  else {
            return "Nothing.";
        }
    }

    /** Return the answer to question K, according to the information
     *  in SOLVER.
     * @param solver a solver
     * @param k number question */
    String getAnswer66(Solver solver, int k) {
        String h = getQuestion(k); Scanner s = new Scanner(h);
        ArrayList<ArrayList<NamedEntity[]>> res =
            new ArrayList<ArrayList<NamedEntity[]>>();
        res = solver.combinations(); int count = 0; int counts = 0; int two = 0;
        if (s.findInLine(qlist[6]) != null) {
            MatchResult mat = s.match();
            NamedEntity result = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            NamedEntity re = new NamedEntity(
                                     NamedEntity.makeName("UN"), PERSON);
            for (int i = 0; i < res.size(); i += 1) {
                for (int j = 0; j < res.get(i).size(); j += 1) {
                    if (mat.group(1).equals(
                        res.get(i).get(j)[2].getName())) {
                        if (res.size() == 2) {
                            if (res.get(i).get(j)[0].equals(
                                res.get(i + 1).get(j)[0])) {
                                two = 1;
                            }
                        }
                        if (res.get(i).get(j)[1].equals(
                            res.get(i + 1).get(j)[1])) {
                            count += 1; result = res.get(i).get(j)[1];
                        }
                        if (res.get(i).get(j)[0].equals(
                            res.get(i + 1).get(j)[0])) {
                            re = res.get(i).get(j)[0]; counts += 1;
                        }
                    }
                }
            }
            if (!result.isAnonymous() && !re.isAnonymous()
                && ((count == res.size() - 1 && res.size() != 2)
                    && (counts == res.size() - 1 && res.size() != 2)
                    || (two == 1 && res.size() == 2))) {
                return re.getName() + " is the " + result.getName()
                    + " and lives in the " + mat.group(1)
                    + " house.";
            }
            if (!re.isAnonymous()
                && ((counts == res.size() - 1 && res.size() != 2)
                    || (two == 1 && res.size() == 2))) {
                return "The " + result.getName() + " lives in "
                    + " the " + mat.group(1) + " house.";
            }
            if (!result.isAnonymous()
                && ((counts == res.size() - 1 && res.size() != 2)
                 || (two == 1 && res.size() == 2))) {
                return re.getName() + " lives in the "
                    + mat.group(1) + " house.";
            }
        } else {
            return "Nothing.";
        }
        return "Impossible.";
    }
}
