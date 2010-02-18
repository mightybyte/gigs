package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class ListGamesCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    int count = 0;
    for (ServerGame g : serverState.getCurrentGames()) {
      if (g != null) {
        connection.writelnToClient(g.getGameString());
        count++;
      }
    }
    connection.writelnToClient("");
    connection.writeToClientPrompt(count + " games displayed.");
  }

}
