package net.mightybyte.gigs.commands;

import net.mightybyte.gigs.ClientConnection;

/**
 * 
 */
public class ReloadCommandsCommand implements Command {

  /*
   * (non-Javadoc)
   * 
   * @see net.mightybyte.pente.commands.Command#execute(net.mightybyte.pente.ClientConnection,
   *      java.lang.String)
   */
  public void execute(ClientConnection connection, String args) {
    connection.writeToClientPrompt("Reloading commands");
    CommandMap.getInstance().loadCommands();
  }

}
