package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class ObserveCommand implements Command
{
  private static ServerState serverState = ServerState.getInstance();
  
  public void execute(ClientConnection connection, String args)
  {
    ConnectedUser player = connection.getConnectedUser();
    try {
      int gameID = Integer.parseInt(args);
      ServerGame game = serverState.getGame(gameID);

      player.addObservedGame(gameID);
      game.addObserver(player.getName());
    } catch ( NumberFormatException e ) {
      player.getConnection().writeToClientPrompt("No such game");
    }
  }

}
