package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;

public class SeekCommand implements Command {

  @Override
  public void execute(ClientConnection connection, String args) {
    String[] argArray = args.split(" +");
    if (argArray.length < 3) {
      connection.writeToClientPrompt("Bad seek command");
      return;
    }

    ConnectedUser originatingPlayer = connection.getConnectedUser();
    if (originatingPlayer.getCurrentGames().size() > 0) {
      connection.writeToClientPrompt("Already playing a game.");
      return;
    }

    
  }

}
