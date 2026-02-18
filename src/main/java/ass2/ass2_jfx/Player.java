package ass2.ass2_jfx;

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
 */
public class Player {

    /** The player's name. */
    private final String name;

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
        this.name = name;
        this.playerMoney = 0;
        this.playerTier = 0;
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's current money total.
     *
     * @return the player's money
     */
    public int getPlayerMoney() {
        return playerMoney;
    }

    /**
     * Adds the specified amount of money to the player.
     *
     * @param amount the amount of money to add
     */
    public void addMoneyToPlayer(int amount) {
        this.playerMoney += amount;
    }

    /**
     * Resets the player's money to zero.
     */
    public void resetPlayerMoney() {
        this.playerMoney = 0;
    }

    /**
     * Returns the player's current tier level.
     *
     * @return the player's tier
     */
    public int getPlayerTier() {
        return playerTier;
    }

    /**
     * Advances the player to the next tier level.
     */
    public void boostPlayerTier() {
        playerTier++;
    }

    /**
     * Resets the player's tier level to zero.
     */
    public void resetPlayerTier() {
        playerTier = 0;
    }

    /**
     * Returns the player's name.
     * Used automatically when displayed in a ListView.
     *
     * @return the player's name
     */
    @Override
    public String toString() {
        return name;
    }
}