package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class UnobserveCommand implements Command
{
  private static ServerState serverState = ServerState.getInstance();
  
  public void execute(ClientConnection connection, String args)
  {
    ConnectedUser player = connection.getConnectedUser();
    int gameID = Integer.parseInt(args);
    ServerGame game = serverState.getGame(gameID);

    player.getObservedGames().remove(new Integer(gameID));
    game.getObservers().remove(player.getName());
  }

}
