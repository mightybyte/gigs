package net.mightybyte.gigs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

import net.mightybyte.gigs.commands.Command;
import net.mightybyte.gigs.commands.CommandMap;
import net.mightybyte.gigs.game.ServerGame;

import org.apache.log4j.Logger;

/**
 * This class controls the I/O for a user. When the user enters a command this
 * thread processes that command.
 */
public class ClientConnection extends Thread {

  private static Logger logger = Logger.getLogger("gigs");
  private String prompt = "gigs% ";

  private ServerState serverState = ServerState.getInstance();

  private CommandMap commands = CommandMap.getInstance();

  private ConnectedUser connectedUser;

  private Socket socket;

  private PrintStream socketPrintStream;

  private BufferedReader socketReader;

  public ClientConnection(Socket skcConnection) {
    socket = skcConnection;
  }

  public void run() {
    try {
      socketPrintStream = new PrintStream(socket.getOutputStream());
      socketReader = new BufferedReader(new InputStreamReader(socket
          .getInputStream()));

      boolean done = !authenticate();

      while (!done) {
        // This line should probably read the prompt from the user's
        // preferences, but we can do that later.
        String input = readFromClient();
        logger.debug(connectedUser.getName()+": "+input);
        
        if (input == null || "quit".equals(input)) {
          done = true;
        } else {
          int firstSpace = input.indexOf(" ");
          String commandText;
          String params;
          if (firstSpace == -1) {
            commandText = input;
            params = "";
          } else {
            commandText = input.substring(0, firstSpace);
            params = input.substring(firstSpace + 1);
          }
          Command cmd = commands.getCommand(commandText);

          if (cmd == null) {
            List<Integer> curGames = connectedUser.getCurrentGames();

            if (curGames.size() == 1) {
              ServerGame g = serverState.getGame(curGames.get(0));
              if ( g.isWellFormedMove(input) ) {
                try {
                  g.makeMove(connectedUser.getName(), input);
                } catch (IllegalArgumentException e) {
                  writeToClientPrompt(e.getMessage());
                }
              } else {
                writeToClientPrompt(commandText+": command not found");
              }
            } else if (curGames.size() == 0) {
              writeToClientPrompt(commandText+": command not found");
            } else {
              // No support for playing multiple games at this time
              // This may never be used because multiple games may
              // be supported through another command.
            }

          } else {
            cmd.execute(this, params);
          }
        }
      }

      logger.debug(this.connectedUser.getName() + " logged out");
      socket.close();

    } catch (Exception e) {
      logger.error(e.getMessage());
      try {
        if ( socket != null ) {
          socket.close();
        }
      } catch (IOException f) {
      }
    }
    
    cleanupConnection();

  }

  /**
   * Executes the authentication exchange
   * 
   * @return True if the user authenticated, false otherwise
   */
  private boolean authenticate() {
    socketPrintStream.print("Enter name: ");
    String name = readFromClient();
    while ( serverState.getUsers().isConnected(name) ) {
      socketPrintStream.println("User already logged in");
      socketPrintStream.print("Enter name: ");
      name = readFromClient();
    }
    connectedUser = new ConnectedUser(name, this);
    serverState.getUsers().addUser(connectedUser);
    writeToClientPrompt(name + " logged in");
    logger.debug(name + " authenticated");
    return true;
  }

  private void cleanupConnection() {
    serverState.getUsers().removeUser(connectedUser.getName());
  }

  /**
   * Read a string from the client socket.
   * @return the string read
   */
  public String readFromClient() {
    String str = null;
    try {
      str = socketReader.readLine();
    } catch (IOException e) {
      logger.debug(e.getMessage());
    }
    return str;
  }

  /**
   * In the future this method needs to take into account the width variable of
   * the user and split the message into separate lines appropriately.
   * 
   * @param message
   */
  public void writeToClientPrompt(String message) {
    socketPrintStream.print("\n"+message + "\r\n");
    socketPrintStream.print(prompt);
  }

  public void writeToClient(String message) {
    socketPrintStream.print(message);
  }

  public void writelnToClient(String message) {
    socketPrintStream.print(message + "\r\n");
  }

  public ConnectedUser getConnectedUser() {
    return connectedUser;
  }

  public void setConnectedUser(ConnectedUser connectedUser) {
    this.connectedUser = connectedUser;
  }

}
