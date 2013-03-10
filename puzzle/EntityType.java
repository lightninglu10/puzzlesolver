package puzzle;

/** Symbolic constants that identify types of puzzle entity.
 * @author Patrick Lu
 */
enum EntityType {
    /** Symbolic constants identifying persons' names, persons' occupations,
     *  and house colors. */
    PERSON, OCCUPATION, COLOR;

    /*
     *  [Java note: An enum is a class that automatically defines its set
     *  of instantiations, giving each of these objects a name. PERSON,
     *  OCCUPATION, and COLOR are all constant (final) and static.  They
     *  can be compared with each other with == and compareTo, used as the
     *  and cases in 'switch' statements, and converted to the integer
     *  values 0, 1, and 2, respectively, by the method .ordinal().
     *  Thus, the ordinal method allows their use as array indices.
     *
     *  To use the enumeration constants without having to qualify them with
     *  'Entity', use
     *     import static puzzle.EntityType.*;
     *
    */

}
