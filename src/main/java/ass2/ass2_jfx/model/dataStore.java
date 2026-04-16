package ass2.ass2_jfx.model;

import java.util.ArrayList;
/**
 * Central in‑memory storage for all game data.
 * Implements a Singleton to ensure a single shared source of truth
 * for questions, players, and the currently selected player.
 *
 *  @author Shane O'Connell
 *  @author Joshua MacPherson
 *  @version Java 21
 */
public class dataStore {
    /** Singleton instance of the dataStore. */
    private static dataStore instance;
    /** The player currently selected for Play Mode. */
    private Player currentPlayer;
    /** List of all predefined and user‑added questions. */
    private final ArrayList<Question> questions = new ArrayList<>();
    /** List of all players available in Design and Play modes. */
    private final ArrayList<Player> players = new ArrayList<>();

    /** Contains default questions and player. */
    private void loadDefaultData() {
        questions.add(new Question("What is the capital of France?",
                new String[]{"Berlin", "Madrid", "Paris", "Rome"}, 2));
        questions.add(new Question("What is 2 + 2?",
                new String[]{"3", "4", "5", "6"}, 1));
        questions.add(new Question("Which planet is closest to the Sun?",
                new String[]{"Venus", "Earth", "Mars", "Mercury"}, 3));
        questions.add(new Question("What is the chemical symbol for water?",
                new String[]{"O2", "H2O", "CO2", "HO"}, 1));
        questions.add(new Question("Who wrote Romeo and Juliet?",
                new String[]{"Dickens", "Tolkien", "Shakespeare", "Austen"}, 2));
        questions.add(new Question("How many sides does a hexagon have?",
                new String[]{"5", "7", "8", "6"}, 3));
        questions.add(new Question("What is the largest ocean on Earth?",
                new String[]{"Atlantic", "Indian", "Arctic", "Pacific"}, 3));
        questions.add(new Question("What is the square root of 64?",
                new String[]{"6", "7", "8", "9"}, 2));
        questions.add(new Question("Which country invented pizza?",
                new String[]{"France", "Italy", "Greece", "Spain"}, 1));
        questions.add(new Question("What gas do plants absorb from the atmosphere?",
                new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"}, 2));
        questions.add(new Question("What is the fastest land animal?",
                new String[]{"Lion", "Horse", "Cheetah", "Leopard"}, 2));
        questions.add(new Question("How many continents are on Earth?",
                new String[]{"5", "6", "8", "7"}, 3));
        questions.add(new Question("What is the hardest natural substance?",
                new String[]{"Gold", "Iron", "Diamond", "Quartz"}, 2));
        questions.add(new Question("Which element has the atomic number 1?",
                new String[]{"Helium", "Oxygen", "Hydrogen", "Carbon"}, 2));
        questions.add(new Question("What year did World War II end?",
                new String[]{"1943", "1944", "1946", "1945"}, 3));
        players.add(new Player("Default Player"));
    }

    /**
     * Saves the current in-memory game data to disk.
     */
    public void saveData() {
        jsonPersistence.save(questions, players);
    }

    /**
     * Initializes the datastore by attempting to load
     * previously saved data from program.
     */
    private dataStore() {
        jsonPersistence.load(questions, players);
        // If no saved data exists, load defaults
        if (questions.isEmpty()) {
            loadDefaultData();
        }
    }

    /**
     * Returns the Singleton instance of the datastore.
     * @return the shared dataStore instance
     */
    public static dataStore getInstance() {
        if (instance == null) instance = new dataStore();
        return instance;
    }

    /**
     * Returns the list of all questions.
     * @return the question list
     */
    public ArrayList<Question> getQuestions() { return questions; }

    /**
     * Returns the list of all players.
     * @return the player list
     */
    public ArrayList<Player> getPlayers() { return players; }

    /**
     * Returns the currently selected player.
     * @return the active player
     */
    public Player getCurrentPlayer() { return currentPlayer; }

    /**
     * Sets the player to be used in Play Mode.
     * @param player the selected player
     */
    public void setCurrentPlayer(Player player) { this.currentPlayer = player; }
}