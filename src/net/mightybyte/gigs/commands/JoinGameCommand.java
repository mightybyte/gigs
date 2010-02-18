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

public class JoinGameCommand implements Command
{

  public void execute(ClientConnection connection, String args)
  {
    ConnectedUser connectedUser = connection.getConnectedUser();
    int gameIndex = Integer.parseInt(args);

    try {
      connectedUser.joinGame(ServerState.getInstance().getPendingGame(gameIndex));
      
      connection.writeToClientPrompt("Joined game "+gameIndex);
    } catch (Exception e) {
      connection.writeToClientPrompt(e.getMessage());
    }
  }

}
