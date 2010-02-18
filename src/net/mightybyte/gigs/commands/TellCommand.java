package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;

public class TellCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    int firstSpace = args.indexOf(" ");
    
    if ( firstSpace == -1 ) {
      connection.writeToClientPrompt("Must specify a user and a message");
      return;
    }
    
    String target = args.substring(0, firstSpace);
    String message = args.substring(firstSpace + 1);

    ConnectedUser user = serverState.getUsers().getUser(target);
    if (user != null) {
      // user.getConnection().writelnToClient("");
      user.getConnection().writelnToClient("");
      user.getConnection().writeToClientPrompt(
          connection.getConnectedUser().getName() + " tells you: " + message);
      connection.writeToClientPrompt("(told " + target + ")");
      connection.getConnectedUser().setLastTalkedTo(target);
      // } else {
      // connection.writeToClientPrompt(target+" is not logged in.");
    } else {
      connection.writeToClientPrompt(target+" not logged in.");
    }
  }

}
