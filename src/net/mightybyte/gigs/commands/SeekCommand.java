package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;

public class SeekCommand implements Command {

  @Override
  public void execute(ClientConnection connection, String args) {
    String[] argArray = args.split(" +");
    String gameType;
    String gameArgs;

    if ("".compareTo(args) == 0) {
      gameType = "tron";
      gameArgs = "random";
    } else {
      if (argArray.length < 2) {
        connection.writeToClientPrompt("Bad seek command");
        return;
      }
      gameType = argArray[0];
      gameArgs = argArray[1];
    }
    
    ConnectedUser originatingPlayer = connection.getConnectedUser();
    if (originatingPlayer.getCurrentGames().size() > 0) {
      connection.writeToClientPrompt("Already playing a game.");
      return;
    }
  }

}
