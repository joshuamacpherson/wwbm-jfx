package ass2.ass2_jfx.model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Handles saving and loading game data using JSON format.
 *
 * This class is responsible for persisting:
 * - All questions
 * - All player
 * Data is stored in a JSON file inside the user's home directory.
 * The file will be created automatically if it doesn't exist.
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class JsonPersistence {

    /** Path to the save file inside the user's home directory. */
    private static final Path FILE_PATH = Path.of(
            System.getProperty("user.home"),
            "wwtbm-save.json"
    );

    /**
     * Saves all questions and players to disk as JSON.
     *
     * @param questions the list of questions to persist
     * @param players   the list of players to persist
     */
    public static void save(ArrayList<Question> questions, ArrayList<Player> players) {
        try {
            JSONObject root = new JSONObject();
            JSONArray questionArray = new JSONArray();

            for (Question q : questions) {
                JSONObject qObj = new JSONObject();

                // Store question text
                qObj.put("question", q.getQuestionText());

                // Store answer choices
                JSONArray answerArray = new JSONArray();
                for (String answer : q.getAnswers()) {
                    answerArray.put(answer);
                }
                qObj.put("answers", answerArray);

                // Determine which index is correct
                int correctIndex = 0;
                for (int i = 0; i < 4; i++) {
                    if (q.isCorrect(i)) {
                        correctIndex = i;
                        break;
                    }
                }
                qObj.put("correct", correctIndex);
                questionArray.put(qObj);
            }
            JSONArray playerArray = new JSONArray();

            for (Player p : players) {
                JSONObject pObj = new JSONObject();
                // Store player name and money
                pObj.put("name", p.getName());
                pObj.put("money", p.getPlayerMoney());
                playerArray.put(pObj);
            }

            root.put("questions", questionArray);
            root.put("players", playerArray);
            // Write formatted JSON
            Files.writeString(FILE_PATH, root.toString(4));

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
        try {
            // If no save file exists, exit early
            if (!Files.exists(FILE_PATH)) {
                return;
            }

            String content = Files.readString(FILE_PATH);
            JSONObject root = new JSONObject(content);

            // Clear existing in-memory data before loading
            questions.clear();
            players.clear();

            JSONArray questionArray = root.getJSONArray("questions");

            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject qObj = questionArray.getJSONObject(i);
                String qText = qObj.getString("question");
                JSONArray answerArray = qObj.getJSONArray("answers");
                String[] answers = new String[4];

                for (int j = 0; j < answerArray.length(); j++) {
                    answers[j] = answerArray.getString(j);
                }

                int correct = qObj.getInt("correct");

                questions.add(new Question(qText, answers, correct));
            }
            JSONArray playerArray = root.getJSONArray("players");

            for (int i = 0; i < playerArray.length(); i++) {
                JSONObject pObj = playerArray.getJSONObject(i);
                Player p = new Player(pObj.getString("name"));
                p.setPlayerMoney(pObj.getInt("money"));
                players.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}