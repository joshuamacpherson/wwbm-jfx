package ass2.ass2_jfx.controller;

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

    /** Delimiter used to separate fields in all messages. */
    public static final String SEP = "|";
    /** Message type sent by a client when first connecting, followed by the player name. */
    public static final String CONNECT = "CONNECT";
    /** Message type sent by the server to signal the game has started, followed by player count. */
    public static final String START = "START";
    /** Message type sent by the server to deliver a question and its four answer choices. */
    public static final String QUESTION = "QUESTION";
    /** Message type sent by a client to submit an answer index for the current question. */
    public static final String ANSWER = "ANSWER";
    /** Message type sent by the server with the result of a player's answer and money earned. */
    public static final String RESULT = "RESULT";
    /** Message type used for chat messages between players, forwarded by the server. */
    public static final String CHAT = "CHAT";
    /** Message type sent when a client or server is disconnecting from the session. */
    public static final String DISCONNECT = "DISCONNECT";
    /** Message type sent by the server to report an error condition. */
    public static final String ERROR = "ERROR";

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