package net.mightybyte.gigs.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.CurrentUsers;
import net.mightybyte.gigs.ServerState;

/**
 * Maybe this class should be named ServerGame
 */
public class ServerGame {
  private static ServerState serverState = ServerState.getInstance();

  private List<Game> gameHistory;

  private String gameType;

  private int gameNumber;

  private Game game;

  private Map<String, PlayerInfo> playerInfo;
  private List<String> observers;

  private int currentTurn;

  private long timeBase;

  private long timeInc;

  private String takebackPlayer;

  private int takebackCount;

  /**
   * 
   * @param pendingGame
   */
  public ServerGame(PendingGame pendingGame) {
    gameHistory = new ArrayList<Game>();
    playerInfo = new HashMap<String, PlayerInfo>();
    observers = new ArrayList<String>();
    timeBase = pendingGame.getTimeBase() * 60 * 1000;
    timeInc = pendingGame.getTimeInc();

    this.gameType = pendingGame.getGameType();
    game = serverState.gameFactory.createGame(pendingGame);

    for (String player : game.getPlayers()) {
      addPlayer(player);
    }

    clearTakeback();
  }

  /**
   * 
   */
  public void start() {
    long stime = System.currentTimeMillis();
    for (PlayerInfo info : playerInfo.values()) {
      info.timeLeft = timeBase;
      info.curTurnStartTime = stime;
    }

    currentTurn = 0;
    game.start();
    writeStateToAllPlayers();
  }

  /**
   * Add a player to this game. 
   * @param player
   */
  public void addPlayer(String player) {
    PlayerInfo info = new PlayerInfo();
    info.player = player;
    info.timeLeft = timeBase;
    playerInfo.put(player, info);
    game.addPlayer(player);
  }
  
  /**
   * Add an observer to this game.
   * @param player
   */
  public void addObserver(String player) {
    observers.add(player);
  }
  
  /**
   * Get the observers.
   * @return the observers.
   */
  public List<String> getObservers() {
    return observers;
  }

  /**
   * 
   * @return the players
   */
  public List<String> getPlayers() {
    return game.getPlayers();
  }

  /**
   * 
   * @return the number of players
   */
  public int getNumPlayers() {
    return game.getNumPlayers();
  }

  /**
   * 
   * @return the game string
   */
  public String getGameString() {
    return "Game " + gameNumber + " (" + game.getPlayerString() + ")";
  }

  /**
   * Return the game over flag
   * 
   * @return the game over flag
   */
  public boolean isGameOver() {
    return game.isGameOver();
  }

  /**
   * Checks ot see if the move is well-formed.
   * @param move the move
   * @return true if the move is well-formed, false otherwise.
   */
  public boolean isWellFormedMove(String move) {
    return game.isWellFormedMove(move);
  }
  
  /**
   * Makes the specified move for the specified player. If the move is illegal
   * then an IllegalArgumentException is thrown. If the move is legal, it is
   * made and the new game state is sent to all players.
   * 
   * @param player
   *          The player making the mvoe
   * @param move
   *          The move string
   * @throws IllegalArgumentException
   */
  public void makeMove(String player, String move)
      throws IllegalArgumentException {
    List<String> playersOnMove = game.getPlayersOnMove();

    // Subtract time from the players who moved
    long curTime = System.currentTimeMillis();
    for (String playerToMove : playersOnMove) {
      PlayerInfo info = playerInfo.get(playerToMove);
      info.timeLeft -= curTime - info.curTurnStartTime;
    }

    Game g = game.copy();
    boolean newMove = g.makeMove(player, move);
    game = g;
    gameHistory.add(currentTurn, g);
    if (newMove) {
      currentTurn++;
    }
    System.out.println(player+" "+move);
    System.out.println(game.getCompleteState());

    // Set turn start time for players who are now on move
    playersOnMove = game.getPlayersOnMove();
    for (String nextPlayer : playersOnMove) {
      PlayerInfo info = playerInfo.get(nextPlayer);
      info.curTurnStartTime = System.currentTimeMillis();
    }

    writeStateToAllPlayers();
    if (game.isGameOver()) {
      writeToAllPlayers("{" + getGameString() + " " + game.getResultString()
          + "}");
      serverState.removeGame(gameNumber);
    }
  }

  /**
   * This method unmakes the specified number of moves and prints the new board
   * to all players.
   * 
   * @param count
   *          The number of moves to unmake
   */
  public void unmakeMove(int count) {
    for (int i = 0; i < count; i++) {
      currentTurn--;
      gameHistory.remove(currentTurn);
    }
    game = gameHistory.get(currentTurn - 1).copy();
    writeStateToAllPlayers();
  }

  /**
   * Returns the number of turns played.
   * 
   * @return the number of turns
   */
  public int getMoveCount() {
    return this.currentTurn;
  }

  /**
   * Returns the number of moves requested in the current takeback request or
   * zero if there is no pending takeback request.
   * 
   * @return Returns the takebackCount.
   */
  public int getTakebackCount() {
    return takebackCount;
  }

  /**
   * The name of the player who the takeback request was offered to.
   * 
   * @return Returns the takebackPlayer.
   */
  public String getTakebackPlayer() {
    return takebackPlayer;
  }

  /**
   * Sets the takeback player and number of turns.
   * 
   * @param player
   * @param turns
   */
  public void setTakeback(String player, int turns) {
    takebackPlayer = player;
    takebackCount = turns;
  }

  /**
   * Clears the current takeback player and number of turns.
   */
  public void clearTakeback() {
    takebackPlayer = "";
    takebackCount = 0;
  }

  /**
   * Test to see whether the specified player is out of time.
   * 
   * @param player
   *          the player to check
   * @return flag indicating whether the player has run out of time
   */
  public boolean isOutOfTime(String player) {
    PlayerInfo info = playerInfo.get(player);
    long timeLeft = info.timeLeft;
    if (game.getPlayersOnMove().contains(player)) {
      timeLeft -= System.currentTimeMillis() - info.curTurnStartTime;
    }
    return timeLeft < 0;
  }

  /**
   * 
   */
  public void writeStateToAllPlayers() {
    for (String s : game.getPlayers()) {
      writeStateToPlayer(s);
    }
    for (String s : observers) {
      writeStateToPlayer(s);
    }
  }

  /**
   * 
   */
  public void writeStateToPlayer(String player) {
    CurrentUsers users = serverState.getUsers();
    List<String> onMove = game.getPlayersOnMove();

    long currentTime = System.currentTimeMillis();
    ClientConnection userCon = users.getUser(player).getConnection();
    userCon.writelnToClient("\n"+game.getHumanReadableState(player));
    for (String s2 : game.getPlayers()) {
      PlayerInfo info = playerInfo.get(s2);
      long t = info.timeLeft;
      if (onMove.contains(s2)) {
        t -= currentTime - info.curTurnStartTime;
      }
      long min = t / 1000 / 60;
      long sec = t / 1000 - min * 60;
      if ( sec < 0 ) {
        min = -min;
        sec = -sec;
        userCon.writeToClient(s2 + " clock: (" + info.timeLeft + ") -" + min);
      } else {
        userCon.writeToClient(s2 + " clock: (" + info.timeLeft + ") " + min);
      }
      if (sec >= 10) {
        userCon.writelnToClient(":" + sec);
      } else {
        userCon.writelnToClient(":0" + sec);
      }
    }
    userCon.writeToClientPrompt("");
  }

  /**
   * 
   * @param str
   */
  public void writeToAllPlayers(String str) {
    CurrentUsers users = serverState.getUsers();
    for (String s : game.getPlayers()) {
      users.getUser(s).getConnection().writeToClientPrompt(str);
    }
  }

  /**
   * 
   * @param str
   */
  public void writeToAllObservers(String str) {
    CurrentUsers users = serverState.getUsers();
    for (String s : observers) {
      users.getUser(s).getConnection().writeToClientPrompt(str);
    }
  }
  
  private static class PlayerInfo {
    public String player;

    public long timeLeft;

    public long curTurnStartTime;
  }

  public int getGameNumber() {
    return gameNumber;
  }
}
