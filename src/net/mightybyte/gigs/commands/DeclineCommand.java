package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.PendingGame;

public class DeclineCommand implements Command {
  ServerState serverState = ServerState.getInstance();

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
          + " to decline.");
      connection
          .writeToClientPrompt("Type \"pending\" to see the list of offers.");
      return;
    }

    games.remove(game);
    for ( String p : game.getPlayers() ) {
      serverState.getUsers().getUser(p).getConnection().writeToClientPrompt(player.getName()+" has declined your offer.");
      serverState.getUsers().getUser(p).getOutgoingGames().remove(game);
      serverState.getUsers().getUser(p).getIncomingGames().remove(game);
    }
    
    connection.writeToClientPrompt("You declined the offer.");
  }

}
