package ass2.ass2_jfx.model;

/**
 * Represents a player in the application.
 *
 * A Player contains:
 * - A unique name
 * - The amount of money earned
 * - The current tier level
 *
 * This class can be used in both:
 * - Design Mode (CRUD management)
 * - Play Mode (gameplay logic)
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class Player extends Person {
    /** The total money the player has earned. */
    private int playerMoney;
    /** The player's current tier level. */
    private int playerTier;

    /**
     * Constructs a new Player with the specified name.
     * Money and tier are initialized to zero.
     *
     * @param name the player's name
     */
    public Player(String name) {
        super(name);
        this.playerMoney = 0;
        this.playerTier = 0;
    }

    /**
     * Returns the player's current money total.
     *
     * @return the player's money
     */
    public int getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(int amount) {
        playerMoney = amount;
    }

    /**
     * Adds the specified amount of money to the player.
     * @param amount the amount of money to add
     */
    public void addMoneyToPlayer(int amount) {
        this.playerMoney += amount;
    }

    /** Resets the player's money to zero. */
    public void resetPlayerMoney() {
        this.playerMoney = 0;
    }

    /** Resets the player's tier level to zero. */
    public void resetPlayerTier() {
        playerTier = 0;
    }

    /**
     * Returns the player's name for display purposes.i
     * @return the player's name + the players' money.
     */
    @Override
    public String toString() {
        return String.format("%s (Money: %d)", getName(), playerMoney);
    }
}