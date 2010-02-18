package net.mightybyte.gigs.commands;

import java.util.List;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.game.PendingGame;

public class PendingCommand implements Command {

  public void execute(ClientConnection connection, String args) {
    ConnectedUser user = connection.getConnectedUser();

    connection.writelnToClient("");
    
    List<PendingGame> games = user.getOutgoingGames();
    if (games.size() == 0) {
      connection.writelnToClient("There are no offers pending to other players");
    } else {
      connection.writelnToClient("Offers to other players:");
      for ( int i = 0; i < games.size(); i++ ) {
        connection.writelnToClient("  "+i+": "+games.get(i).toString());
      }
      connection.writelnToClient("If you wish to withdraw any of these offers, type \"withdraw <number>\"");
    }

    connection.writelnToClient("");
    
    games = user.getIncomingGames();
    if (games.size() == 0) {
      connection.writelnToClient("There are no offers pending from other players");
    } else {
      connection.writelnToClient("Offers from other players:");
      for ( int i = 0; i < games.size(); i++ ) {
        connection.writelnToClient("  "+i+": "+games.get(i).toString());
      }
      connection.writelnToClient("If you wish to accept any of these offers type \"accept <number>\"");
      connection.writelnToClient("If you wish to decline any of these offers, type \"decline <number>\"");
    }
    connection.writelnToClient("");
    connection.writeToClientPrompt("");
  }
}
