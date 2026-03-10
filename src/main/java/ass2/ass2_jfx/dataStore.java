package ass2.ass2_jfx;

import java.util.ArrayList;

public class dataStore {

    private static dataStore instance;

    private final ArrayList<Question> questions = new ArrayList<>();
    private final ArrayList<Player> players = new ArrayList<>();

    private dataStore() {
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
    }

    public static dataStore getInstance() {
        if (instance == null) {
            instance = new dataStore();
        }
        return instance;
    }

    public ArrayList<Question> getQuestions() { return questions; }
    public ArrayList<Player> getPlayers() { return players; }
}
