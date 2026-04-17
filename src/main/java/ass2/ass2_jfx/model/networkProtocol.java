package ass2.ass2_jfx.model;

/**
 * Defines the message protocol used for communication between the game client and server.
 * All messages are pipe-delimited strings built and parsed using the
 * static utility methods in this class. Each message begins with a
 * message type constant followed by any relevant fields.
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class networkProtocol {

    /** Message type constants and delimiter used for all client-server communication. */
    public static final String SEP = "|", CONNECT = "CONNECT", START = "START",
            QUESTION = "QUESTION", ANSWER = "ANSWER", RESULT = "RESULT",
            CHAT = "CHAT", DISCONNECT = "DISCONNECT", ERROR = "ERROR";

    /**
     * Builds a pipe-delimited message string from the provided parts.
     * @param parts the message type followed by any additional fields
     * @return a single string with all parts joined by the delimiter
     */
    public static String build(String... parts) {
        return String.join(SEP, parts);
    }

    /**
     * Parses a pipe-delimited message string into its individual fields.
     * Trailing empty strings are preserved using a limit of -1.
     * @param message the raw message string to parse
     * @return an array of fields split by the delimiter
     */
    public static String[] parse(String message) {
        return message.split("\\|", -1);
    }
}