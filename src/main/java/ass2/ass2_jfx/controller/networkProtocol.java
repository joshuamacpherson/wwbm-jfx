package ass2.ass2_jfx.controller;

public class networkProtocol {
    public static final String SEP = "|";
    public static final String CONNECT = "CONNECT";
    public static final String START = "START";
    public static final String QUESTION = "QUESTION";
    public static final String ANSWER = "ANSWER";
    public static final String RESULT = "RESULT";
    public static final String CHAT = "CHAT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String ERROR = "ERROR";

    public static String build(String... parts) {
        return String.join(SEP, parts);
    }

    public static String[] parse(String message) {
        return message.split("\\|", -1);
    }
}