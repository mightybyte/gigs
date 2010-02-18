package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class RefreshCommand implements Command {

  public void execute(ClientConnection connection, String args) {
    ConnectedUser user = connection.getConnectedUser();
    List<Integer> games = user.getCurrentGames();
    if ( games.size() == 1 ) {
      ServerGame g = ServerState.getInstance().getGame(games.get(0));
      g.writeStateToPlayer(user.getName());
    }
  }

}
