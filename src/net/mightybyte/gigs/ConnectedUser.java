package net.mightybyte.gigs;

import java.util.ArrayList;
import java.util.List;

import net.mightybyte.gigs.game.PendingGame;
import net.mightybyte.gigs.game.ServerGame;

public class ConnectedUser {
  private String name;

  private ClientConnection connection;

  /**
   * List of games that the user is currently playing.
   */
  private List<Integer> currentGames;

  /**
   * List of games that the user has joined but have not started yet.
   */
  private List<PendingGame> joinedGames;

  /**
   * List of games that the user is observing.
   */
  private List<Integer> observedGames;

  /**
   * List of incoming game requests
   */
  private List<PendingGame> incomingGames;

  /**
   * Lost of outgoing game requests
   */
  private List<PendingGame> outgoingGames;

  private String lastTalkedTo;

  // Later this will probably hold user status information such as
  // whether they're playing a game, shout status, etc.

  public ConnectedUser(String user, ClientConnection con) {
    this.name = user;
    this.connection = con;
    currentGames = new ArrayList<Integer>();
    joinedGames = new ArrayList<PendingGame>();
    observedGames = new ArrayList<Integer>();
    incomingGames = new ArrayList<PendingGame>();
    outgoingGames = new ArrayList<PendingGame>();
    lastTalkedTo = "";
  }

  public void cleanupUser() {
    ServerState s = ServerState.getInstance();
    for (int gameID : currentGames) {
      s.removeGame(gameID);
    }
    currentGames.clear();
    
    for (int gameID : observedGames) {
      s.getGame(gameID).removeObserver(name);
    }
    observedGames.clear();
    
    for (PendingGame g : joinedGames) {
      g.removePlayer(name);
    }
    joinedGames.clear();

    for (PendingGame g : incomingGames) {
      g.removePlayer(name);
      if ( g.getMaxPlayers() == 2 ) {
        ServerState.getInstance().removePendingGame(g);
      }
    }
    incomingGames.clear();

    for (PendingGame g : outgoingGames) {
      ServerState.getInstance().removePendingGame(g);
    }
    outgoingGames.clear();
  }

  public ClientConnection getConnection() {
    return connection;
  }

  public void setConnection(ClientConnection connection) {
    this.connection = connection;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Adds a PendingGame to teh outgoing games list.
   * 
   * @param game
   *          The PendingGame to add
   */
  public void addOutgoingGame(PendingGame game) {
    outgoingGames.add(game);
  }

  /**
   * Adds a PendingGame to the incoming games list.
   * 
   * @param game
   *          The PendingGame to add
   */
  public void addIncomingGame(PendingGame game) {
    incomingGames.add(game);
  }

  /**
   * Get the list of incoming games
   * 
   * @return the list
   */
  public List<PendingGame> getIncomingGames() {
    return incomingGames;
  }

  /**
   * Get the list of outgoing games
   * 
   * @return the list
   */
  public List<PendingGame> getOutgoingGames() {
    return outgoingGames;
  }

  /**
   * Add a game to this player's list of games that are in progress.
   * 
   * @param gameID
   *          The server game ID
   */
  public void addCurrentGame(int gameID) {
    currentGames.add(gameID);
  }

  /**
   * Add a game to the list of games that this player is observing.
   * 
   * @param gameID
   *          the server game ID
   */
  public void addObservedGame(int gameID) {
    observedGames.add(gameID);
  }

  /**
   * @return Returns the list of observed games.
   */
  public List<Integer> getObservedGames() {
    return observedGames;
  }

  /**
   * @return Returns the currentGames.
   */
  public List<Integer> getCurrentGames() {
    return currentGames;
  }

  /**
   * @return Returns the lastTalkedTo.
   */
  public String getLastTalkedTo() {
    return lastTalkedTo;
  }

  /**
   * @param lastTalkedTo
   *          The lastTalkedTo to set.
   */
  public void setLastTalkedTo(String lastTalkedTo) {
    this.lastTalkedTo = lastTalkedTo;
  }

  /**
   * Updates the user to reflect joining a game
   */
  public void joinGame(PendingGame game) throws Exception {
    if (joinedGames.size() > 0) {
      throw new Exception("Error: You have already joined a game");
    }
    joinedGames.add(game);
    game.addPlayer(name);
  }

  /**
   * Updates the user information to start a joined game
   */
  public void startGame(PendingGame pending, ServerGame game) {
    currentGames.add(game.getGameNumber());
    joinedGames.remove(pending);
  }

  /**
   * Starts the game owned by the user.
   * 
   * @throws Exception
   *           if the user doesn't own any game
   */
  public void initiateGameStart() throws Exception {
    for (PendingGame g : joinedGames) {
      if (g.isOwner(name)) {
        g.start();
        break;
      }
    }
  }
}
