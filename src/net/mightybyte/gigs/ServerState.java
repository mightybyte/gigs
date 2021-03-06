package net.mightybyte.gigs;

import java.util.ArrayList;
import java.util.List;

import net.mightybyte.gigs.game.GameFactory;
import net.mightybyte.gigs.game.PendingGame;
import net.mightybyte.gigs.game.ServerGame;

/**
 * This the top-level server object that holds all the information about the
 * current state of the server. I think this should be a singleton so that it
 * doesn't have to be passed around, but that might not be the best way to do
 * it.
 */
public class ServerState {
  private static final ServerState instance = new ServerState();

  public final GameFactory gameFactory;

  private CurrentUsers users;

  private List<ServerGame> currentGames;
  private List<PendingGame> pendingGames;
  private List<PendingGame> soughtGames;

  public static ServerState getInstance() {
    return instance;
  }

  private ServerState() {
    gameFactory = new GameFactory("GameTypes.props");
    users = new CurrentUsers();
    currentGames = new ArrayList<ServerGame>();
    pendingGames = new ArrayList<PendingGame>();
    soughtGames = new ArrayList<PendingGame>();
  }

  public CurrentUsers getUsers() {
    return users;
  }

  /**
   * Get the current games that are in progress
   * 
   * @return the games
   */
  public List<ServerGame> getCurrentGames() {
    return currentGames;
  }

  /**
   * Get the ServerGame represented by the specified game ID.
   * 
   * @param gameID
   *          the game ID
   * @return The ServerGame
   */
  public ServerGame getGame(int gameID) {
    return currentGames.get(gameID);
  }

  /**
   * Removes an in-progress game from the server. This method does not do any
   * ratings calculation or storage, so it is applicable to all commands that
   * end games.
   * 
   * @param gameID
   */
  public void removeGame(int gameID) {
    ServerGame g = currentGames.get(gameID);
    currentGames.set(gameID, null);
//    for (String player : g.getPlayers()) {
//      users.getUser(player).getCurrentGames().remove(new Integer(gameID));
//    }
  }

  /**
   * Adds a new game and returns the game ID.
   * 
   * @param game
   *          The Game to add
   * @return The game ID
   */
  public int addNewGame(ServerGame game) {
    return addGeneric(currentGames, game);
  }

  /**
   * Adds a new pending game
   * 
   * @param game
   */
  public int addPendingGame(PendingGame game) {
    return addGeneric(pendingGames, game);
  }

  /**
   * Removes a pending game
   * 
   * @param game
   *          the game to remove
   */
  public void removePendingGame(PendingGame game) {
    int index = pendingGames.indexOf(game);
    if (index != -1) {
      pendingGames.set(index, null);
    }
  }

  /**
   * Get the current games that are in progress
   * 
   * @return the games
   */
  public Iterable<PendingGame> getPendingGames() {
    return pendingGames;
  }

  /**
   * Get the pending game represented by the specified game ID.
   * 
   * @param gameID
   *          the game ID
   * @return The ServerGame
   */
  public PendingGame getPendingGame(int gameID) {
    return pendingGames.get(gameID);
  }

  /**
   * Removes a sought game
   * 
   * @param game
   *          the game to remove
   */
  public void removeSoughtGame(PendingGame game) {
    int index = soughtGames.indexOf(game);
    if (index != -1) {
      soughtGames.set(index, null);
    }
  }

  /**
   * Adds a new sought game
   * 
   * @param game
   */
  public int addSoughtGame(PendingGame game) {
    return addGeneric(soughtGames, game);
  }
  
  /**
   * Get the current games that are in progress
   * 
   * @return the games
   */
  public List<PendingGame> getSoughtGames() {
    return soughtGames;
  }

  /**
   * Get the sought game represented by the specified game ID.
   * 
   * @param gameID
   *          the game ID
   * @return The ServerGame
   */
  public PendingGame getSoughtGame(int gameID) {
    return soughtGames.get(gameID);
  }

  public void writeToPlayer(String player, String message) {
    users.getUser(player).getConnection().writeToClientPrompt("\n" + message);
  }

  /**
   * Adds a new pending game
   * 
   * @param game
   */
  public <T> int addGeneric(List<T> list, T game) {
    int index = -1;
    for (index = 0; index < list.size(); index++) {
      if (list.get(index) == null) {
        break;
      }
    }
    if (index == list.size()) {
      list.add(game);
    } else {
      list.set(index, game);
    }
    return index;
  }

}
