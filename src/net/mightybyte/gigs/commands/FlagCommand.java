package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class FlagCommand implements Command {

  public void execute(ClientConnection connection, String args) {
    ConnectedUser user = connection.getConnectedUser();
    List<Integer> games = user.getCurrentGames();
    if ( games.size() == 1 ) {
      ServerGame g = ServerState.getInstance().getGame(games.get(0));
      if ( g.getNumPlayers() == 2 ) {
        String player = "";
        for ( String s : g.getPlayers() ) {
          if ( s != user.getName() ) {
            player = s;
            break;
          }
        }
        if ( g.isOutOfTime(player) ) {
          g.writeToAllPlayers(player+" forfeited on time.");
          ServerState.getInstance().removeGame(g.getGameNumber());
        } else {
          connection.writeToClientPrompt(player+" is not out of time.");
        }
      }
    }

  }

}
