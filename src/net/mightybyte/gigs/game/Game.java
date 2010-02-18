package net.mightybyte.gigs.game;

import java.util.List;

/**
 * These are the basic methods that need to be implemented for a game. These
 * methods should take care of all of the I/O for the game. This removes the
 * need for the game-related commands to implement I/O. This implies that each
 * game should have a list of all the players so that it can send each of them
 * the required I/O.
 */
public interface Game {
  /**
   * Initializes any necessary game parameters.  If the game parameters affect
   * construction of game data structures, then construction should be done here.
   * The init() method will *always* be called after the constructor and before
   * the start() method.
   * 
   * @param params A string containing parameters unique to this type of game.
   */
  public void init(String params);
  
  /**
   * Starts the game
   */
  public void start();

  /**
   * Adds a player to the game
   * 
   * @param player
   *          The player to be added
   * @return true if the player could be added, false otherwise
   */
  public boolean addPlayer(String player);

  /**
   * Get a list of all the players in the game
   * 
   * @return the players
   */
  public List<String> getPlayers();

  /**
   * Get the number of players currently in the games
   * 
   * @return the number of players
   */
  public int getNumPlayers();

  /**
   * Checks to see if the move looks like a well-formed move
   * @param move the move string
   * @return true if the move is well formed, false otherwise
   */
  public boolean isWellFormedMove(String move);
  
  /**
   * Make a move. If this move results in the end of a turn, then the method
   * should return true, otherwise false. If the move is illegal or it is not
   * the player's turn, then an IllegalArgumentException is thrown.  The server
   * will always call isWellFormedMove() before makeMove().
   * 
   * @param player
   *          The name of the player making the move
   * @param move
   *          The move string
   * @throws InvalidInputException
   *           if the move is illegal.
   * @return a boolean flag indicating the end of a turn
   */
  public boolean makeMove(String player, String move)
      throws IllegalArgumentException;

  /**
   * Get a list of the players who are on move
   * 
   * @return list of players
   */
  public List<String> getPlayersOnMove();

  /**
   * Returns true if the game is over, false otherwise.
   * 
   * @return True if the game is over, false otherwise.
   */
  public boolean isGameOver();

  /**
   * Get a player's score. If the game is not over, then the return value is not
   * defined.
   * 
   * @param player
   *          The name of the player
   * @return The player's score
   */
  public double getPlayerScore(String player);

  /**
   * Gets the game summary string. Currently, this string is just a list of the
   * players in the game.
   * 
   * @return The game summary string
   */
  public String getPlayerString();

  /**
   * Gets the result string. This only works when the game is over.
   * 
   * @return the result string.
   */
  public String getResultString();

  /**
   * Gets the complete game state represented as a string. In games of perfect
   * information (i.e. chess, or pente) this method could be the same as
   * getHumanReadableState() or getMachineReadableState(). But in games of
   * imperfect information, there may be some state information that should not
   * be seen by the players. This method returns the complete game state
   * including all the information that is unseen by the players. In games of
   * chance, this will probably include randomization data that enables the game
   * to be exactly reconstructed. This method is basically equivalent to
   * serialization.
   * 
   * @return the complete state.
   */
  public String getCompleteState();

  /**
   * Gets a string representation of the game for a specific player. Some games
   * have state information that should not be visible to some players. The
   * player is passed to this method to allow the game to return the correct
   * information for that player.
   * 
   * @param player
   *          the player for which the state is being generated
   * @return The game state
   */
  public String getHumanReadableState(String player);

  /**
   * Gets a string representation of the game state that is machine readable.
   * 
   * @param player
   *          the player for which the state is being generated
   * @return The game state
   */
  public String getMachineReadableState(String player);

  /**
   * Deep clone
   * 
   * @return a deep clone of the object.
   */
  public Game copy();

}
