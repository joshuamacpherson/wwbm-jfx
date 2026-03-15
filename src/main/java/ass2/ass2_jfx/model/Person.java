package ass2.ass2_jfx.model;

/**
 * Basic entity representing a person with a name.
 *
 *  @author Shane O'Connell
 *  @author Joshua MacPherson
 *  @version Java 21
 */
public class Person {

    /** The person's name. */
    private final String name;

    /**
     * Creates a new Person.
     * @param name the person's name
     */
    Person(String name) { this.name = name; }

    /**
     * Returns the person's name.
     * @return the name
     */
    public String getName() { return name; }
}