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
import net.mightybyte.gigs.ServerState;
import net.mightybyte.gigs.game.PendingGame;

public class ListOpenGamesCommand implements Command
{
  private static ServerState serverState = ServerState.getInstance();

  public void execute(ClientConnection connection, String args)
  {
    StringBuilder sb = new StringBuilder();
    int count = 0;
    for (PendingGame g : serverState.getPendingGames()) {
      sb.setLength(0);
      if (g != null) {
        sb.append(count);
        sb.append(" (");
        sb.append(g.getGameType());
        sb.append("): ");
        for ( String name : g.getPlayers() ) {
          sb.append(name+" ");
        }
        count++;
        connection.writelnToClient(sb.toString());
      }
    }
    connection.writelnToClient("");
    connection.writeToClientPrompt(count + " games displayed.");
  }

}
