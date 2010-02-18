package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class KibitzCommand implements Command {
  public void execute(ClientConnection connection, String args) {
    ConnectedUser user = connection.getConnectedUser();
    ServerState serverState = ServerState.getInstance();

    int primaryGame = -1;
    List<Integer> games = user.getCurrentGames();
    if (games.size() > 0) {
      primaryGame = games.get(0);
    } else {
      games = user.getObservedGames();
      if (games.size() > 0) {
        primaryGame = games.get(0);
      }
    }

    if (primaryGame != -1) {
      ServerGame game = serverState.getGame(primaryGame);
      game.writeToAllPlayers(user.getName() + "[" + primaryGame
          + "] kibitzes: " + args);
      game.writeToAllObservers(user.getName() + "[" + primaryGame
          + "] kibitzes: " + args);
      int count = game.getObservers().size()+game.getNumPlayers();
      user.getConnection().writelnToClient(
          "(kibitzed to " + count + " players)");
    } else {
      user.getConnection().writeToClientPrompt("You are not playing or observing a game");
    }
  }

}
