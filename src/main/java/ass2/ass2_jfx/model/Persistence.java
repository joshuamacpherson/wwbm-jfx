package ass2.ass2_jfx.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Handles saving and loading game data using a simple custom text format.
 *
 * This class is responsible for persisting:
 * - All questions
 * - All players
 * Data is stored in a plain text file inside the user's home directory.
 * The file will be created automatically if it doesn't exist.
 *
 * No external dependencies required — uses only java.nio and java.util.
 *
 * File format:
 *   QUESTION|answer0|answer1|answer2|answer3|correctIndex
 *   PLAYER|name|money
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class Persistence {

    /** Path to the save file inside the user's home directory. */
    private static final Path FILE_PATH = Path.of(
            System.getProperty("user.home"),
            "wwtbm-save.txt"
    );

    /** Delimiter used to separate fields in each line. */
    private static final String DELIM = "|";

    /** Escaped delimiter for use in String.split() regex. */
    private static final String DELIM_REGEX = "\\|";

    /**
     * Saves all questions and players to disk.
     *
     * @param questions the list of questions to persist
     * @param players   the list of players to persist
     */
    public static void save(ArrayList<Question> questions, ArrayList<Player> players) {
        StringBuilder sb = new StringBuilder();

        for (Question q : questions) {
            int correctIndex = 0;
            for (int i = 0; i < 4; i++) {
                if (q.isCorrect(i)) {
                    correctIndex = i;
                    break;
                }
            }
            sb.append("QUESTION")
                    .append(DELIM).append(escape(q.getQuestionText()));

            for (String answer : q.getAnswers()) {
                sb.append(DELIM).append(escape(answer));
            }

            sb.append(DELIM).append(correctIndex)
                    .append("\n");
        }

        for (Player p : players) {
            sb.append("PLAYER")
                    .append(DELIM).append(escape(p.getName()))
                    .append(DELIM).append(p.getPlayerMoney())
                    .append("\n");
        }

        try {
            Files.writeString(FILE_PATH, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads questions and players from disk into memory.
     * If the file does not exist, nothing is loaded.
     *
     * @param questions the question list to populate
     * @param players   the player list to populate
     */
    public static void load(ArrayList<Question> questions, ArrayList<Player> players) {
        if (!Files.exists(FILE_PATH)) {
            return;
        }

        try {
            String content = Files.readString(FILE_PATH);
            questions.clear();
            players.clear();

            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(DELIM_REGEX, -1);

                if (parts[0].equals("QUESTION") && parts.length == 7) {
                    String qText = unescape(parts[1]);
                    String[] answers = new String[]{
                            unescape(parts[2]),
                            unescape(parts[3]),
                            unescape(parts[4]),
                            unescape(parts[5])
                    };
                    int correct = Integer.parseInt(parts[6]);
                    questions.add(new Question(qText, answers, correct));

                } else if (parts[0].equals("PLAYER") && parts.length == 3) {
                    Player p = new Player(unescape(parts[1]));
                    p.setPlayerMoney(Integer.parseInt(parts[2]));
                    players.add(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Escapes the pipe delimiter and newlines in field values so they
     * don't break the line-based format.
     */
    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\n", "\\n");
    }

    /**
     * Reverses the escaping applied by {@link #escape(String)}.
     */
    private static String unescape(String value) {
        return value
                .replace("\\n", "\n")
                .replace("\\p", "|")
                .replace("\\\\", "\\");
    }
}