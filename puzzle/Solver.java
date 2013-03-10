package puzzle;

import java.util.List;
import java.util.ArrayList;
import static puzzle.EntityType.*;

/** A puzzle-solving engine.
    @author Patrick Lu */
class Solver {

    /** Assertions. */
    private ArrayList<String> asserts;

    /** Arraylist of associations. */
    private ArrayList<NamedEntity[]> triples =
        new ArrayList<NamedEntity[]>();

    /** Arraylist of negatives. */
    private ArrayList<NamedEntity[]> negatives =
        new ArrayList<NamedEntity[]>();

    /** Arraylist of exists. */
    private ArrayList<NamedEntity[]> exist =
        new ArrayList<NamedEntity[]>();

    /** Arraylist of people. */
    private ArrayList<NamedEntity> people;

    /** Arraylist of occupations. */
    private ArrayList<NamedEntity> occupations;

    /** Arraylist of colors. */
    private ArrayList<NamedEntity> colors;

    /** Set of all combinations. */
    private ArrayList<ArrayList<NamedEntity[]>> combo =
        new ArrayList<ArrayList<NamedEntity[]>>();

    /** Set of all combo copys. */
    private ArrayList<ArrayList<NamedEntity[]>> copy =
        new ArrayList<ArrayList<NamedEntity[]>>();

    /** Returns the triples list. */
    ArrayList<NamedEntity[]> tripleslist() {
        return triples;
    }

    /** Returns the negatives list. */
    ArrayList<NamedEntity[]> negativeslist() {
        return negatives;
    }

    /** Returns the combination list. */
    ArrayList<ArrayList<NamedEntity[]>> combinations() {
        return combo;
    }

    /** Returns the people list. */
    ArrayList<NamedEntity> peoplelist() {
        return people;
    }

    /** Returns the occupations list. */
    ArrayList<NamedEntity> occupationslist() {
        return occupations;
    }

    /** Returns the people list. */
    ArrayList<NamedEntity> colorslist() {
        return colors;
    }

    /** Gets the assertions and triples from Parser class.
     * @param assertions list of asserts
     * @param person list of people
     * @param jobs list of jobs
     * @param color list of colors */
    void obtain(ArrayList<String> assertions, ArrayList<NamedEntity> person,
                ArrayList<NamedEntity> jobs, ArrayList<NamedEntity> color) {
        asserts = assertions;
        people = person;
        occupations = jobs;
        colors = color;
    }

    /** Asserts that E0 and E1 are associated (e.g., Tom is the
     *  carpenter). */
    void associate(NamedEntity e0, NamedEntity e1) {
        if (e0.getType() == PERSON && e1.getType() == OCCUPATION) {
            NamedEntity[] adder = {e0, e1,
                                   new NamedEntity
                                   (NamedEntity.makeName("UNKNOWN"), COLOR)};
            triples.add(adder);
        }
        if (e0.getType() == PERSON && e1.getType() == COLOR) {
            NamedEntity[] adder =
            {e0, new NamedEntity(NamedEntity.makeName("UNKNOWN"), OCCUPATION),
             e1};
            triples.add(adder);
        }
        if (e0.getType() == OCCUPATION && e1.getType() == COLOR) {
            NamedEntity[] adder =
            {new NamedEntity(NamedEntity.makeName("UNKNOWN"), PERSON),
             e0, e1};
            triples.add(adder);
        }
    }

    /** Asserts that E0 and E1 are not associated (e.g., Tom is not the
     *  carpenter). */
    void disassociate(NamedEntity e0, NamedEntity e1) {
        if (e0.getType() == PERSON && e1.getType() == OCCUPATION) {
            NamedEntity[] adder =
            {e0, e1,
             new NamedEntity(NamedEntity.makeName("UNKNOWN"), COLOR)};
            negatives.add(adder);
        }
        if (e0.getType() == PERSON && e1.getType() == COLOR) {
            NamedEntity[] adder =
            {e0, new NamedEntity(NamedEntity.makeName("UNKNOWN"),
                                 OCCUPATION), e1};
            negatives.add(adder);
        }
        if (e0.getType() == OCCUPATION && e1.getType() == COLOR) {
            NamedEntity[] adder =
            {new NamedEntity(NamedEntity.makeName("UNKNOWN"), PERSON),
             e0, e1};
            negatives.add(adder);
        }
    }

    /** Asserts that E exists (e.g., Tom lives around here). */
    void exists(NamedEntity e) {
        if (e.getType() == PERSON) {
            NamedEntity[] adder =
            {e, new NamedEntity(NamedEntity.makeName("UNKNOWN"),
                                OCCUPATION),
             new NamedEntity(NamedEntity.makeName("UNKNOWN"), COLOR)};
            exist.add(adder);
        }
        if (e.getType() == COLOR) {
            NamedEntity[] adder =
            {new NamedEntity(NamedEntity.makeName("UNKNOWN"), PERSON),
             new NamedEntity(NamedEntity.makeName("UNKNOWN"),
                             OCCUPATION), e};
            exist.add(adder);
        }
        if (e.getType() == OCCUPATION) {
            NamedEntity[] adder =
            {new NamedEntity(NamedEntity.makeName("UNKNOWN"), PERSON),
             e, new NamedEntity(NamedEntity.makeName("UNKNOWN"),
                                COLOR)};
            exist.add(adder);
        }
    }

    /** Gets rid of non associated elements
     *  in the permute list. */
    void association() {
        int a = combo.size();
        int b = combo.get(0).size();
        ArrayList<ArrayList<NamedEntity[]>> hold =
            new ArrayList<ArrayList<NamedEntity[]>>();
        if (triples.size() > 0) {
            for (int i = 0; i < triples.size(); i += 1) {
                for (int z = 0; z < combo.size();) {
                    boolean match = false;
                    for (int k = 0; k < b; k += 1) {
                        int count = 0;
                        for (int j = 0; j < 3; j += 1) {
                            if (triples.get(i)[j].getName().equals
                                (combo.get(z).get(k)[j].getName())) {
                                count += 1;
                            }
                            if (count == 2) {
                                match = true;
                            }
                        }
                    }
                    if (!match) {
                        combo.remove(z);
                    } else {
                        z += 1;
                    }
                }
            }
        }
    }

    /** Gets rid of the disassociations. */
    void disassociation() {
        int a = combo.size();
        if (negatives.size() > 0 && combo.size() > 0) {
            ArrayList<ArrayList<NamedEntity[]>> hold =
                new ArrayList<ArrayList<NamedEntity[]>>();
            for (int i = 0; i < negatives.size(); i += 1) {
                for (int z = 0; z < combo.size();) {
                    boolean match = false;
                    for (int k = 0; k < combo.get(0).size(); k += 1) {
                        int count = 0;
                        for (int j = 0; j < 3; j += 1) {
                            if (negatives.get(i)[j].getName().equals
                                (combo.get(z).get(k)[j].getName())) {
                                count += 1;
                            }
                            if (count == 2) {
                                match = true;
                            }
                        }
                    }
                    if (match) {
                        combo.remove(z);
                    } else {
                        z += 1;
                    }
                }
            }
        }
    }

    /** Return a list of all possible associations of entities that include
     *  the entity named ID.  For example, knownAbout("Tom") might
     *  contain a list of the lists:
     *     [ (Tom, PERSON), (plumber, OCCUPATION), (green, COLOR) ],
     *     [ (Tom, PERSON), (plumber, OCCUPATION), (color#1, COLOR) ]
     *  (where color#1 denotes an anonymous color.). */
    List<List<NamedEntity>> knownAbout(String id) {
        return null;
    }

    /** Return true iff the current set of facts is impossible. */
    boolean impossible() {
        if (combo.size() == 0) {
            return true;
        }
        return false;
    }

    /** Sets combo to a set of all permutations. */
    void permutes() {
        int count = 0;
        samesize();
        ArrayList<int[]> permjobs =
            allPerms(occupations.size());
        ArrayList<int[]> permcolors =
            allPerms(colors.size());
        for (int i = 0; i < permjobs.size(); i += 1) {
            for (int k = 0; k < permcolors.size(); k += 1) {
                ArrayList<NamedEntity[]> result =
                     new ArrayList<NamedEntity[]>();
                for (int z = 0; z < people.size(); z += 1) {
                    NamedEntity[] adder = new NamedEntity[3];
                    adder[0] = people.get(z);
                    adder[1] = occupations.get(permjobs.get(i)[z]);
                    adder[2] = colors.get(permcolors.get(k)[z]);
                    result.add(adder);
                }
                copy.add(result);
                combo.add(result);
            }
        }
    }

    /** Gives permutations.
     * @param A integer arraylist
     * @return ArrayList<int[]> */
    ArrayList<int[]> allPerms(int A) {
        int[] holder;
        holder = new int[A];
        ArrayList<int[]> result = new ArrayList<int[]>();
        for (int i = 0; i < A; i += 1) {
            holder[i] = i;
        }
        do {
            int[] x;
            x = new int[holder.length];
            for (int i = 0; i < holder.length; i += 1) {
                x[i] = holder[i];
            }
            result.add(x);
        } while(nextPerm(holder));
        return result;
    }

    /** Tells if another permutation exists. Taken from lab5.
     * @param A integer array
     * @return boolean */
    static boolean nextPerm(int[] A) {
        int N = A.length;
        int k;
        boolean[] S = new boolean[N];
        k = N - 1;
        while (k >= 0) {
            int v;
            for (v = A[k] + 1; v < N; v += 1) {
                if (S[v]) {
                    S[v] = false;
                    S[A[k]] = true;
                    A[k] = v;
                    for (int i = 0; i < N; i += 1) {
                        if (S[i]) {
                            k += 1;
                            A[k] = i;
                        }
                    }
                    return true;
                }
            }
            S[A[k]] = true;
            k -= 1;
        }
        return false;
    }

    /** Checks for repeated words. */
    void nodups() {
        for (int i = 0; i < people.size(); i += 1) {
            for (int j = 0; j < occupations.size(); j += 1) {
                for (int k = 0; k < colors.size(); k += 1) {
                    if (people.get(i).getName().equals(
                            occupations.get(j).getName())) {
                        throw new PuzzleException("Name comes up twice.");
                    }
                    if (people.get(i).getName().equals(
                            colors.get(k).getName())) {
                        throw new PuzzleException("Name comes up twice.");
                    }
                    if (colors.get(k).getName().equals(
                            occupations.get(j).getName())) {
                        throw new PuzzleException("Name comes up twice.");
                    }
                }
            }
        }
    }

    /** Checks to see if colors, occupations, people list match. */
    void samesize() {
        int a = Math.max(people.size(),
                         Math.max(occupations.size(), colors.size()));
        while (people.size() < a) {
            people.add(new NamedEntity(NamedEntity.makeName("PERSON"), PERSON));
        }
        while (occupations.size() < a) {
            occupations.add(new NamedEntity(NamedEntity.makeName("OCCUPATION"),
                                            OCCUPATION));
        }
        while (colors.size() < a) {
            colors.add(new NamedEntity(NamedEntity.makeName("COLOR"), COLOR));
        }
    }
}
