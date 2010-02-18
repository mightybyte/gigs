package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;

public class WhoCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    connection.writelnToClient("");

    int count = 0;
    for (ConnectedUser user : serverState.getUsers()) {
      connection.writelnToClient(user.getName());
      count++;
    }

    connection.writeToClientPrompt(count + " users displayed.");
  }
}
