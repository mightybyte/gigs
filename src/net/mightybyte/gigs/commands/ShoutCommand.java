package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.CurrentUsers;
import net.mightybyte.gigs.ServerState;

public class ShoutCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    CurrentUsers currentUsers = serverState.getUsers();
    String username = connection.getConnectedUser().getName();

    int count = 0;
    for (ConnectedUser user : currentUsers) {
      if (username.equals(user.getName())) {
        user.getConnection().writeToClient(
            connection.getConnectedUser().getName() + " shouts: " + args);
      } else {
        user.getConnection()
            .writeToClientPrompt(
                "\n" + connection.getConnectedUser().getName() + " shouts: "
                    + args);
      }
      count++;
    }

    connection.writeToClientPrompt("(shouted to " + count + " players)");
  }

}
