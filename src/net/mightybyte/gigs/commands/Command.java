package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;

public interface Command {
  public void execute(ClientConnection connection, String args);
}
