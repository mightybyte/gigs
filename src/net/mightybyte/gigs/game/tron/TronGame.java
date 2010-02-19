package net.mightybyte.gigs.game.tron;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.mightybyte.gigs.game.Game;

public class TronGame implements Game {
  /**
   * The max number of players for this game
   */
  public static final int MAX_PLAYERS = 2;

  protected List<String> players;
  protected List<String> moves;
  protected boolean[] isAlive;
  protected int[] playerPositions;
  protected int[] nextMove;
  protected int width;
  protected int height;

  protected boolean[] isWall;

  public TronGame() {
    players = new ArrayList<String>();
    moves = new ArrayList<String>();
    playerPositions = new int[MAX_PLAYERS];
    nextMove = new int[MAX_PLAYERS];
    isAlive = new boolean[MAX_PLAYERS];
    Arrays.fill(isAlive, true);
  }

  public void init(String params) {
    loadMap(params);

    // For later
    // if (!params.matches("\\d+x\\d+")) {
    // loadMap(params + ".tronmap");
    // } else {
    // String[] dims = params.split("x");
    // width = Integer.parseInt(dims[0]);
    // height = Integer.parseInt(dims[0]);
    // isWall = new boolean[width * height];
    // clear();
    // }
  }

  protected void loadMap(String map) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("maps/" + map + ".tronmap")));
      String dims[] = br.readLine().split(" ");
      width = Integer.parseInt(dims[0]);
      height = Integer.parseInt(dims[1]);
      isWall = new boolean[width * height];
      clear();

      int ind = 0;
      for (int i = 0; i < height; i++) {
        byte row[] = br.readLine().getBytes();
        if (row.length != width) {
          throw new IllegalArgumentException("The map '" + map
              + "' is corrupt.  Please notify the administrator.");
        }
        for (int j = 0; j < row.length; j++) {
          if (row[j] == '#') {
            isWall[ind++] = true;
          } else if (row[j] == ' ') {
            isWall[ind++] = false;
          } else if (Character.isDigit(row[j]) && row[j] - '1' >= 0
              && row[j] - '1' < MAX_PLAYERS) {
            playerPositions[row[j] - '1'] = ind++;
          } else {
            throw new IOException();
          }
        }
      }
      br.close();
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("Map not found: '" + map + "'");
    } catch (IOException e) {
      throw new IllegalArgumentException("The map '" + map
          + "' is corrupt.  Please notify the administrator.");
    }

  }

  protected void clear() {
    for (int i = 0; i < MAX_PLAYERS; i++) {
      nextMove[i] = -1;
    }
    players.clear();
    moves.clear();
    for (int i = 0; i < width * height; i++) {
      isWall[i] = false;
    }
  }

  public void start() {
  }

  public boolean addPlayer(String player) {
    boolean flag = false;
    if (players.size() < MAX_PLAYERS) {
      players.add(player);
      flag = true;
    }

    return flag;
  }

  public List<String> getPlayers() {
    return this.players;
  }

  /**
   * Return the number of players in this game.
   */
  public int getNumPlayers() {
    return players.size();
  }

  /**
   * Returns true if the first character of the move is a letter and the
   * substring starting on the second character is a number.
   * 
   * @return true if the move looks like a pente move, false otherwise
   */
  public boolean isWellFormedMove(String move) {
    return move.matches("[1-4]");
  }

  /**
   * Make a move without setting a list of the next players' turn.
   * 
   * @param player
   * @param move
   * @throws IllegalArgumentException
   */
  public boolean makeMove(String player, String move)
      throws IllegalArgumentException {
    int playerNum = players.indexOf(player);

    // Check that a valid player string was supplied and
    // that it is not the opponent's turn.
    if (playerNum == -1) {
      throw new IllegalArgumentException(
          "Illegal move (this should not happen)");
    }

    if (nextMove[playerNum] != -1) {
      throw new IllegalArgumentException("You already moved this turn.");
    }

    int pos = playerPositions[playerNum];

    if (move.compareTo("1") == 0 && pos >= width) {
      nextMove[playerNum] = pos - width;
    } else if (move.compareTo("2") == 0 && pos % width < width - 1) {
      nextMove[playerNum] = pos + 1;
    } else if (move.compareTo("3") == 0 && pos + width < width * height) {
      nextMove[playerNum] = pos + width;
    } else if (move.compareTo("4") == 0 && pos % width > 0) {
      nextMove[playerNum] = pos - 1;
    } else {
      throw new IllegalArgumentException("Illegal move");
    }

    for (int i = 0; i < MAX_PLAYERS; i++) {
      if (nextMove[i] == -1) {
        // Still some players left to move
        return false;
      }
    }

    // If we get here, then all the players have made their moves and we can
    // update the board state.

    StringBuilder moveString = new StringBuilder();
    for (int i = 0; i < MAX_PLAYERS; i++) {
      moveString.append(Integer.toString(nextMove[i]) + " ");
      isWall[playerPositions[i]] = true;
      playerPositions[i] = nextMove[i];
      nextMove[i] = -1;
    }

    moves.add(moveString.toString());
    return true;
  }

  protected boolean hasCollided(int player) {
    for (int j = 0; j < MAX_PLAYERS; j++) {
      if (player != j && playerPositions[player] == playerPositions[j]) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Get a list containing the name of the player who is to move.
   * 
   * @return List containing the name of the player to move
   */
  public List<String> getPlayersOnMove() {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < MAX_PLAYERS; i++) {
      if (nextMove[i] == -1 && isAlive[i]) {
        list.add(players.get(i));
      }
    }
    return list;
  }

  public boolean isGameOver() {
    int alive = 0;
    for (int i = 0; i < MAX_PLAYERS; i++) {
      if (isAlive[i] && !isWall[playerPositions[i]] && !hasCollided(i)) {
        alive++;
      }
    }

    return alive < 2;
  }

  public double getPlayerScore(String player) {
    int playerNum = players.indexOf(player);

    if (playerNum == -1) {
      throw new IllegalArgumentException(player + " not playing this game");
    }

    return 0;
  }

  public String getPlayerString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < players.size(); i++) {
      if (i != 0) {
        sb.append(" vs. ");
      }
      sb.append(players.get(i));
    }
    return sb.toString();
  }

  public String getResultString() {
    if (this.isGameOver()) {
      int winner = -1;
      int count = 0;
      
      for (int i = 0; i < players.size(); i++) {
        if ( isAlive[i] && !isWall[playerPositions[i]] && !hasCollided(i) ) {
          winner = i;
          count++;
        }
      }
      
      if ( count == 1 ) {
        return players.get(winner)+" wins";
      } else if ( count == 0 ) {
        return "draw";
      }
    }
    return "in progress";
  }

  public String getHumanReadableState(String player) {
    StringBuilder out = new StringBuilder();
    out.append(width);
    out.append(' ');
    out.append(height);
    out.append("\r\n");
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int pos = i * width + j;
        if (isWall[pos]) {
          out.append('#');
        } else {
          int k;
          for (k = 0; k < MAX_PLAYERS; k++) {
            if (pos == playerPositions[k]) {
              break;
            }
          }
          if (k < MAX_PLAYERS) {
            out.append(k + 1);
          } else {
            out.append(' ');
          }
        }
      }
      out.append("\r\n");
    }
    return out.toString();
  }

  public String getMachineReadableState(String player) {
    return getHumanReadableState(player);
  }

  public String getCompleteState() {
    StringBuilder sb = new StringBuilder();
    sb.append(getMachineReadableState(players.get(0)));
    sb.append("\n");
    sb.append(players.toString());
    sb.append("\n");
    sb.append(moves.toString());
    sb.append("\n");
    sb.append(nextMove.toString());
    return sb.toString();
  }

  /**
   * Creates and returns a deep copy of the current object.
   * 
   * @return the deep copy
   */
  public Game copy() {
    TronGame g = new TronGame();
    g.width = width;
    g.height = height;
    for (String s : players) {
      g.players.add(s);
    }
    for (String s : moves) {
      g.moves.add(s);
    }
    System.arraycopy(playerPositions, 0, g.playerPositions, 0,
        playerPositions.length);
    System.arraycopy(nextMove, 0, g.nextMove, 0, nextMove.length);
    g.isWall = new boolean[width * height];
    System.arraycopy(isWall, 0, g.isWall, 0, isWall.length);

    return g;
  }

}
