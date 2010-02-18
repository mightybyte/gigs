package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.PendingGame;

public class AcceptCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    ConnectedUser player = connection.getConnectedUser();
    List<PendingGame> games = player.getIncomingGames();

    PendingGame game = null;

    if (!"".equals(args)) {
      for (PendingGame g : games) {
        if (g.getPlayers().contains(args)) {
          game = g;
        }
      }
    } else if (games.size() == 1) {
      game = games.get(0);
    }

    if (game == null) {
      connection.writelnToClient("There are no offers from " + args
          + " to accept.");
      connection
          .writeToClientPrompt("Type \"pending\" to see the list of offers.");
      return;
    }

    try {
      player.joinGame(game);
    } catch (Exception e) {
      connection.writeToClientPrompt("Exception: "+e.getMessage());
    }
    
    if (game.isFull()) {
      // The game is full, so we will automatically start it
      game.start();
    }
  }
}
