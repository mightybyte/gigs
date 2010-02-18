/*
 * Unclassified              U N C L A S S I F I E D       Unclassified
 *
 * Copyright (c) 2006 Northrop Grumman, Inc. All rights reserved.
 *
 * Change Date     Change Description                      Programmer
 * -----------     ------------------                      -------------
 * Apr 13, 2006         Initial test release                    D. Beardsley
 * ---------------------------------------------------------------------
 */
package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;
import net.mightybyte.gigs.ConnectedUser;
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.PendingGame;

public class CreateGameCommand implements Command {
  ServerState serverState = ServerState.getInstance();

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

    String param = argArray.length >= 4 ? argArray[3] : "";
    
    // This is a bad way to set the max players. It is temporary.
    PendingGame pendingGame = new PendingGame(argArray[0], param,
        originatingPlayer.getName(), 10, Integer.parseInt(argArray[1]), Integer
            .parseInt(argArray[2]));

    int gameIndex = serverState.addPendingGame(pendingGame);

    try {
      originatingPlayer.joinGame(pendingGame);
    } catch (Exception e) {
      connection.writeToClientPrompt(e.getMessage());
    }

    originatingPlayer.getConnection().writeToClientPrompt(
        "Creating new " + argArray[0] + " game " + gameIndex);
  }

}
