package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;

public class RetellCommand implements Command {

  public void execute(ClientConnection connection, String args) {
    TellCommand cmd = new TellCommand();

    if ("".equals(connection.getConnectedUser().getLastTalkedTo())) {
      connection.writeToClientPrompt("I don't know who to tell that to.");
    } else {
      cmd.execute(connection, connection.getConnectedUser().getLastTalkedTo()
          + " " + args.trim());
    }
  }

}
