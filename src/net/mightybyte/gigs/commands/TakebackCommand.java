package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.ServerGame;

public class TakebackCommand implements Command {
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args) {
    ConnectedUser player = connection.getConnectedUser();

    if (player.getCurrentGames().size() == 0) {
      connection.writeToClientPrompt("You are not playing a game.");
      return;
    } else {
      // This assumes that the player is only playing one game
      ServerGame game = serverState.getGame(player.getCurrentGames().get(0));

      String takebackPlayer = game.getTakebackPlayer();
      int takebackCount = game.getTakebackCount();

      int turns;
      if ("".equals(args)) {
        turns = 1;
      } else {
        turns = Integer.parseInt(args);
      }

      if (game.getNumPlayers() == 2) {
        if (player.getName() == takebackPlayer && turns == takebackCount) {
          // Accept the takeback
          game.clearTakeback();
          game.unmakeMove(turns);
          return;
        }

        ConnectedUser otherPlayer = null;
        for (String p : game.getPlayers()) {
          if (p != player.getName()) {
            otherPlayer = serverState.getUsers().getUser(p);
            break;
          }
        }

        // Propose new takeback
        if (takebackPlayer == "") {
          int moveCount = game.getMoveCount();
          if (moveCount - turns < 0) {
            connection.writeToClientPrompt("No moves have been made.");
            return;
          }
          game.setTakeback(otherPlayer.getName(), turns);
          connection.writeToClientPrompt("Takeback request sent.");
          otherPlayer.getConnection().writelnToClient("");
          otherPlayer.getConnection().writeToClientPrompt(
              player.getName() + " would like to take back " + turns
                  + " move(s)");
        } else if (otherPlayer.getName() == takebackPlayer) {
          // Update existing takeback request
          game.setTakeback(otherPlayer.getName(), turns);
          connection.writeToClientPrompt("Updating existing takeback request.");
          otherPlayer.getConnection().writelnToClient("");
          otherPlayer.getConnection().writelnToClient(
              "Updated takeback request received.");
          otherPlayer.getConnection().writeToClientPrompt(
              player.getName() + " would like to take back " + turns
                  + " move(s)");
        } else {
          // Counter with new takeback request
          game.setTakeback(otherPlayer.getName(), turns);
          connection
              .writelnToClient("You disagree on the number of half-moves to take back.");
          connection.writeToClientPrompt("Alternate takeback request sent.");
          otherPlayer.getConnection().writelnToClient("");
          otherPlayer.getConnection().writeToClientPrompt(
              player.getName() + " proposes a different number (" + turns
                  + ") of move(s).");
        }
      }
    }
  }
}
