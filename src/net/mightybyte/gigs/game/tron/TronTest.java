package net.mightybyte.gigs.game.tron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TronTest {
  /**
   * @param args
   */
  public static void main(String[] args) {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    TronGame g = new TronGame();
    int turn = 0;

    g.init("empty-room");
    g.addPlayer("alice");
    g.addPlayer("bob");

    try {
      while (true) {
        String line = br.readLine();
        if (line.compareTo("quit") == 0) {
          break;
        } else if (line.compareTo("print") == 0) {
          System.out.println(g.getHumanReadableState(g.getPlayers().get(turn)));
        } else {
          if (g.isWellFormedMove(line)) {
            g.makeMove(g.getPlayers().get(turn), line);
            turn = 1 - turn;
            System.out.println(g
                .getHumanReadableState(g.getPlayers().get(turn)));
            System.out.println(g.getPlayers().get(turn) + "'s turn");
            
            if ( g.isGameOver() ) {
              System.out.println("Game over");
              System.out.println(g.getResultString());
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
