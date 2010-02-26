package net.mightybyte.gigs.game;

import java.util.List;

import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;

public class PendingGame {
  private static ServerState serverState = ServerState.getInstance();

  private String gameType;
  private String gameParams;
  private int maxPlayers;
  private int timeBase;
  private int timeInc;
  private String gameOwner;
  private ServerGame serverGame;

  public PendingGame(String type, String params, String firstPlayer,
      int maxPlayers, int base, int inc) {
    gameType = type;
    gameParams = params;
    this.maxPlayers = maxPlayers;
    timeBase = base;
    timeInc = inc;
    gameOwner = firstPlayer;

    // Create the actual game object
    serverGame = new ServerGame(this);
    serverGame.addPlayer(firstPlayer);
  }

  /**
   * Start the game
   */
  public void start() {
    serverState.removePendingGame(this);
    int gameIndex = serverState.addNewGame(serverGame);

    // Remove this game from all players' pending lists and add the game ID
    // to their list of current games.
    for (String pName : serverGame.getPlayers()) {
      ConnectedUser cUser = serverState.getUsers().getUser(pName);
      cUser.getIncomingGames().clear();
      cUser.getOutgoingGames().clear();
      cUser.addCurrentGame(gameIndex);

      cUser.getConnection().writelnToClient("");
      cUser.getConnection().writelnToClient(
          "Creating: " + serverGame.getGameString());
      cUser.getConnection().writelnToClient(
          "{" + serverGame.getGameString() + " creating game.}");
      // Creating: alice (1234) bob (++++) unrated atomic 3 0
      // {Game 80 (alice vs. bob) Creating unrated atomic match.}

    }

    serverGame.start();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(gameType);
    sb.append(" ");
    sb.append(timeBase);
    sb.append(" ");
    sb.append(timeInc);
    return sb.toString();
  }

  public String getGameType() {
    return gameType;
  }

  public void setGameType(String gameType) {
    this.gameType = gameType;
  }

  public String getGameParams() {
    return gameParams;
  }

  public int getTimeBase() {
    return timeBase;
  }

  public void setTimeBase(int timeBase) {
    this.timeBase = timeBase;
  }

  public int getTimeInc() {
    return timeInc;
  }

  public void setTimeInc(int timeInc) {
    this.timeInc = timeInc;
  }

  /**
   * Add a player to the game
   * 
   * @param player
   *          the name of the player ot add
   * @throws Exception
   *           if the game is full
   */
  public void addPlayer(String player) throws Exception {
    List<String> players = serverGame.getPlayers(); 
    if (players.size() >= maxPlayers) {
      throw new Exception("Game is full");
    }
    for (String name : players) {
      serverState.writeToPlayer(name, player + " joined game ("
          + (players.size() + 1) + " players total)");
    }
    serverGame.addPlayer(player);
  }

  /**
   * Add a player to the game
   * 
   * @param player
   *          the name of the player ot add
   */
  public void removePlayer(String player) {
    serverGame.removePlayer(player);

    List<String> players = serverGame.getPlayers(); 
    for (String name : players) {
      serverState.writeToPlayer(name, player + " left game ("
          + (players.size() - 1) + " players total)");
    }
  }

  /**
   * Gets the player list.
   * @return the player list
   */
  public List<String> getPlayers() {
    return serverGame.getPlayers();
  }
  
  /**
   * Checks if the specified player is the owner of this game.
   * 
   * @param player
   *          the player name
   * @return true if the player owns the game, false otherwise
   */
  public boolean isOwner(String player) {
    return player.equals(this.gameOwner);
  }

  /**
   * @return Returns the maxPlayers.
   */
  public int getMaxPlayers() {
    return maxPlayers;
  }

  /**
   * @param maxPlayers
   *          The maxPlayers to set.
   */
  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  /**
   * Return whether the game is full.
   * 
   * @return the game full flag
   */
  public boolean isFull() {
    return serverGame.getNumPlayers() == maxPlayers;
  }

}
