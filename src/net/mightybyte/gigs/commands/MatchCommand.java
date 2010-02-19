package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.PendingGame;

public class MatchCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    String[] argArray = args.split(" +");
    if (argArray.length < 3) {
      connection.writeToClientPrompt("Bad match command");
      return;
    }

    ConnectedUser originatingPlayer = connection.getConnectedUser();
    if (originatingPlayer.getCurrentGames().size() > 0) {
      connection.writeToClientPrompt("Already playing a game.");
      return;
    }

    ConnectedUser terminatingPlayer = serverState.getUsers().getUser(
        argArray[0]);
    if (terminatingPlayer == null) {
      connection.writeToClientPrompt(argArray[0] + " is not logged in.");
      return;
    }

    try {
      String type = "tron";
      String param = "empty-room";

      if (argArray.length >= 4) {
        type = argArray[3];
      }
      if (argArray.length >= 5) {
        param = argArray[4];
      }

      // TODO This is a bad way to set the max players. It is temporary.
      PendingGame pendingGame = new PendingGame(type, param, originatingPlayer
          .getName(), 2, Integer.parseInt(argArray[1]), Integer
          .parseInt(argArray[2]));
      serverState.addPendingGame(pendingGame);
      originatingPlayer.addOutgoingGame(pendingGame);
      terminatingPlayer.addIncomingGame(pendingGame);

      originatingPlayer.getConnection().writeToClientPrompt(
          "Issuing " + pendingGame.toString());
      terminatingPlayer.getConnection().writelnToClient("");
      terminatingPlayer.getConnection().writelnToClient(
          "Challenge: " + originatingPlayer.getName() + " vs. "
              + terminatingPlayer.getName() + " "
              + pendingGame.getTimeBase() + " "
              + pendingGame.getTimeInc() + " "
              + pendingGame.getGameType() + " " 
              + param);
      terminatingPlayer
          .getConnection()
          .writeToClientPrompt(
              "You can \"accept\" or \"decline\", or propose different parameters.");
    } catch (Exception e) {
      originatingPlayer.getConnection().writeToClientPrompt(e.getMessage());
    }
  }
}
