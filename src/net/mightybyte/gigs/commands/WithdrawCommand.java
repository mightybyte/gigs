package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.PendingGame;

public class WithdrawCommand implements Command {
  ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    ConnectedUser player = connection.getConnectedUser();
    List<PendingGame> games = player.getOutgoingGames();

    if ( games.size() == 0 ) {
      connection.writeToClientPrompt("There are no offers to withdraw.");
      return;
    }

    
    PendingGame game = null;

    if (!"".equals(args)) {
      //Argument specified
      for (PendingGame g : games) {
        if (g.getPlayers().contains(args)) {
          game = g;
        }
      }
      if ( game == null ) {
        connection.writelnToClient("There no offer with that number.");
        return;
      }
    } else {
      //No argument
      if ( games.size() > 1 ) {
        connection.writelnToClient("There is more than one pending offer.");
        connection.writelnToClient("Type \"pending\" to see the list of offers.");
        connection.writelnToClient("Type \"withdraw <number>\" to withdraw an offer.");
        return;
      }
      
      game = games.get(0);
    }
    
    for ( String p : game.getPlayers() ) {
      serverState.getUsers().getUser(p).getOutgoingGames().remove(game);
      serverState.getUsers().getUser(p).getIncomingGames().remove(game);
    }

  }

}
