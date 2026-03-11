package ass2.ass2_jfx;
/**
 * Basic entity representing a person with a name.
 */
public class Person {

    /** The person's name. */
    private String name;

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